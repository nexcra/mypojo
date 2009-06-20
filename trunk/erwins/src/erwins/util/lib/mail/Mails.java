
package erwins.util.lib.mail;

import java.text.MessageFormat;

import org.apache.commons.mail.*;

import erwins.util.lib.Encoders;

/**
 * commons mail을 확장한다.
 */
public class Mails {

    private HtmlEmail email;
    private String[] cssList;

    /**
     * 메일 서버의 IP를 지정한다.
     */
    public Mails(String host,String ... cssList) {
        email = new HtmlEmail();
        email.setHostName(host);
        this.cssList = cssList;
    }

    public void setFrom(String fromName, String fromMail){
        try {
            email.setFrom(fromMail, fromName);
        }
        catch (EmailException e) {
            Encoders.stackTraceTo(e);
        }
    }
    
    /**
     * 여러 놈을 지정 가능 
     */
    public void addTo(String toName, String toMail){
        try {
            email.addTo(toMail, toName);
        }
        catch (EmailException e) {
            Encoders.stackTraceTo(e);
        }
    }
    /**
     * 만약 일반경로 사용시 보안적용이 된 메일브라우저는 이미지를 차단할 것이다. 
     * 이를 방지할려면 다음과 같이 메일 자체에 이미지를 첨부시키자.
     * 추후 replace ALL로 변경할것!
     */
    protected String getImgStr(String imageUrl) throws EmailException {
        //URL url = new URL(imageUrl);
        String cid = email.embed(imageUrl, "Apache logo");
        return "<img src=\"cid:"+cid+"\">";
    }

    /**
     * serverFilePath : 확장자를 포함함 풀 경로를 적어준다.
     * 여러개의 파일 등록이 가능하다.
     */
    public void setFile(String serverFilePath,String toFileName,String desc){
        EmailAttachment attachment = new EmailAttachment();
        attachment.setPath(serverFilePath);
        attachment.setDisposition(EmailAttachment.ATTACHMENT);
        attachment.setDescription("good~.");
        attachment.setName(toFileName); //한글 안됨
        try {
            email.attach(attachment);
        }
        catch (EmailException e) {
            Encoders.stackTraceTo(e);
        }
    }
    
    /**
     * 주의!!  templit에 '를 넣지 말것.. \"로 대체하라.
     * 내부적으로 MessageFormat이 사용된다.
     */    
    public void send(String subject,String templit, Object... values) {        
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
        
        try {
            email.setHtmlMsg(htmlContent.toString());
            //HTML 이메일을 지원하지 않는 클라이언트라면 다음 메세지를 뿌려웁니다?
            //보안 설정이 안되어있는 클라이언트한테 나타나는듯..
            //email.setTextMsg("Your email client does not support HTML messages");
            email.send();
        }
        catch (EmailException e) {
            Encoders.stackTrace(e);
            //아무것도 하지 않음.
        }
    }

}
