
package erwins.util.spring;

import java.io.UnsupportedEncodingException;
import java.text.MessageFormat;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;

/**
 * 파일 첨부가 가능한 스프링 메일 
 * commons메일 때문에 사용할일은 없을듯. 
 * SpringMailHelper helper = new SpringMailHelper(HOST);
        helper.setFrom("영감님", FROM,"");
        helper.send(TO,"hellow","asd {0}","id");
 */
public class SpringMailHelper{

    private JavaMailSenderImpl sender;
    private Log log = LogFactory.getLog(getClass());

    private String fromName;
    private String fromMail;
    private String css;

    /**
     * 메일 서버의 IP를 지정한다.
     */
    public SpringMailHelper(String host) {
        sender = new JavaMailSenderImpl();
        sender.setHost(host);
    }

    public void setFrom(String fromName, String fromMail,String css) {
        this.fromName = fromName;
        this.fromMail = fromMail;
        this.css = css;
    }
    
    /**
     * 주의!!  templit에 '를 넣지 말것.. \"로 대체하라.
     * 내부적으로 MessageFormat이 사용된다.
     */    
    public void send(String toMail, String subject,String templit, Object... values) {
        MimeMessage message = sender.createMimeMessage();
        try {
            MimeMessageHelper messageHelper = new MimeMessageHelper(message, true, "euc-kr");
            messageHelper.setSubject(subject);
            
            StringBuilder htmlContent = new StringBuilder();
            htmlContent.append("<html>");
            htmlContent.append("<body>");
            htmlContent.append("<head><meta http-equiv='Content-Type' content='text/html; charset=euc-kr' />");
            htmlContent.append("<title>"+subject+"</title>");
            htmlContent.append(css);
            htmlContent.append(MessageFormat.format(templit, values));
            htmlContent.append("</body>");
            htmlContent.append("</html>");
            messageHelper.setText(htmlContent.toString(), true);
            messageHelper.setFrom(fromMail, fromName);
            //messageHelper.setTo(new InternetAddress("mail", "member.getName()", "euc-kr")); ??
            messageHelper.setTo(toMail);

            //DataSource dataSource = new FileDataSource("attachmentfile/회원가입안내문.doc");
            //messageHelper.addAttachment(MimeUtility.encodeText("회원가입안내문.doc", "euc-kr", "B"), dataSource);
        }
        catch (MessagingException e) {
            log.warn("fail to MimeMessage", e);
            return;
        }
        catch (UnsupportedEncodingException e) {
            log.warn("fail to MimeMessage", e);
            return;
        }
        try {
            sender.send(message);
            if (log.isDebugEnabled()) {
                log.debug("Sent mail successfully!");
            }
        }
        catch (MailException e) {
            log.warn("fail to send MimeMessage:" + e.getMessage(), e);
        }
    }

}
