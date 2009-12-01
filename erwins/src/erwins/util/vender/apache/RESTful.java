
package erwins.util.vender.apache;

import java.io.*;
import java.util.Map.Entry;

import org.apache.commons.httpclient.*;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.io.IOUtils;

import erwins.util.exception.Throw;
import erwins.util.lib.*;
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
    
    private InputStream in = null;
    private boolean post = true;
    
    private NameValuePair[] querys;
    private String queryString;
    
    
    /**  기본은 true이다. */
    public void setPost(boolean post) {
        this.post = post;
    }

    public RESTful query(NameValuePair[] querys) {
        this.querys = querys;
        return this;
    }

    public RESTful query(String queryString) {
        this.queryString = queryString;
        return this;
    }
    
    /**
     * 첨부파일 등록시.
     *  File targetFile = new File(upLoadPath);
   FilePart filePart = new FilePart("upfile", targetFile, null, "EUC-KR"); //EUC-KR
   Part[] parts = { filePart  };
   post.setRequestEntity(new MultipartRequestEntity(parts, post.getParams()));
     * @param url
     * @return
     */

    public RESTful build(String url){
        HttpMethod method = null;
        if(post) method = new  PostMethod(url);
        else method = new  GetMethod(url);
        
        if(querys!=null) method.setQueryString(querys);
        else if(queryString!=null)method.setQueryString(queryString);
        
        try {
            client.executeMethod(method);
            in =  method.getResponseBodyAsStream();
        }
        catch (HttpException e) {
            Throw.wrap(e);
        }
        catch (IOException e) {
            Throw.wrap(e);
        }
        return this;
    }
    
    /** 반드시 in을 닫아줄것! */
    public InputStream asStream(){
        return in;
    }
    
    /** 기본 UTF-8이다. 국내의 경우 EUC-KR로 해야 보이는 경우도 있다. */
    public String asString(){
        return asString(CharSets.UTF_8);
    }
    
    public String asString(String encode){
        String result = null;
        try {
            //result = IOUtils.toString(in);
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
        try {
            Files.write(in, result);
        }finally{
            IOUtils.closeQuietly(in);
        }
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
                new RESTful().build(src).asFile(local);
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
