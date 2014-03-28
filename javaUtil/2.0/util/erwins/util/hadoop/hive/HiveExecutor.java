package erwins.util.hadoop.hive;

import java.io.Closeable;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;

import erwins.util.root.JdbcResultSetCallback;
import erwins.util.root.JdbcResultSetCallback.JdbcMapCallback;

/** 간이 HIVE실행기. 어플에서 쓸때는 ThreadLocal에 놓고 써라. */
public class HiveExecutor {
	
	private Connection con = null;
	int fetchSize = 10000;
	
	/** ex) jdbc:hive://182.162.16.81:10000/default */
	public HiveExecutor(String url) throws SQLException{
		try {
			Class.forName("org.apache.hadoop.hive.jdbc.HiveDriver");
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
		con = DriverManager.getConnection(url);
	}
	
	public void close() throws SQLException{
		if(con != null) con.close();
	}
	
	/** Statement는 쓰고 버린다. */
	public long executeQuery(String hql,JdbcResultSetCallback callback) throws SQLException{
		long row = 0;
		Statement stmt = null;
		try {
			stmt = con.createStatement();
			stmt.setFetchSize(fetchSize);
			ResultSet res = stmt.executeQuery(hql);
			while (res.next()) {
				row++;
				callback.lineResultSet(res);
			}
		} finally {
			if(stmt!=null) stmt.close();
		}
		return row;
	}
	
	public List<Map<String,Object>> executeQuery(String hql) throws SQLException{
		final List<Map<String,Object>> result = Lists.newArrayList();
		executeQuery(hql, new JdbcMapCallback() {
			@Override
			public void lineMap(Map<String, Object> line) {
				result.add(line);
			}
		});
		return result;
	}
	
	/**
	 * 한번 사용된 콜백은 버린다(close)고 간주한다.
	 * ex)
	 *  String hql = "select channel_id, count(*) count  from req_raw where basic_date='20130427'  group by channel_id";
		File file = new File("C:/DATA/download/result.csv");
		HiveExecutor.executeQuery("jdbc:hive://182.162.16.81:10000/default", hql, JdbcCsvCallback.createForMsRead(file) );
	 *  */
	public static void executeQuery(String url,String hql,JdbcResultSetCallback callback){
		HiveExecutor he =  null;
		try {
			he = new HiveExecutor(url);
			he.executeQuery(hql, callback);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		} finally {
			if(he!=null) {
				try {
					he.close();
				} catch (SQLException e) {
					throw new RuntimeException(e);
				}
				if(callback instanceof Closeable){
					Closeable closeable = (Closeable) callback;
					try {
						closeable.close();
					} catch (IOException e) {
						throw new RuntimeException(e);
					}
				}
			}
		}
	}
	

}
