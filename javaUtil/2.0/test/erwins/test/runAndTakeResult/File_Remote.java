
package erwins.test.runAndTakeResult;

import java.io.File;

import org.junit.Test;

import erwins.util.vender.apache.RESTful;

public class File_Remote{
	
	/** URL의 내용을 다운받는다. */
	@Test
    public void gather() throws Exception {
    	String url = "http://www.ruliweb.com/ruliboard/read.htm?table=img_cos&main=hb&num=12215";
    	RESTful.parseUrlAndSaveImg(url, new File("D:/img"), "jpg");
    }
    
}