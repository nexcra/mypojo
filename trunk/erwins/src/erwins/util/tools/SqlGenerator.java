package erwins.util.tools;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import erwins.util.lib.Strings;
import erwins.util.lib.file.Files;


/**
 * Oracle SQL을 생성해 보아요~ 노가다는 싫어.
 */
public class SqlGenerator{	
    
    /**
     * @uml.property  name="jdbc"
     * @uml.associationEnd  
     */
    JDBC jdbc;
    private static final String[] TABLE_NAMES = {"TABLE_NAME","COMMENTS"};
    private List<Mapp> tableInfos = new ArrayList<Mapp>();
    private static final String[] COLUMN_NAMES = {"COLUMN_NAME","KEY"};
    private List<Mapp> columnInfos = new ArrayList<Mapp>();
    
    public SqlGenerator(JDBC jdbc){
        this.jdbc = jdbc;
        initUserInfo();
    }
    
    /** 로컬DB를 RemoteDB와 동기화 시키는 merge DML을 생성한다. */
    public void getMergeDML(String remoteName,String fileName){
        DmlTemplit dml = new MergeDML(remoteName);
        dml.getDmlToFile(fileName);
    }
    
    public void getInsertDML(String fileName){
        DmlTemplit dml = new InsertDML();
        dml.getDmlToFile(fileName);
    }
    
    public void getUpdateDML(String fileName){
        DmlTemplit dml = new UpdateDML();
        dml.getDmlToFile(fileName);
    }
    
        
    
    private void initUserInfo(){
        StringBuilder sql = new StringBuilder();
        sql.append(" select a.TABLE_NAME,b.COMMENTS from USER_tables a, USER_TAB_COMMENTS b");
        sql.append("  where a.TABLE_NAME = b.TABLE_NAME ");
        sql.append("  order by  TABLE_NAME ");
        tableInfos = jdbc.select(sql.toString(),TABLE_NAMES);
        
        List<Mapp> newList = new ArrayList<Mapp>(); 
        for(Mapp map : tableInfos){
            if(!Strings.isMatch(map.getStr(TABLE_NAMES[0]),"$")) newList.add(map);
        }
        tableInfos = newList;
        
    }
    
    /** DD를 조회해서 컬럼과 PK정보를 읽어온다. */
    private void initColumnInfo(String tableName){
        StringBuilder sql = new StringBuilder();
        sql.append(" select "+COLUMN_NAMES[0]+",");
        sql.append("  (SELECT B.COLUMN_NAME");
        sql.append("  FROM   USER_CONSTRAINTS A,");
        sql.append("  USER_CONS_COLUMNS B");
        sql.append("  WHERE  A.CONSTRAINT_NAME = B.CONSTRAINT_NAME");
        sql.append("  AND    A.TABLE_NAME = '"+tableName+"'");
        sql.append("  AND    A.CONSTRAINT_TYPE = 'P'");
        sql.append("  and    COLUMN_NAME = m.COLUMN_NAME)\""+COLUMN_NAMES[1]+"\"");
        sql.append(" from   user_tab_columns m");
        sql.append(" where  table_name = '"+tableName+"'");
        columnInfos = jdbc.select(sql.toString(),COLUMN_NAMES);
    }  
    
    /** PK인지? */
    private boolean isKey(Mapp map){
        return !map.isEmpty(COLUMN_NAMES[1]);
    }
    /** 첫번째 인자 색출 */
    private String value(Mapp map){
        return map.getStr(COLUMN_NAMES[0]);
    }
    
    
    /**
     * 각 Table별 DML을 출력하는 템플릿이다. 
     */
    private abstract class DmlTemplit{
        protected StringBuilder dml;
        public void getDmlToFile(String fileName){
            StringBuilder result = new StringBuilder();
            for(Mapp map : tableInfos){
                String tableName = map.getStr(TABLE_NAMES[0]).toUpperCase();
                initColumnInfo(tableName);
                result.append("---------------  ");
                result.append(map.getStr(TABLE_NAMES[0]));
                if(!map.isEmpty(TABLE_NAMES[1])) result.append("("+map.getStr(TABLE_NAMES[1])+")");
                result.append("   ---------------\n");
                result.append(getDML(map.getStr(TABLE_NAMES[0])) + "; \n");
            }
            result.append(getEtc());
            Files.writeStr(result.toString(), new File(fileName),"UTF-8");
        }
        
