package erwins.util.jdbc;

import java.io.File;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import oracle.jdbc.driver.OracleDriver;
import erwins.util.collections.map.RequestMap;
import erwins.util.exception.BusinessException;
import erwins.util.lib.ReflectionUtil;
import erwins.util.root.StringCallback;
import erwins.util.tools.TextFileReader;


/**  
 * JDBC API 테스트 용임으로 DBUtils를 사용하지 않는다. 
 * 사용후 반드시 닫을것!
 *  Connection을 풀링하지 않는다면 conn만 닫으면 하위의 자원(Statement, ResultSet)도 함께 닫힌다.
 *  만약 풀링한다면 conn이 닫히지 않기 때문에 수많은 state들이 남아있어 공간이 부족해질 수 있다.
 *  여기에서는 임시 커넥션을 만을 사용함으로 명시적으로 닫지 않는다. 
 * */
public class JDBC{	
	
    /** 드라이버 구버전 */
	public static final String URL_MS_SQL_OLD = "jdbc:sqlserver://{0}:{1};Databasename={2}" ;	
	public static final String URL_MY_SQL = "jdbc:mysql://{0}:{1}/{2}";
	public static final String URL_ORACLE = "jdbc:oracle:thin:@{0}:{1}{2}{3}" ;
	
	private static final String COUNT = "COUNT(*)";
	
	private Connection connection_oracle = null;
	
	public static JDBC oracleInstance(String ip,String port,boolean isSid,String sid,String userId,String pass){
		String url = MessageFormat.format(URL_ORACLE, ip,port,isSid?":":"/",sid);
		return new JDBC(url,userId,pass,new OracleDriver());
	}
	public static JDBC oracleInstance(String ip,String sid,String userId,String pass){
		String url = MessageFormat.format(URL_ORACLE, ip,"1521",":",sid);
		return new JDBC(url,userId,pass,new OracleDriver());
	}
	
