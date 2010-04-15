
package erwins.util.vender.apache;


/**
 * oracle uploader.. 간단 업로드/통계쿼리용.
 * 나중에 다시 만들자.
 */
public class PoiOracleUploader{
	
	private final Poi poi;
	
	public PoiOracleUploader(Poi poi){
		this.poi = poi;
	}
	
    /*
	
    *//**
     * 오라클로 엑셀을 업로드 합니다.
     * 첫번째 로우 이름이 Table의 컬럼 이름이 됩니다.
     *//*
    public void uploadForOracle(String tableName,String ip,String sid,String id,String pass){
        new OracleUploader(tableName,ip,sid,id,pass);
    }    
    
    *//**
     * Key를 지정 후 insert or update하게 변경하자. num과 vchar를 구분하는 로직은 필요 없을듯.
     *//*
    private class OracleUploader{
        private static final String CREATE_TIME = "CREATE_TIME";
        private static final String TIMESTAMP = "TIMESTAMP";
        
        private List<String> cols = new ArrayList<String>();
        *//**
         * @uml.property  name="jdbc"
         * @uml.associationEnd  
         *//*
        JDBC jdbc;
        private String tableName;
        
        public OracleUploader(String tableName,String ip,String sid,String id,String pass){
            this.tableName = tableName.toUpperCase();
            try {
				jdbc = new JDBC(ip,sid,id,pass);
			} catch (SQLException e) {
				throw new RuntimeException(e);
			}
            initSheet(0,0);
            run();
        }

        private void run() {
            try {
                makeTable();
                insertData();
                jdbc.commit();
            }
            catch (Exception e) {
                jdbc.rollback();
                throw new RuntimeException(e);
            }finally{
                jdbc.close();
            }            
        }

        private void insertData() throws SQLException {
            List<String> datas = null;
            int count = 0;
            while((datas = next()) != null){
                StringBuilder str = new StringBuilder("INSERT INTO "+tableName+" ");
                boolean first = true;
                str.append(" ( ");
                for(String col: cols){
                    if(first) first = false;
                    else  str.append(" , ");
                    str.append(col);
                }
                str.append(","+CREATE_TIME);
                str.append(" ) values (");
                
                first = true;
                if(datas.size() < cols.size()) throw new RuntimeException(count+" size가 col보다 작습니다.");
                for(int i=0;i<cols.size();i++){
                    String value = datas.get(i);
                    if(first) first = false;
                    else  str.append(" , ");
                    str.append("'");
                    str.append(value.replaceAll("'","\'")); //????
                    str.append("'");
                }
                str.append(","+"SYSDATE");
                str.append(" ) ");
                jdbc.update(str.toString());
                count++;
            }
        }

        *//**
         * 이놈들은 commit이라는게 없다. 조심 
         *//*
        private void makeTable() throws SQLException {
            cols = next();
            if(jdbc.isContain("select count(*) from user_tables where table_name = '"+tableName+"'")) return;
            StringBuilder str = new StringBuilder("CREATE TABLE "+tableName+" ");
            str.append("(");
            boolean isFirst = true;
            for(String row : cols){
                if(isFirst) isFirst = false;
                else str.append(","); 
                str.append(row);
                str.append(" VARCHAR2 (4000)");
            }
            str.append(","+CREATE_TIME + " "+TIMESTAMP+" ");
            str.append(")");
            jdbc.update(str.toString());
            jdbc.update("COMMENT ON TABLE "+tableName+" IS '"+Days.DATE.get()+" POI로 제작된 테이블 입니다.'");
        }
        
    }
    */
    
}
