package erwins.test.runAndTakeResult;

import java.io.File;

import org.junit.Test;

import erwins.test.MyAccount;
import erwins.util.jdbc.JDBC;
import erwins.util.vender.apache.Poi;
import erwins.util.vender.apache.PoiDownloader;
import erwins.util.vender.apache.PoiUploader;

public class DB_Poi {

	// @Test
	public void backupTable() throws Exception {
		//JDBC jdbc = new JDBC(MyAccount.HOST.IP, MyAccount.HOST.ORACLE_PORT, MyAccount.HOST.ORACLE_SID, "loms", "loms");
		JDBC jdbc = JDBC.oracleInstance(MyAccount.HOST.IP, MyAccount.HOST.ORACLE_PORT,true, MyAccount.HOST.ORACLE_SID, "loms", "loms");
		Poi poi = new Poi();
		PoiDownloader pd = new PoiDownloader(poi, jdbc);
		pd.loadTable("backup_agent");
		poi.wrap();
		poi.write("D:/asd.xls");
	}

	@Test
	public void uploadTable() throws Exception {
		JDBC jdbc = JDBC.oracleInstance(MyAccount.HOST.IP, MyAccount.HOST.ORACLE_PORT,true, MyAccount.HOST.ORACLE_SID, "loms", "loms");
		jdbc.setDefaultTimeStampFormat();
		PoiUploader up = new PoiUploader(new File("D:/asd.xls"), jdbc);
		up.upload();
	}

}