package erwins.util.vender.spring;

import java.io.UnsupportedEncodingException;

import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;


/**
 * @author  Administrator
 */
public class Mail{

    private JavaMailSender mailSender;

    /**
     * @param mailSender
     * @uml.property  name="mailSender"
     */
    @Autowired
    public void setMailSender(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }
    
    private Log log = LogFactory.getLog(getClass());
    
    protected void sendEmailTo(String email,String name) {
        MimeMessage message = mailSender.createMimeMessage();
        try {
            MimeMessageHelper messageHelper = new MimeMessageHelper(message,
                    true, "euc-kr");
            messageHelper.setSubject("회원 가입 안내 [Attachemtn]");
            String htmlContent = "<strong>안녕하세요</strong>, 반갑습니다.";
            messageHelper.setText(htmlContent, true);
            messageHelper.setFrom("erwins@nate.com", "영감님");
            messageHelper.setTo(new InternetAddress(email, name, "euc-kr"));

            //DataSource dataSource = new FileDataSource("attachmentfile/회원가입안내문.doc");
            //messageHelper.addAttachment(MimeUtility.encodeText("회원가입안내문.doc","euc-kr", "B"), dataSource);
        } catch (MessagingException e) {
            log.warn("fail to MimeMessage", e);
            return;
        } catch (UnsupportedEncodingException e) {
            log.warn("fail to MimeMessage", e);
            return;
        }
        try {
            mailSender.send(message);
            if (log.isDebugEnabled()) {
                log.debug("Sent mail successfully!");
            }
        } catch (MailException e) {
            log.warn("fail to send MimeMessage:" + e.getMessage(), e);
        }
    }

}
