package erwins.util.hadoop.hive;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.hadoop.hive.jdbc.HiveStatement;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemStream;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.jdbc.core.RowMapper;

import com.google.common.base.Preconditions;

import erwins.util.root.ThreadSafe;

/**   */
@ThreadSafe
public class HiveItemReader<T> implements ItemReader<T>, ItemStream{
	
	public static final String READ_COUNT = "read.count";
	/** ex) jdbc:hive://182.162.16.71:10000/default */
	private String url;
	/** 파라메터 따위 없음 */
	private String sql;
	private RowMapper<T> rowMapper;
	private Connection connection;
	private Statement statement;
	private ResultSet resultSet;
	private int fetchSize = 1000;
	
	//private int linesToSkip = 0;
	private int lineCount = 0;
	
	@Override
	public void close() throws ItemStreamException {
		try {
			if(resultSet!=null) resultSet.close();
			if(statement!=null) statement.close();
			if(connection!=null) connection.close();
		} catch (SQLException e) {
			throw new ItemStreamException(e);
		}
	}

	@SuppressWarnings("unused")
	@Override
	public void open(ExecutionContext arg0) throws ItemStreamException {
		Preconditions.checkNotNull(url, "url is required");
		Preconditions.checkNotNull(rowMapper, "rowMapper is required");
		Preconditions.checkNotNull(sql, "sql is required");
		try {
			Class.forName("org.apache.hadoop.hive.jdbc.HiveDriver");
			connection = DriverManager.getConnection(url);
			statement = connection.createStatement();
			if(statement instanceof HiveStatement){
				HiveStatement hiveStatement = (HiveStatement)statement;
				//~~
			}
			statement.setFetchSize(fetchSize);
			resultSet = statement.executeQuery(sql);
		} catch (ClassNotFoundException e) {
			throw new ItemStreamException(e);
		} catch (SQLException e) {
			throw new ItemStreamException(e);
		}
	}

	@Override
	public void update(ExecutionContext arg0) throws ItemStreamException {
		arg0.putInt(READ_COUNT, lineCount);
	}

	@Override
	public synchronized T read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
		boolean able =  resultSet.next();
		if(!able) return null;
		T vo = rowMapper.mapRow(resultSet, lineCount);
		lineCount++;
		return vo;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getSql() {
		return sql;
	}

	public void setSql(String sql) {
		this.sql = sql;
	}

	public RowMapper<T> getRowMapper() {
		return rowMapper;
	}

	public void setRowMapper(RowMapper<T> rowMapper) {
		this.rowMapper = rowMapper;
	}

	public int getFetchSize() {
		return fetchSize;
	}

	public void setFetchSize(int fetchSize) {
		this.fetchSize = fetchSize;
	}
	
	
	
}