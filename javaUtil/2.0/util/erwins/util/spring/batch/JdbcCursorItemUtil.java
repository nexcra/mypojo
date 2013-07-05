package erwins.util.spring.batch;

import javax.sql.DataSource;

import oracle.jdbc.OracleDriver;

import org.apache.commons.dbcp.BasicDataSource;


/**
 * 간단 작업/테스트용 유틸. 
 * 대용량이라서 그루비를 사용할 수 없을때 사용
 */
public abstract class JdbcCursorItemUtil{
	
	public static DataSource qwe(){
		BasicDataSource from = new BasicDataSource();
    	from.setUsername("midas_test");
    	from.setPassword("nvista1024");
    	from.setDriverClassName(OracleDriver.class.getName());
    	from.setUrl("jdbc:oracle:thin:@182.162.16.51:1521:ORCL");
		return from;
	}
    
}
