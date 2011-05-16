package erwins.util.jdbc;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import erwins.util.counter.Latch;
import erwins.util.jdbc.TableInfos.TableInfo;
import erwins.util.tools.StringBuilder2;

/** 일단 각 DBMS별로 테이블 정보가 로드 된다면 SQL의 생성이 가능하다. */
public class TableInfos implements Iterable<TableInfo>{
	
	public static final String COMMENT_SQL_TABLE = "COMMENT ON TABLE {0} IS '{1}'";
	public static final String COMMENT_SQL_COLUMN = "COMMENT ON COLUMN {0} IS '{1}'";
	
	public static class TableInfo implements Iterable<ColumnInfo>{
		public String name;
		public String comment;
		public List<ColumnInfo> columns;
		@Override
		public Iterator<ColumnInfo> iterator() {
			return columns.iterator();
		}
		public String createDML(){
			StringBuilder2 builder = new StringBuilder2();
			builder.append("CREATE TABLE ");
			builder.append(name);
			builder.append("(");
			Latch l = new Latch();
			for(ColumnInfo each : columns){
				if(!l.next()) builder.append(",");
				builder.appendWord(each.name);
				builder.appendWord(each.dataType); //~~
				if(each.isPk()) builder.appendWord("NOT");
				builder.appendWord("NULL");
			}
			builder.append(")");
			return builder.toString();
		}
	}
	
	public static class ColumnInfo{
		public String name;
		public String dataType;
		public BigDecimal dataLength;
		public BigDecimal dataPrecision;
		public BigDecimal dataScale;
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

	@Override
	public Iterator<TableInfo> iterator() {
		return tables.iterator();
	}
    
}


