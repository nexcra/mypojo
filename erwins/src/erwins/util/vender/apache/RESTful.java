
package erwins.util.vender.apache;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map.Entry;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.io.IOUtils;

import erwins.util.exception.Throw;
import erwins.util.lib.CharSets;
import erwins.util.lib.Encoders;
import erwins.util.lib.Files;
import erwins.util.lib.RegEx;
import erwins.util.lib.Strings;
import erwins.util.root.StringCallback;
import erwins.util.tools.Mapp;



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
    public static RESTful get(String url){
    	RESTful instance = new RESTful();
    	instance.method = new  GetMethod(url);
    	return instance;
    }
    
    /** url 사이의 port는 테스트 해바야함. */
    public RESTful run(){
        try {
            client.executeMethod(method);
        }
        catch (IOException e) {
            Throw.wrap(e);
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
        return asString(CharSets.UTF_8);
    }
    
    public String asString(String encode){
        String result = null;
        InputStream in = null;
        try {
        	in = asStream();
            result = IOUtils.toString(in,encode);
        }
        catch (IOException e) {
            Throw.wrap(e);
        }finally{
            IOUtils.closeQuietly(in);
        }
        return result;
    }
    
    public void asFile(File result){
    	InputStream in = null;
        try {
        	in = asStream();
            Files.write(in, result);
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
    
    /** text를 파싱하여 원격지의 img파일을 로컬로 이동시킨다. */
    public static String parseAndSaveImg(String html,final File webroot, final String path){
        final Mapp map = new Mapp();
        html = RegEx.TAG_SCRIPT.replace(html,"");
        RegEx.TAG_IMG.process(html, new StringCallback(){
            public void process(String line) {
                String src = RegEx.find("(?<=src=('|\")).*?(?=\\1)", line);
                if(Strings.contains(src, "userfiles")) return; //내 파일이면 무시. 
                String fileName = Strings.getLast(src,"/");
                String filePath = path+"/"+fileName;
                File local = new File(webroot,filePath);
                RESTful.get(src).asFile(local);
                map.put(src, filePath.replaceAll("\\\\","/")); //윈도우형을 유닉스형으로 바꿔줌.
            }
        });
        for(Entry<Object,Object> entry : map.entrySet()){
            html = html.replaceAll(Encoders.escapeRegEx(entry.getKey().toString()),
                    Encoders.escapeRegEx(entry.getValue().toString()));
        }
        return html;
    }    


}
