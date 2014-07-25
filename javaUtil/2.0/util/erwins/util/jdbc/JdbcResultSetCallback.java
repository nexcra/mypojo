package erwins.util.jdbc;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Map;

import org.apache.commons.collections.map.ListOrderedMap;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.io.FileSystemResource;

import erwins.util.spring.batch.CsvItemWriter;
import erwins.util.spring.batch.CsvItemWriter.PassThroughCsvAggregator;

/** 
 * 일단 HIVE실행기 사용 목적으로 만듬
 * 필요하면 각 구현체는 분리
 *  */
public interface JdbcResultSetCallback {
	
	public void lineResultSet(ResultSet resultSet) throws SQLException;
	
	public static abstract class JdbcMapCallback implements JdbcResultSetCallback{
		
		@SuppressWarnings("unchecked")
		public void lineResultSet(ResultSet resultSet) throws SQLException{
			ResultSetMetaData resultSetMetaData = resultSet.getMetaData();
			Map<String,Object> line = new ListOrderedMap();
			for(int i=0;i<resultSetMetaData.getColumnCount();i++){
				String columnName = resultSetMetaData.getColumnName(i); 
				line.put(columnName,resultSet.getObject(columnName));	
			}
			lineMap(line);
		}
		
		protected abstract void lineMap(Map<String,Object> line);
	}
	
	public static abstract class JdbcArrayCallback implements JdbcResultSetCallback{
		
		public void lineResultSet(ResultSet resultSet) throws SQLException{
			ResultSetMetaData resultSetMetaData = resultSet.getMetaData();
			Object[] line = new Object[resultSetMetaData.getColumnCount()];
			for(int i=0;i<resultSetMetaData.getColumnCount();i++){
				line[i] = resultSet.getObject(i+1); //+1 주의
			}
			lineArray(line);
		}
		
		protected abstract void lineArray(Object[] line);
	}
	
	/** 필요에 따라 수정해서 사용 */
	public static class JdbcCsvCallback implements JdbcResultSetCallback,Closeable{

		private CsvItemWriter<String[]> writer = new CsvItemWriter<String[]>();
		private Converter<Object,String> toStringConverter = new Converter<Object, String>() {
			@Override
			public String convert(Object arg0) {
				if(arg0==null) return "";
				return arg0.toString();
			}
		};
		
		/** 일반적으로는 문제 없지만, 이스케이핑 맞추려면 다르게 써야한다. */
		public static JdbcCsvCallback createForMsRead(File file){
			JdbcCsvCallback callback = new JdbcCsvCallback();
			callback.writer.setCsvAggregator(new PassThroughCsvAggregator());
			callback.writer.setResource(new FileSystemResource(file));
			callback.writer.open(new ExecutionContext());
			return callback;
		}
		
		public static JdbcCsvCallback createForJavaRead(File file){
			JdbcCsvCallback callback = new JdbcCsvCallback();
			callback.writer.setEncoding("UTF-8");
			callback.writer.setCsvRead(true);
			callback.writer.setCsvAggregator(new PassThroughCsvAggregator());
			callback.writer.setResource(new FileSystemResource(file));
			callback.writer.open(new ExecutionContext());
			return callback;
		}
		
		public void lineResultSet(ResultSet resultSet) throws SQLException{
			ResultSetMetaData resultSetMetaData = resultSet.getMetaData();
			String[] lines = new String[resultSetMetaData.getColumnCount()];
			for(int i=0;i<resultSetMetaData.getColumnCount();i++){
				lines[i] = toStringConverter.convert(resultSet.getObject(i+1)); //+1 주의
			}
			writer.writeLine(lines);
		}

		@Override
		public void close() throws IOException {
			writer.close();
		}
		
	}
	

}
