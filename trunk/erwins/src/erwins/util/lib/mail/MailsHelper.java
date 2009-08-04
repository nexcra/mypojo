
package erwins.util.lib.mail;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import erwins.util.tools.SystemInfo;
import erwins.util.vender.spring.SpringMailHelper;

/**
 * commons mail을 확장한다. 디폴드 보내는 ~~를 지정한다.
 */
@SuppressWarnings("unused")
public class MailsHelper extends Mails {

    public MailsHelper(String host) {
        super(host);
    }
    
    private Log log = LogFactory.getLog(getClass());
    
    private static final String CSS = "<style>body {margin:0px;}body, table, tr, td, div, th, form{color:#626262; font-family:돋움,돋움체,verdana,tahoma,arial; font-size:12px; line-height:18px;}a:{color:#626262;}a:link         {color:#626262;text-decoration:none;}a:visited      {color:#626262;text-decoration:none;}a:active     {color:#626262;text-decoration:none;}a:hover      {color:#F67F00;text-decoration:none;}.tbl_title_green {color:#247e04; font-size:12px; font-weight:bold;}</style>";
    private static final String HEADER ;
    private static final String FOOTER ;
    private static final String FIND_ID ;
    private static final String FIND_PW ;
    private static final String APPROVAL_JOIN ;
    private static final String PATH = "http://"+SystemInfo.getServerIp();
    private static final String FROM_NAME = "폐기물부담금 관리자";
    private static final String FROM_MAIL = "cdp@envico.or.kr";
    private static final String TITLE = "폐기물 부담금시스템에서 알려드립니다.";
    
    /**
     * @uml.property  name="helper"
     * @uml.associationEnd  
     */
    private static SpringMailHelper helper = new SpringMailHelper("210.104.107.5");
    
    static{
        HEADER ="<table width=\"650\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\"><tr><td>"
        +"<img src=\""+PATH+"/images/mail/mail_top01.gif\" width=\"650\" height=\"233\"></td>"
        +"</tr><tr><td><img src=\""+PATH+"/images/mail/mail_top02.gif\" width=\"650\" height=\"100\"></td></tr><tr>"
        +"<td background=\""+PATH+"/images/mail/mail_bg.gif\" style=\"padding:20px 50px 10px 63px;\">";
        
        FOOTER = "<br> <p>이 메일은 발신 전용입니다.<br>" 
        +"서비스 이용시 궁금한 점이나 불편사항이 있으면 고객상담센터로 문의주시기 바랍니다.</p></td></tr>"
        +"<tr><td><img src=\""+PATH+"/images/mail/mail_bottom.gif\" width=\"650\" height=\"121\">"
        +"</td></tr></table>";
        
        FIND_ID = "<p>고객님이 문의하신 <b>아이디</b>에 대해서 알려드립니다.<br>" 
            +"고객님의 아이디입니다.</p><p><b>아이디 :</b> <span class=\"tbl_title_green\">{0}</span></p>";
        
        FIND_PW = "<p>고객님이 문의하신 <b>비밀번호</b>에 대해서 알려드립니다.<br>" +
                    "임시 비밀번호입니다.</p><p><b>비밀번호 :</b> <span class='tbl_title_green'>{0}</span></p>" +
                    "<p>발급된 임시비밀번호로 로그인 후 비밀번호 도용방지를 위해 비밀번호를 변경해 주시기 바랍니다.</p>";
        
        APPROVAL_JOIN = "<p>고객님의  <b>회원가입</b>이 정상적으로 승인되었습니다.<br>" +
                        "</p><p>로그인 하실 <b> ID </b> : <span class='tbl_title_green'>{0}</span></p>" +
                         "<p>발급된 ID로 로그인 후 사용하시길 바랍니다.</p>";
        
        helper.setFrom(FROM_NAME, FROM_MAIL,CSS);
    }    

}
