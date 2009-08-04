package erwins.util.tools;

import java.sql.*;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import oracle.jdbc.driver.OracleDriver;


/** 테스트용 간이 템플릿. 사용후 반드시 닫을것! */
public class JDBC{	
    
	//private static final String DRIVER_ORACLE = "oracle.jdbc.driver.OracleDriver" ;	
	//private static final String DRIVER_MSSQL = "com.microsoft.jdbc.sqlserver.SQLServerDriver";;
	private static final String URL_ORACLE = "jdbc:oracle:thin:@{0}:1521:{1}" ;
	//private static final String URL_MSSQL = "jdbc:microsoft:sqlserver://211.255.6.117:1433;database=tjkasa";
	
	private static final String COUNT = "COUNT(*)";
	
	private Connection connection_oracle = null;
	
	/** oracle용 입니다. */
	public JDBC(String ip,String sid,String userId,String pass){
	    try {
	        //Class.forName(URL_MSSQL);
	        DriverManager.registerDriver(new OracleDriver());
	        String url = MessageFormat.format(URL_ORACLE, ip,sid);
            connection_oracle = DriverManager.getConnection(url,userId, pass);
            connection_oracle.setAutoCommit(false);
        }
        catch (SQLException e) {
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
	
    public List<Mapp> select(String sql,String ... params){
        
        List<Mapp> results = new ArrayList<Mapp>();
        
        try {
            Statement statement_oracle = connection_oracle.createStatement();
            ResultSet resultSet = statement_oracle.executeQuery(sql);
            
            while(resultSet.next()){
                Mapp result = new Mapp();
                for(String param : params){
                    result.put(param,resultSet.getString(param));
                }
                results.add(result);
            }
            resultSet.close();
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return results;
    }
    
    
    /**
     *  select count(*) from user_tables 등의 소속 여부를 리턴한다.
     */
    public boolean isContain(String sql){
        try {
            Statement statement_oracle = connection_oracle.createStatement();
            ResultSet resultSet = statement_oracle.executeQuery(sql);
            resultSet.next();
            boolean result = resultSet.getBoolean(COUNT);
            resultSet.close();
            return result;
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    
    public int update(String sql) throws SQLException{
        Statement statement_oracle = connection_oracle.createStatement();
        int result = statement_oracle.executeUpdate(sql);
        return result;
    }
	
	/*
	public List<Mapp> select(String sql,String ... params) throws SQLException{
		
	    List<Mapp> results = new ArrayList<Mapp>();
	    
	    Statement statement_oracle = connection_oracle.createStatement();
        ResultSet resultSet = statement_oracle.executeQuery(sql);
        
        while(resultSet.next()){
            Mapp result = new Mapp();
            for(String param : params){
                result.put(param,resultSet.getString(param));
            }
            results.add(result);
        }
            
        resultSet.close();
                        
        connection_oracle.commit(); 
		
		return results;
		
	}
	*/
	

}


