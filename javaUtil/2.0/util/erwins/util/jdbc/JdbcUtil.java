package erwins.util.jdbc;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import oracle.sql.CLOB;

import org.apache.commons.collections.map.ListOrderedMap;
import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.MapHandler;
import org.apache.commons.io.IOUtils;
import org.springframework.core.convert.converter.Converter;

import com.google.common.base.Throwables;
import com.google.common.collect.Lists;

import erwins.util.lib.CollectionUtil;
import erwins.util.root.exception.SQLRuntimeException;
import erwins.util.text.StringAppender;
import erwins.util.text.StringUtil;

/** 
 * 자주 쓰는거만 일단 만듬. 
 * 향후 추가하자. */
public class JdbcUtil {
	
	/** 일단 간이. enum을 자동변환 해주자. */
	public static Converter<Object,Object> CONVERTER = new Converter<Object, Object>() {
		@Override
		public Object convert(Object source) {
			if(source instanceof Enum){
        		Enum<?> en = (Enum<?>) source;
        		return en.name();
        	}
			return source;
		}
	};

	/** 부분 커밋하면서 입력한다.
	 * ex) batchInsert(dataSource,"QQ", params, 1000); */
	public static void batchInsert(DataSource dataSource,String tableName,List<Object[]> params,int commitInterval){
		Connection conn = null;
		try {
			conn = dataSource.getConnection();
			conn.setAutoCommit(false);
			
			QueryRunner runner = new QueryRunner();
			
			StringAppender appender = new StringAppender();
			appender.appendLine("SELECT a.TABLE_NAME,a.COLUMN_NAME,COMMENTS,DATA_TYPE,DATA_LENGTH,DATA_PRECISION,DATA_SCALE");
			appender.appendLine("FROM user_tab_columns a JOIN USER_COL_COMMENTS b");
			appender.appendLine("ON a.COLUMN_NAME = b.COLUMN_NAME AND a.TABLE_NAME = b.TABLE_NAME");
			appender.appendLine("WHERE a.TABLE_NAME = ? ");
			appender.appendLine("ORDER BY a.TABLE_NAME, COLUMN_ID ");
			
			List<String> columnNames = Lists.newArrayList();
			//걍 한번 쓸거라 일케 함
			List<Map<String,Object>> result = runner.query(conn, appender.toString(),LIST_MAP_HANDLER,tableName);
			for(Map<String,Object> value : result){
				columnNames.add(value.get("COLUMN_NAME").toString());
			} 
			
			String sql = "INSERT INTO "+tableName+" ("+StringUtil.join(columnNames,",")+") values ("+StringUtil.iterateStr("?", ",", columnNames.size())+")";
			
			List<List<Object[]>> splited = CollectionUtil.splitBySize(params, commitInterval); 
			for(List<Object[]> inputList : splited){
				
				Object[][] convertedList = new Object[inputList.size()][];
				
				for(int i=0;i<convertedList.length;i++){
					Object[] input = inputList.get(i);
					Object[] converted = new Object[input.length];
					for(int j=0;j<input.length;j++){
						converted[j] = CONVERTER.convert(input[j]);
					}
					convertedList[i] = converted;
				}
				runner.batch(conn, sql, convertedList);
				conn.commit();
			}
		} catch (SQLException e) {
			throw new SQLRuntimeException(e);
		}finally{
			DbUtils.closeQuietly(conn);
		}
		
	}
	
	/** SQL을 직접 사용하는건 다 이쪽이다. 내부 API 호출은 batchInsert와 동일 */
	public static  void batchUpdate(DataSource dataSource,String sql,List<Object[]> params,int commitInterval){
		Connection conn = null;
		try {
			conn = dataSource.getConnection();
			conn.setAutoCommit(false);
			
			QueryRunner runner = new QueryRunner();
			List<List<Object[]>> splited = CollectionUtil.splitBySize(params, commitInterval); 
			for(List<Object[]> each : splited){
				runner.batch(conn, sql, each.toArray(new Object[each.size()][]));
				conn.commit();
			}
		} catch (SQLException e) {
			throw new SQLRuntimeException(e);
		}finally{
			DbUtils.closeQuietly(conn);
		}
		
	}
	
	/** DDL 같은거 날릴때 사용. 잘 되는지는 의문 */
	public static void execute(DataSource dataSource,String sql,Object[] params){
		Connection conn = null;
		try {
			conn = dataSource.getConnection();
			conn.setAutoCommit(false);
			
			QueryRunner runner = new QueryRunner();
			runner.update(conn, sql, params);
			conn.commit();
			
		} catch (SQLException e) {
			throw new SQLRuntimeException(e);
		}finally{
			DbUtils.closeQuietly(conn);
		}
		
	}
	
	/** 간단 완성품을 만들때 사용한다. */
	public static ResultSetHandler<List<Map<String,Object>>> LIST_MAP_HANDLER = new ResultSetHandler<List<Map<String,Object>>>() {
		@SuppressWarnings("unchecked")
		@Override
		public List<Map<String, Object>> handle(ResultSet rs) throws SQLException {
			ResultSetMetaData metadata = rs.getMetaData(); 
			List<Map<String, Object>> result = Lists.newArrayList();
			while(rs.next()){
				Map<String, Object> map = new ListOrderedMap();
				for(int i=1;i<metadata.getColumnCount();i++){
					map.put(metadata.getColumnName(i), rs.getObject(i));
				}
				result.add(map);
			}
			return result;
		}
	};
	

    /** 
     * CLOB을 그냥 select해서 넣으면 안들어간다. 이거 한번 돌리고 쓰자.
     *  */
    public static List<? extends Object[]> convertClob(List<? extends Object[]> items){
    	List<Object[]> newItems = Lists.newArrayList();
    	for(Object[] item : items){
    		Object[] newItem = new Object[item.length];
    		for(int i=0;i<item.length;i++){
    			Object columnValue  = item[i];
    			if(columnValue instanceof CLOB){
    				CLOB clob = (CLOB) columnValue;
    				try {
    					columnValue = IOUtils.toString(clob.getCharacterStream());
					} catch (Exception e) {
						Throwables.propagate(e);
					}		
    			}
    			newItem[i] = columnValue;
    		}
    		newItems.add(newItem);
		}
    	return newItems;
    }
    

	/**   
	 * 별도의 커넥션 관련 코딩이 필요하다.
	 * ex)Connection conn = null;  conn = dataSource.getConnection(); 
	 * @throws SQLException 
	 *  */
	public static <T> T query(Connection conn,String sql,ResultSetHandler<T> rsh,Object ... params) throws SQLException{
		QueryRunner runner = new QueryRunner();
		T result =  runner.query(conn,sql, rsh, params);
		return result;
	}
	
	public static Map<String,Object> query(Connection conn,String sql,Object ... params) throws SQLException{
		return query(conn,sql,new MapHandler(),params);
	}
	
	/** 한번에 처리 후 커넥션을 닫는다. */
	public static List<Map<String,Object>> query(DataSource dataSource,String sql,List<Object[]> paramList){
		Connection conn = null;
		try {
			conn = dataSource.getConnection();
			conn.setAutoCommit(false);
			List<Map<String,Object>> results = Lists.newArrayList();
			for(Object[] param : paramList){
				Map<String,Object> result =  query(conn,sql, param);	
				results.add(result);
			}
			return results;
		} catch (SQLException e) {
			throw new SQLRuntimeException(e);
		}finally{
			DbUtils.closeQuietly(conn);
		}
	}

}
