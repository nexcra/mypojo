
package erwins.test.runAndTakeResult;

import java.io.File;

import org.junit.Test;

import erwins.test.MyAccount;
import erwins.util.jdbc.JDBC;

public class SqlGenerate{
	
    public void merge2(){
        //Poi poi = new Poi("D:/book1.xls");
        //poi.uploadForOracle("test","128.1.63.213","epr","ECOUSER","ECOUSER");
    }	
    
    @Test
    public void merge() throws Exception {
        //JDBC jdbc = new JDBC("121.161.186.117","1522","sysbrain","agent","agent");
    	JDBC jdbc = JDBC.oracleInstance(MyAccount.HOST.IP, MyAccount.HOST.ORACLE_PORT,true, MyAccount.HOST.ORACLE_SID, "agent", "agent");
    	//JDBC jdbc = new JDBC("jdbc:sqlserver://192.168.1.156:1433;Databasename=agentDB","sa","agent",new SQLServerDriver());
        try {
			//TableInfos g = new TableInfoForOracle(jdbc);
        	//TableInfos g = new TableInfos(jdbc);
        	//g.addTable("TB_ATTEND_HIST","TB_ATTEND_RESULT_HIST","TB_CLASS_HIST","TB_SCORE_HIST","TB_USER_HIST","TB_USER_LOGIN_HIST");
        	//g.addTable("TB_COURSE_HIST","TB_SURVEY_QUEST","TB_SURVEY_QUEST_ANSWER","TB_SURVEY_QUEST_ITEM","TB_SURVEY");
			//g.removeAllTable();
			//jdbc.setDefaultTimeStampFormat();
			jdbc.loadSql(new File("D:/me3.txt"));
			//new MergeSql(g,"remote").toFile("D:/merge.sql");
			//new InsertSql(g).toFile("D:/insert.sql");
			//g.removeAllTable();
	        //g.getMergeDML("wcs_real","D:/merge.sql");
	        //g.getInsertDML("D:/insert.sql");
	        //g.getUpdateDML("D:/update.sql");			
		} catch (Exception e) {
			throw new RuntimeException(e);
		}finally{
			jdbc.close();
		}
    }
}