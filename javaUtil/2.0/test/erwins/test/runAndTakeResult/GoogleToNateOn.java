
package erwins.test.runAndTakeResult;

import java.io.File;
import java.util.Map;

import org.junit.Test;

import erwins.util.lib.FileUtil;
import erwins.util.lib.StringUtil;
import erwins.util.lib.TextFileUtil;
import erwins.util.tools.TextFileReader;
import erwins.util.tools.TextFileReader.StringArrayCallback;
import erwins.util.tools.TextFileReader.StringMapCallback;

public class GoogleToNateOn{
	
	File org = new File("D:/google.csv");
	File temp = new File("D:/temp.txt");
	File result = new File("D:/result.csv");
    
    /** 일단 보정한다.  \n가 들어있는거는 하나로 합쳐주고 ,가 중간에 들어있는거는 대충 짜른다. */
    @Test
    public void change() throws Exception {
    	final StringBuilder b = new StringBuilder();
		TextFileReader r = new TextFileReader();
		r.read(org, new StringArrayCallback(){
			String temp = "";
			@Override
			public void process(String[] line) {
				if(line.length==56){
					b.append(StringUtil.join(line,'|'));
					b.append("\r\n");
				}else{
					temp += StringUtil.join(line,'|');
					int l = StringUtil.splitPreserveAllTokens(temp,'|').length;
					if(l>=56){
						if(l>56) temp = temp.substring(0,temp.length()-2);
						b.append(temp);
						b.append("\r\n");
						temp = "";
					}
				}
			}
			@Override  /** ,가 치환되어있지 않다.. 구글.. 이정도냐. */
			protected String seperator() {
				return ",";
			}
		},"UNICODE");
		TextFileUtil.write(temp, b);
    }
    
    /** 업로드 가능한 양식으로 바꿔준다. 이걸로 네이트에 업로드 ㄱㄱ */
    @Test
    public void reWrite() throws Exception {
    	final StringBuilder b = new StringBuilder();
		TextFileReader r = new TextFileReader();
		r.read(temp, new StringMapCallback(){
			@Override
			public void process(Map<String,String> line) {
				b.append(line.get("Name"));
				b.append(",");
				b.append(line.get("E-mail 1 - Value"));
				b.append(",");
				b.append(line.get("Phone 1 - Value"));
				b.append("\r\n");
			}
		});
		TextFileUtil.write(result, b,"EUC-KR");
		FileUtil.delete(org);
		FileUtil.delete(temp);
    }

}