package erwins.util.spring.batch;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.List;

import lombok.Data;

import org.springframework.jdbc.core.RowMapper;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

import erwins.util.root.NotThreadSafe;
import erwins.util.spring.batch.CsvItemWriter.CsvHeaderCallback;

/** DB로우데이터를 CSV로 내릴때 사용된다.
 * 스래드 안전하지 않다.  */
@NotThreadSafe
@Data
public class ToStringArrayRowMapper implements RowMapper<String[]>,CsvHeaderCallback{

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
    
    /** 한줄씩 호출되기 때문에, 스트리밍으로 쓸 경우 헤더를 추가할 수 없다.
     * 대신 최초 로우일때 메타데이터를 저장하게는 해놓았다.  */
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

	@Override
	public List<String[]> headers() {
		Preconditions.checkNotNull(columnCount, "read() 호출 후 헤더가 호출되어야 합니다.");
		String[] header = new String[columnCount];
		for(int i=0;i<columnMetaDatas.size();i++){
			ColumnMetaData each = columnMetaDatas.get(i);
			header[i] = each.columnLabel;
		}
		List<String[]> headers = Lists.newArrayList();
		headers.add(header);
		return headers;
	}

}
