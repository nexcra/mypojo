package erwins.jsample;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.dbutils.*;
import org.apache.commons.dbutils.handlers.BeanListHandler;

import erwins.util.root.EntityId;


/** DBUtil을 이용한 간단한 JDBC 템플릿 */
@SuppressWarnings({"unused","unchecked"})
public class DbUtilSample{	
    
	//private static final String DRIVER_ORACLE = "oracle.jdbc.driver.OracleDriver" ;	
	//private static final String DRIVER_MSSQL = "com.microsoft.jdbc.sqlserver.SQLServerDriver";;
    private static final String URL_ORACLE = "jdbc:oracle:thin:@{0}:1521:{1}" ;
	//private static final String URL_MSSQL = "jdbc:microsoft:sqlserver://211.255.6.117:1433;database=tjkasa";
	
	private static final String COUNT = "COUNT(*)";
	
	private Connection connection_oracle = null;
	
	/** oracle용 입니다. */
	public DbUtilSample(String ip,String sid,String userId,String pass){
	    Connection conn = null;

	       try {

	           DbUtils.loadDriver("com.mysql.jdbc.Driver");
	           conn = DriverManager.getConnection("jdbc:mysql://localhost/test", "test", "1111");

	           ArrayList params = new ArrayList();
	           params.add("1%");

	           ResultSetHandler rsh = new BeanListHandler(EntityId.class);

	           QueryRunner qr = new QueryRunner();

	           List list = (List)qr.query(conn, "SELECT boardTitle, boardContent, userNick FROM board_test_t WHERE userIp like ?", params.toArray(), rsh);
	           
	           

	       } catch (Exception e) {

	           System.out.println(e);

	       } finally {

	           DbUtils.closeQuietly(conn);

	       }
	}

	/*
	public void setBoardCommonSecurity(ConnectionContext connectioncontext,

            AdminForm adminForm) throws BaseException {


String updateQuery = "UPDATE board_common_t SET badIp=?, badId=?, badNick=?, badContent=?, inputPerMin=?, tryLogin=?";

try {
ArrayList params = new ArrayList();
params.add(encode(adminForm.getBadIp()));
params.add(encode(adminForm.getBadId()));
params.add(encode(adminForm.getBadNick()));
params.add(encode(adminForm.getBadContent()));
params.add(String.valueOf(adminForm.getInputPerMin()));
params.add(String.valueOf(adminForm.getTryLogin()));

QueryRunner queryRunner = new QueryRunner();
queryRunner.update(connectioncontext.getConnection(), encode(updateQuery), params.toArray());

} catch (Exception e) {
logger.error("Error at AdminDAO.setBoardCommonSecurity",e);
BaseException baseException = new BaseException("errors.sql.problem");
throw baseException;
}

logger.info("AdminDAO.setBoardCommonSecurity was executed");
}
*/
}


