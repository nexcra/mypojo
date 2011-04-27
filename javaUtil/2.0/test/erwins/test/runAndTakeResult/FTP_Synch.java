
package erwins.test.runAndTakeResult;

import org.junit.Test;

import erwins.util.vender.apache.Net;
import erwins.util.vender.apache.NetRoot.FtpLog;

public class FTP_Synch{
	
    
    @Test
    public void ftpSynch() throws Exception {
        Net m1 = new Net();
        try {
            m1.connect("",0,"","");
            m1.setPassive();
            //m1.setRoots("/system/file/open/music","D:/DATA/MUSIC").update();
            m1.setRoots("/SYSTEM/file/open/music","D:/DATA/MUSIC").updateLog();
            //m1.connect("218.156.67.18","guest","dudrkasla");
            //m1.setRoots("/share/test","D:/임시").updateLog();
            //m1.setRoots("/d:/SYSTEM/임시","D:/임시").updateLog();
            FtpLog log = m1.getFtpLog();
            System.out.println(log);
        }
        catch (Exception e) {
            System.out.println("error : "+e.getMessage());
        }
        finally {
            m1.disconnect();
            System.out.println("disconnect!!!");
        }
    }
}
