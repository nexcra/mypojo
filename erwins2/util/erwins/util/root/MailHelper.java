
package erwins.util.root;

import java.io.File;
import java.text.MessageFormat;

import org.apache.commons.mail.EmailAttachment;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;

/**
 * commons mail을 확장한다.
 * 상세 구문은 템플릿은 Groovy를 이용할것!
 * ex) MailHelper mail = new MailHelper(HOST);
        mail.setFrom("영감님", FROM);
        mail.addTo("Me", FROM);
        mail.addTo("zzz", TO);
        mail.addFile("d:/o123.zip", "123.zip");
        mail.send("Apache", "2nd - <img src=\"http://www.apache.org/images/asf_logo_wide.gif\"> {0}", "zzz");
 */
public class MailHelper {

    private HtmlEmail email;
    private String[] cssList;

    /**
     * 메일 서버의 IP를 지정한다.
     */
    public MailHelper(String host,String ... cssList) {
        email = new HtmlEmail();
        email.setHostName(host);
        this.cssList = cssList;
    }

    public void setFrom(String fromName, String fromMail){
        try {
            email.setFrom(fromMail, fromName);
        }
        catch (EmailException e) {
            throw new RuntimeException(e);
        }
    }
    
    /** 디폴트가 UTF-8인듯? */
    public void setCharset(String charset){
    	email.setCharset(charset);
    }
    
    /**
     * 여러 놈을 지정 가능 
     */
    public void addTo(String toName, String toMail){
        try {
            email.addTo(toMail, toName);
        }
        catch (EmailException e) {
            throw new RuntimeException(e);
        }
    }
    /**
     * 만약 일반경로 사용시 보안적용이 된 메일브라우저는 이미지를 차단할 것이다. 
     * 이를 방지할려면 다음과 같이 메일 자체에 이미지를 첨부시키자.
     * 추후 replace ALL로 변경할것!
     */
    public String buildImgTag(File img,String append) throws EmailException {
        //String cid = email.embed(imageUrl, "Apache logo");
    	String cid = email.embed(img);
        return "<img src=\"cid:"+cid+"\" "+append+" >";
    }

    /**
     * serverFilePath : 확장자를 포함함 풀 경로를 적어준다. 한글이 안되니 주의!
     */
    public void addFile(String serverFilePath,String toFileName){
        EmailAttachment attachment = new EmailAttachment();
        attachment.setPath(serverFilePath);
        attachment.setDisposition(EmailAttachment.ATTACHMENT);
        //attachment.setDescription(desc);
        attachment.setName(toFileName);
        try {
            email.attach(attachment);
        }
        catch (EmailException e) {
            throw new RuntimeException(e);
        }
    }
    
    /**
     * 주의!!  templit에 '를 넣지 말것.. \"로 대체하라.
     * 내부적으로 MessageFormat이 사용된다.
     * 예외가 자주 일어날 수 있음으로 잔드시 잡아줄것 확인할것. 
     * @throws EmailException 
     */    
    public void send(String subject,String templit, Object... values) throws EmailException {        
        email.setSubject(subject);
        
        StringBuilder htmlContent = new StringBuilder();
        htmlContent.append("<html>");
        htmlContent.append("<body>");
        htmlContent.append("<head><meta http-equiv='Content-Type' content='text/html; charset=euc-kr' />");
        htmlContent.append("<title>"+subject+"</title>");
        for(String css : cssList) htmlContent.append(css);
        htmlContent.append(MessageFormat.format(templit, values));
        htmlContent.append("</body>");
        htmlContent.append("</html>");
        
        email.setHtmlMsg(htmlContent.toString());
        //HTML 이메일을 지원하지 않는 클라이언트라면 다음 메세지를 뿌려웁니다?
        //보안 설정이 안되어있는 클라이언트한테 나타나는듯..
        //email.setTextMsg("Your email client does not support HTML messages");
        email.send();

    }

}
