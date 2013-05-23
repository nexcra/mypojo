package erwins.util.vender.springBatch;

import javax.sql.DataSource;

import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.jdbc.core.PreparedStatementSetter;

import erwins.util.root.StringArrayCallback;

/**
 * 간단 읽기용 리더
 */
public class JdbcCursorItemReaderUtil{
	
	/** 간단 사용용 */
	public static ExecutionContext read(DataSource dataSource,String sql,PreparedStatementSetter preparedStatementSetter,Integer maxItemCount, StringArrayCallback callback) throws Exception{
		int fetchSize = 10000; //조절 가능해야 할지도?
		ExecutionContext ex = new ExecutionContext();
		JdbcCursorItemReader<String[]> reader = new JdbcCursorItemReader<String[]>();
    	
		if(maxItemCount!=null) reader.setMaxItemCount(maxItemCount);
		
		reader.setDataSource(dataSource);
		ToStringArrayRowMapper rowMapper = new ToStringArrayRowMapper();
		reader.setRowMapper(rowMapper);
		reader.setFetchSize(fetchSize); 
		reader.setSql(sql);
		if(preparedStatementSetter!=null) reader.setPreparedStatementSetter(preparedStatementSetter);
		reader.afterPropertiesSet();
		
		try{
			reader.open(ex);
			while(true){
				String[] line = reader.read();
				if(line==null) break;
				callback.readStringArray(line, 0);
			}
			reader.update(ex);
		}finally{
			reader.close();
		}
		return ex;
	}
    
}
