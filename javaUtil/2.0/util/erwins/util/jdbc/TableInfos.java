package erwins.util.jdbc;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/** 일단 각 DBMS별로 테이블 정보가 로드 된다면 SQL의 생성이 가능하다. */
public class TableInfos{
	
	public static class TableInfo{
		public String name;
		public String comment;
		public List<ColumnInfo> columns;
	}
	
	public static class ColumnInfo{
		public String name;
		public String dataType;
		public String pk;
		public boolean isPk(){
			return "true".equals(pk);
		}
	}
	
	public TableInfos(JDBC jdbc){
		this.jdbc = jdbc;
	}
    
    protected final JDBC jdbc;
    /** 해당 계정의 테이블 정보. */
    public List<TableInfo> tables = new ArrayList<TableInfo>();
    
    public void removeAllTable() throws SQLException{
    	for(TableInfo each : tables){
    		jdbc.execute("DELETE FROM " + each.name);
    		jdbc.commit();
    	}
    }
    
    /** JDBC를 이용해 테이블 정보를 가져오는게 아니라 직접 입력한다. */
    public void addTable(String ... names){
    	for(String each : names){
    		TableInfo info = new TableInfo();
    		info.name = each;
    		tables.add(info);
    	}
	}
    
}


