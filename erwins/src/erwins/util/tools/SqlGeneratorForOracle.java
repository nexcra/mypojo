package erwins.util.tools;

import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import erwins.util.lib.Files;
import erwins.util.lib.RegEx;


/**
 * Oracle SQL을 생성해 보아요~ 노가다는 싫어. 소스가 저질이다. ㅠㅠ.
 */
public class SqlGeneratorForOracle{
	
	public static class TableInfo{
		public String name;
		public String comment;
		public List<ColumnInfo> columns;
	}
	
	public static class ColumnInfo{
		public String name;
		public String pk;
		public boolean isPk(){
			return "true".equals(pk);
		}
	}
    
    private JDBC jdbc;
    /** 해당 계정의 테이블 정보. */
    private List<TableInfo> tables = new ArrayList<TableInfo>();
    
    /** 테이블/컬럼 정보를 로드한다. */
    public SqlGeneratorForOracle(JDBC jdbc) throws SQLException{
        this.jdbc = jdbc;
        
        String tableScanSql = "select a.TABLE_NAME\"name\",b.COMMENTS\"comment\" from USER_tables a join USER_TAB_COMMENTS b on a.TABLE_NAME = b.TABLE_NAME";
        tables = jdbc.select(tableScanSql,TableInfo.class);
        
        StringBuilder temp = new StringBuilder();
        temp.append("SELECT M.COLUMN_NAME\"name\",");
        temp.append(" (select 'true'");
        temp.append("  from USER_CONS_COLUMNS A join USER_CONSTRAINTS B on A.CONSTRAINT_NAME = B.CONSTRAINT_NAME");
        temp.append("  where B.CONSTRAINT_TYPE = 'P'");
        temp.append("  and A.COLUMN_NAME = M.COLUMN_NAME");
        temp.append("  and A.TABLE_NAME = M.TABLE_NAME )\"pk\" ");
        temp.append("from user_tab_columns M ");
        temp.append("where M.table_name = ");
        String columnScanSql = temp.toString();
        for(TableInfo each : tables) each.columns = jdbc.select(columnScanSql + "'"+each.name+"'",ColumnInfo.class);
    }
    
    /** 로컬DB를 RemoteDB(DB링크)와 동기화 시키는 merge DML을 생성한다. */
    public void getMergeDML(String remoteName,String fileName){
        DmlTemplit dml = new MergeDML(remoteName);
        dml.getDmlToFile(fileName);
    }
    /*
    public void getInsertDML(String fileName){
        DmlTemplit dml = new InsertDML();
        dml.getDmlToFile(fileName);
    }
    
    public void getUpdateDML(String fileName){
        DmlTemplit dml = new UpdateDML();
        dml.getDmlToFile(fileName);
    }*/
    
    /**
     * 각 Table별 DML을 출력하는 템플릿이다. 
     * 출력 String은 첫 문자에 공백이 없게 설정한다.
     */
    private abstract class DmlTemplit{
        protected StringBuilder dml;
        
        public void getDmlToFile(String fileName){
            StringBuilder result = new StringBuilder();
            for(TableInfo each : tables){
                result.append("---------------  ");
                result.append(each.name);
                if(each.comment!=null) result.append("("+each.comment+")");
                result.append("   ---------------\n");
                result.append(getDML(each) + "; \n");
            }
            Files.writeStr(RegEx.FIRST_BLANK.replace(result.toString(),""), new File(fileName),"UTF-8");
        }
        
        protected abstract String getDML(TableInfo table);
    }
    
    private class MergeDML extends DmlTemplit{
        String remoteName;
        public MergeDML(String remoteName){
            this.remoteName = remoteName;
        }
        
        /** key값은 무시 */
        @Override
        protected String getDML(TableInfo table){
            dml = new StringBuilder();
            dml.append(" merge into ");
            dml.append(table.name+" t ");
            dml.append(" using "+table.name+"@"+remoteName+" r ");
            //str.append(" using "+tableName+"@"+remoteName+" r ");
            where(table);
            update(table);
            insert(table);
            return dml.toString();
        }
        
        protected String getEtc() throws SQLException{
            dml = new StringBuilder();
            dml.append("\n");
            List<Mapp> seqs = jdbc.select("select * from USER_SEQUENCES");
            for(Mapp each : seqs) dml.append(each.json().toString(1)+"\n");
            return dml.toString();
        }

        /** insert문 */
        private void insert(TableInfo table) {
            boolean first = true;
            dml.append("\n when not matched then \n insert ( ");
            for(ColumnInfo each:table.columns){
                if(first) first = false;
                else dml.append(",");
                dml.append(" t."+each.name);
            }
            dml.append(" ) \n values ( ");
            first = true;
            for(ColumnInfo each:table.columns){
                if(first) first = false;
                else dml.append(",");
                dml.append(" r."+each.name);
            }           
            dml.append(" ) \n");
        }

        /** update문 */
        private void update(TableInfo table) {
            dml.append(" when MATCHED THEN update \n set ");
            boolean first = true;
            for(ColumnInfo each:table.columns){
                if(each.isPk()) continue;
                if(first) first = false;
                else dml.append(",");
                dml.append(" t."+each.name+" = r."+each.name);
            }
        }
        
        /** 비교 조건 입력 */
        private void where(TableInfo table) {
            dml.append(" on ( ");
            boolean first = true;
            for(ColumnInfo each:table.columns){
            	if(each.isPk()) continue;
                if(first) first = false;
                else dml.append(" and ");
                dml.append(" t."+each.name+" = r."+each.name);
            }
            dml.append(" )");
        }
        
    }
    
    /*
    
    private class InsertDML extends DmlTemplit{

        @Override
        protected String getDML(TableInfo tableName) {
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
        protected String getDML(TableInfo tableName) {
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
    */
    

}