        protected abstract String getDML(String tableName);
        
        /**
         * 기타 추가할 자료를 입력해 준다. 
         */
        protected String getEtc(){return "";};
    }
    
    private class InsertDML extends DmlTemplit{

        @Override
        protected String getDML(String tableName) {
            dml = new StringBuilder();
        
            dml.append(" insert into ");
            dml.append(tableName);
            
            set();
            values();

            return dml.toString();
        }

        private void set() {
            boolean first = true;
            dml.append(" ( ");
            for(Mapp map : columnInfos){
                if(first) first = false;
                else  dml.append(" , ");
                dml.append(value(map));
            }
            dml.append(" ) \n");
        }
        private void values() {
            boolean first = true;
            dml.append(" values ( ");
            for(Mapp map : columnInfos){
                if(first) first = false;
                else  dml.append(" , ");
                dml.append("#");
                dml.append(Strings.getCamelize(value(map)));
                dml.append("#");
            }
            dml.append(" )");
        }
        
    }
    
    private class UpdateDML extends DmlTemplit{

        @Override
        protected String getDML(String tableName) {
            dml = new StringBuilder();
        
            dml.append(" update ");
            dml.append(tableName);
            
            set();
            dml.append(" where ... ");

            return dml.toString();
        }

        private void set() {
            boolean first = true;
            dml.append(" set ");
            for(Mapp map : columnInfos){
                if(first) first = false;
                else  dml.append(" , ");
                dml.append(value(map));
                dml.append("=");
                dml.append("#");
                dml.append(Strings.getCamelize(value(map)));
                dml.append("#");
            }
            dml.append(" \n");
        }
        
    }    
    
    private class MergeDML extends DmlTemplit{
        String remoteName;
        public MergeDML(String remoteName){
            this.remoteName = remoteName;
        }
        
        /** key값은 무시 */
        @Override
        protected String getDML(String tableName){
            dml = new StringBuilder();
            dml.append(" merge into ");
            dml.append(tableName+" t ");
            dml.append(" using "+tableName+"@"+remoteName+" r ");
            //str.append(" using "+tableName+"@"+remoteName+" r ");
            where();
            update();
            insert();
            return dml.toString();
        }
        
        @Override
        protected String getEtc(){
            dml = new StringBuilder();
            dml.append("\n");
            List<Mapp> seqs = jdbc.select("select * from USER_SEQUENCES","SEQUENCE_NAME","MAX_VALUE","LAST_NUMBER");
            for(Mapp each : seqs) dml.append(each.json().toString(1)+"\n");
            return dml.toString();
        }

        /** insert문 */
        private void insert() {
            boolean first = true;
            dml.append("\n when not matched then \n insert ( ");
            for(Mapp s:columnInfos){
                if(first) first = false;
                else dml.append(",");
                dml.append(" t."+value(s));
            }
            dml.append(" ) \n values ( ");
            first = true;
            for(Mapp s:columnInfos){
                if(first) first = false;
                else dml.append(",");
                dml.append(" r."+value(s));
            }           
            dml.append(" ) \n");
        }

        /** update문 */
        private void update() {
            dml.append(" when MATCHED THEN update \n set ");
            boolean first = true;
            for(Mapp s:columnInfos){
                if(isKey(s)) continue;
                if(first) first = false;
                else dml.append(",");
                dml.append(" t."+value(s)+" = r."+value(s));
            }
        }
        
        /** 비교 조건 입력 */
        private void where() {
            dml.append(" on ( ");
            boolean first = true;
            for(Mapp s:columnInfos){
                if(!isKey(s)) continue;
                if(first) first = false;
                else dml.append(" and ");
                dml.append(" t."+value(s)+" = r."+value(s));
            }
            dml.append(" )");
        }
        
    }
    
    

}


