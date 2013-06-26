package erwins.util.spring.batch;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.List;

import org.springframework.jdbc.core.RowMapper;

import com.google.common.collect.Lists;

/** DB로우데이터를 CSV로 내릴때 사용된다  */
public class ToStringArrayRowMapper implements RowMapper<String[]>{

    private Integer columnCount = null;
    private List<ColumnMetaData> columnMetaDatas = Lists.newArrayList();
    
    public static class ColumnMetaData{
    	public final String columnLabel;
    	public final String columnClassName;
    	public final String columnTypeName;
		public ColumnMetaData(String columnLabel, String columnClassName,String columnTypeName) {
			this.columnLabel = columnLabel;
			this.columnClassName = columnClassName;
			this.columnTypeName = columnTypeName;
		}
    }
    
    /** 첫 라이터가 호출되기 전에 메타데이터가 저장된다.
     * 헤더를 원하면 첫 라인에 추가하자 */
    @Override
    public String[] mapRow(ResultSet rs, int rowNum) throws SQLException {
        if(rs.isFirst()) {
            ResultSetMetaData md = rs.getMetaData();
            columnCount = md.getColumnCount();
            for(int column=0;column<columnCount;column++){
            	ColumnMetaData metadata = new ColumnMetaData(md.getColumnLabel(column+1),md.getColumnClassName(column+1),md.getColumnTypeName(column+1)); 
            	columnMetaDatas.add(metadata);
            }
        }
        String[] result = new String[columnCount];
        for(int column=0;column<columnCount;column++){
            result[column] =  rs.getString(column+1);
        }
        return result; 
    }

	public void setColumnMetaDatas(List<ColumnMetaData> columnMetaDatas) {
		this.columnMetaDatas = columnMetaDatas;
	}
    

}