	public JDBC(String url,String userId,String pass,Driver driver) {
		try {
			DriverManager.registerDriver(driver);
			connection_oracle = DriverManager.getConnection(url,userId, pass);
			connection_oracle.setAutoCommit(false);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	
	
	public void close(){
        try {
            connection_oracle.close();
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }	    
	}
	
	public void commit() throws SQLException{
	    connection_oracle.commit();
	}
	
	public void rollback(){
        try {
            connection_oracle.rollback();
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
	}
	
    public List<RequestMap> select(String sql){
        List<RequestMap> results = new ArrayList<RequestMap>();
        try {
			Statement statement_oracle = connection_oracle.createStatement();
			ResultSet resultSet = statement_oracle.executeQuery(sql);
			
			while(resultSet.next()){
			    results.add(resultsetToRequestMap(resultSet));
			}
			resultSet.close();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
        return results;
    }
    
    public <T> List<T> select(String sql,Class<T> clazz) throws SQLException{
    	List<T> results = new ArrayList<T>();
		Statement statement_oracle = connection_oracle.createStatement();
		ResultSet resultSet = statement_oracle.executeQuery(sql);
		
		while(resultSet.next()){
			results.add(resultsetToClass(resultSet,clazz));
		}
		resultSet.close();
    	return results;
    }
    
    public RequestMap selectOne(String sql){
    	List<RequestMap> list = select(sql);
    	if(list.size() == 1) return list.get(0); 
    	throw new BusinessException("result size must be 1. input size is {0}",list.size());
    }
    public RequestMap selectOne2(String sql){
    	List<RequestMap> list = select(sql);
    	if(list.size() == 1) return list.get(0); 
    	throw new BusinessException("result size must be 1. input size is {0}",list.size());
    }
    
    /** 타임스탬프 포맷을 현제 세션에서 조정한다. */
    public void setDefaultTimeStampFormat() throws SQLException{
    	execute("alter session set nls_timestamp_format='YYYY-MM-DD HH24:MI:SSXFF'");
    }
    
    /** SQL구문이 담긴 문장을 읽고 실행한다. and 커밋까지. 기본 툴에서 실행하는것과 동일하며 한줄에 하나의 SQL만이 담겨야 한다. */
    public void loadSql(File sql) throws SQLException{
    	final List<String> list = new ArrayList<String>();
    	new TextFileReader().read(sql,new StringCallback() {
			@Override
			public void process(String line) {
				line = line.trim().replaceAll(";","");
				if("".equals(line)) return;
				if(line.startsWith("--")) return;
				list.add(line);
			}
		});
    	execute(list);
		commit();
    }    
    
    
    /**
     *  select count(*) from user_tables 등의 소속 여부를 리턴한다.
     * @throws SQLException 
     */
    public boolean isContain(String sql) throws SQLException{
    	Statement statement_oracle = connection_oracle.createStatement();
        ResultSet resultSet = statement_oracle.executeQuery(sql);
        resultSet.next();
        boolean result = resultSet.getBoolean(COUNT);
        resultSet.close();
        return result;
    }
    
    public void executeByPrepareStatement(String sql,Object ... objs) throws SQLException{
    	PreparedStatement statement_oracle = connection_oracle.prepareStatement(sql);
    	for(int i=0;i<objs.length;i++){
    		statement_oracle.setObject(i+1,objs[i]);
    	}
    	statement_oracle.execute();
    }
    
    /** 대량insert 
     * PreparedStatement를 재사용하기위해 사용한다. = > 나중에 배치로 변경 */
    public void insert(String sql,List<Object[]> parameters) throws SQLException{
    	PreparedStatement statement_oracle = connection_oracle.prepareStatement(sql);
    	for(Object[] parameter : parameters){
    		for(int i=0;i<parameter.length;i++){
        		statement_oracle.setObject(i+1,parameter[i]);
        	}
        	statement_oracle.execute();	
        	statement_oracle.clearParameters();
    	}
    }
    
    public void execute(String sql) throws SQLException{
    	Statement statement_oracle = connection_oracle.createStatement();
    	if(statement_oracle.execute(sql)) throw new SQLException(sql+" is fail");
    }
    
    public void execute(Collection<String> sqls) throws SQLException{
    	Statement statement_oracle = connection_oracle.createStatement();
    	for(String sql : sqls){
    		try {
				if(statement_oracle.execute(sql)) throw new SQLException(sql+" is fail");
			} catch (Exception e) {
				throw new RuntimeException(sql+" is fail",e);
			}
    	}
    }
    
    public int update(String sql) throws SQLException{
        Statement statement_oracle = connection_oracle.createStatement();
        int result = statement_oracle.executeUpdate(sql);
        return result;
    }
    public void updateOne(String sql) throws SQLException{
    	Statement statement_oracle = connection_oracle.createStatement();
    	int result = statement_oracle.executeUpdate(sql);
    	if(result!=0) throw new SQLException(sql+" 's result must be 1");
    }
    
    // =============================  static ====================================    
    
    /** ResultSet을 Map으로 매핑한다. */
    public static Map<String,Object> resultsetToLinkedHashMap(ResultSet rs) throws SQLException {
		ResultSetMetaData meta = rs.getMetaData();
		int columnCount = meta.getColumnCount();
		Map<String,Object> resultMap = new LinkedHashMap<String,Object>(columnCount);
		for(int i = 1; i <= columnCount; i++){
			String key = lookupColumnName(meta, i);
			Object obj = getResultSetValue(rs, i);
			resultMap.put(key, obj);
		}
		return resultMap;
    }
    public static RequestMap resultsetToRequestMap(ResultSet rs) throws SQLException {
    	ResultSetMetaData meta = rs.getMetaData();
    	int columnCount = meta.getColumnCount();
    	RequestMap resultMap = new RequestMap();
    	for (int i = 1; i <= columnCount; i++) {
    		String key = lookupColumnName(meta, i);
    		Object obj = getResultSetValue(rs, i);
    		resultMap.put(key, obj);
    	}
    	return resultMap;
    }
    /** field에 직접 매핑한다. 간단한것만 사용할것!. */
    public static <T> T resultsetToClass(ResultSet rs,Class<T> clazz) throws SQLException {
    	ResultSetMetaData meta = rs.getMetaData();
    	int columnCount = meta.getColumnCount();
    	T one = ReflectionUtil.newInstance(clazz);
    	for (int i = 1; i <= columnCount; i++) {
    		String key = lookupColumnName(meta, i);
    		Object obj = getResultSetValue(rs, i);
    		ReflectionUtil.setField(clazz,one, key, obj);
    	}
    	return one;
    }
    
    /** 각 벤더의 고유한 처리 담당. */
    private static Object getResultSetValue(ResultSet rs, int index) throws SQLException {
		Object obj = rs.getObject(index);
		String className = null;
		if (obj != null) {
			className = obj.getClass().getName();
		}
		if (obj instanceof Blob) {
			obj = rs.getBytes(index);
		}
		else if (obj instanceof Clob) {
			obj = rs.getString(index);
		}
		else if (className != null &&
				("oracle.sql.TIMESTAMP".equals(className) ||
				"oracle.sql.TIMESTAMPTZ".equals(className))) {
			obj = rs.getTimestamp(index);
		}
		else if (className != null && className.startsWith("oracle.sql.DATE")) {
			String metaDataClassName = rs.getMetaData().getColumnClassName(index);
			if ("java.sql.Timestamp".equals(metaDataClassName) ||
					"oracle.sql.TIMESTAMP".equals(metaDataClassName)) {
				obj = rs.getTimestamp(index);
			}
			else {
				obj = rs.getDate(index);
			}
		}
		else if (obj != null && obj instanceof java.sql.Date) {
			if ("java.sql.Timestamp".equals(rs.getMetaData().getColumnClassName(index))) {
				obj = rs.getTimestamp(index);
			}
		}
		return obj;
	}    
    
    /** 메타정보로 컬럼 이름을 반환한다. */
	public static String lookupColumnName(ResultSetMetaData resultSetMetaData, int columnIndex) throws SQLException {
		String name = resultSetMetaData.getColumnLabel(columnIndex);
		if (name == null || name.length() < 1) {
			name = resultSetMetaData.getColumnName(columnIndex);
		}
		return name;
	}
}


