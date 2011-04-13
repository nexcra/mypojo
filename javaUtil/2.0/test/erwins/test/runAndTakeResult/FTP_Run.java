
package erwins.test.runAndTakeResult;

import org.junit.Test;

import erwins.util.vender.apache.Net;

public class FTP_Run{
    
    //@Test
    public void get() throws Exception {
        Net m1 = new Net();
        try {
            m1.connect("128.1.63.213","wcs","answjdvlf");
            m1.setRoots("/images/alice/", "/images/alice/editor").setAbleFolders("form").moveAll();
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
        finally {
            m1.disconnect();
        }
    }
    
    /**
     * WAS의 로그를 복사해온다. 
     */
    @Test
    public void getLog() throws Exception {
        Net m1 = new Net();
        try {
            m1.connect("210.104.107.18","weblogic","buweblogic");
            m1.setRoots("/nas/logs","D:/nas").downloadAll();
            m1.setRoots("/APP/bea/user_projects/domains/wcs/logs","D:/nas")
            .setAbleExtentions("log").setAbleNames("m2").setAllDirectory(false).downloadAll();
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
        finally {
            m1.disconnect();
        }
        
        Net m2 = new Net();
        try {
            m2.connect("210.104.107.19","weblogic","buweblogic2");
            m2.setRoots("/APP/bea/user_projects/domains/wcs/logs","D:/nas")
            .setAbleExtentions("log").setAbleNames("m1").setAllDirectory(false).downloadAll();
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
        finally {
            m2.disconnect();
        }
    }
    
    /** 실제 적용예. */
    //@Test
    public void getLog2() throws Exception {
        Net m1 = new Net();
        try {
            m1.connect("128.1.1.61","custom","vPrlanf%");
            m1.setRoots("/custom/ready/week","/nas/custom/ready").setAllDirectory(false).downloadAll();
            m1.setRoots("/custom/ready/week", "/custom/ready/week/keep").setAllDirectory(false).moveAll();
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
        finally {
            m1.disconnect();
        }

    }
}