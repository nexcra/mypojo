
package erwins.util.vender.apache;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpURL;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.URIException;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.io.IOUtils;

import erwins.util.exception.ExceptionUtil;
import erwins.util.lib.CharEncodeUtil;
import erwins.util.lib.StringEscapeUtil;
import erwins.util.lib.FileUtil;
import erwins.util.lib.RegEx;
import erwins.util.lib.StringUtil;
import erwins.util.root.StringCallback;



/**
 * NameValuePair 사용시 자동으로 UTF-8형식으로 URL-인코드 된다?.
 * 표준파서 사용시 Stream을 그대로 부어서 제작하면 encode오류가 난다. ㅠㅠ 이유는 몰라염 내공이 부족하구나.
 * 따라서 getResponseBodyAsString()으로 String변환(즉 UTF-8)했다가 stream으로 바꾸어 준다.
 * 근데 위방법도 같은 XP장비의 톰캣에서 에러가 난다..  그래서 파서를 하나 만들었다.
 * @author erwins(my.pojo@gmail.com)
 */
public class RESTful{
    
    /** 걍 공용으로 쓴다. */
    private static HttpClient client = new HttpClient();
    
    private HttpMethod method = null;
    
    public static RESTful post(String url){
    	RESTful instance = new RESTful();
        instance.method = new  PostMethod(url);
        return instance;
    }
    public static RESTful post(){
    	RESTful instance = new RESTful();
    	instance.method = new  PostMethod();
    	return instance;
    }
    public static RESTful get(String url){
    	RESTful instance = new RESTful();
    	instance.method = new  GetMethod(url);
    	return instance;
    }
    public static RESTful get(){
    	RESTful instance = new RESTful();
    	instance.method = new  GetMethod();
    	return instance;
    }
    
    /** 동일한 세션에서 다른 url을 사용할때 쓴다. */
    public RESTful run(String url){
    	try {
			method.setURI(new HttpURL(url));
		} catch (URIException e) {
			throw new RuntimeException(e);
		}
		return run();
    }
    
    /** url 사이의 port는 테스트 해바야함. */
    public RESTful run(){
        try {
            client.executeMethod(method);
        }
        catch (IOException e) {
            ExceptionUtil.castToRuntimeException(e);
        }
        return this;
    }    
    
    /**
     * 파일 업로드시.
     * ex) FilePart filePart = new FilePart("upfile", targetFile, null, "EUC-KR"); //EUC-KR
     * Part[] parts = { filePart  };
     */
    public RESTful upload(Part ... parts){
    	try {
    		PostMethod post = (PostMethod)method;
			post.setRequestEntity(new MultipartRequestEntity(parts, post.getParams()));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return this;
    }
    
    /** 반드시 in을 닫아줄것! */
    public InputStream asStream(){
    	try {
			return method.getResponseBodyAsStream();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
    }
    
    /** 기본 UTF-8이다. 국내의 경우 EUC-KR로 해야 보이는 경우도 있다. */
    public String asString(){
        return asString(CharEncodeUtil.UTF_8);
    }
    
    public String asString(String encode){
        String result = null;
        InputStream in = null;
        try {
        	in = asStream();
            result = IOUtils.toString(in,encode);
        }
        catch (IOException e) {
            ExceptionUtil.castToRuntimeException(e);
        }finally{
            IOUtils.closeQuietly(in);
        }
        return result;
    }
    
    public void asFile(File result){
    	InputStream in = null;
        try {
        	in = asStream();
            FileUtil.write(in, result);
        }finally{
            IOUtils.closeQuietly(in);
        }
    }
    
    public RESTful query(NameValuePair[] querys) {
    	method.setQueryString(querys);
        return this;
    }

    public RESTful query(String queryString) {
    	method.setQueryString(queryString);
        return this;
    }
    
    // ===========================================================================================
    //                                   static
    // ===========================================================================================
    
    /** URL의 HTML을 파싱해서 이미지파일을 가져옵니다. */
    public static void parseUrlAndSaveImg(String url,final File rootDir,String ... ableExt){
    	String html = RESTful.get(url).run().asString("EUC-KR"); //보통 한국.
		RESTful.parseAndSaveImg(html,rootDir,ableExt);
    }
    
    /** text를 파싱하여 원격지의 img파일을 로컬로 이동시킨다. */
    public static void parseAndSaveImg(String html,final File rootDir,final String ... ableExt){
    	if(!rootDir.exists()) rootDir.mkdirs();
        html = RegEx.TAG_SCRIPT.replace(html,"");
        RegEx.TAG_IMG.process(html, new StringCallback(){
            public void process(String line) {
                String src = RegEx.find("(?<=src=('|\")).*?(?=\\1)", line);
                if(src==null) return; // <img src=http://img.ruliweb.com/image/memo2.gif/> 처럼 ""로 안둘러싸여져 있을때 무시.
                if(ableExt.length!=0 && !StringUtil.isMatchIgnoreCase(src, ableExt)) return;
                String fileName = StringUtil.getLast(src,"/");
                File local = new File(rootDir,fileName);
                RESTful.get(src).run().asFile(local);
            }
        });
    }
    
    /** text를 파싱하여 원격지의 img파일을 로컬로 이동시킨다. + 기존 HTML을 로컬의 url로 치환한다. 특수용도라서 하드코딩~ 나중에 수정하자. */
    public static String parseAndSaveImg(String html,final File webroot, final String targetDirName){
    	final Map<String,String> map = new HashMap<String,String>();
    	html = RegEx.TAG_SCRIPT.replace(html,"");
    	RegEx.TAG_IMG.process(html, new StringCallback(){
    		public void process(String line) {
    			if(!StringUtil.containsIgnoreCase(line,".jpg")) return;
    			String src = RegEx.find("(?<=src=('|\")).*?(?=\\1)", line);
    			if(StringUtil.contains(src, "userfiles")) return; //내 파일이면 무시. 
    			String fileName = StringUtil.getLast(src,"/");
    			String filePath = targetDirName+"/"+fileName;
    			File local = new File(webroot,filePath);
    			RESTful.get(src).run().asFile(local);
    			map.put(src, filePath.replaceAll("\\\\","/")); //윈도우형을 유닉스형으로 바꿔줌.
    		}
    	});
    	for(Entry<String,String> entry : map.entrySet()){
    		html = html.replaceAll(StringEscapeUtil.escapeRegEx(entry.getKey().toString()),
    				StringEscapeUtil.escapeRegEx(entry.getValue().toString()));
    	}
    	return html;
    }    


}
