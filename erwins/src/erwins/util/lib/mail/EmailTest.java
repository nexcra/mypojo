package erwins.util.lib.mail;

import erwins.util.vender.spring.SpringMailHelper;


/**
 * 테스트를 해볼수가 없다. ㅠㅠ 
 */
public class EmailTest{
    
    private static final String FROM = "shsin@sundosoft.com";
    private static final String TO = "erwins@nate.com";
    private static final String HOST = "210.104.107.5";
    
    //@Test
    public void springMail() throws Exception{
        SpringMailHelper helper = new SpringMailHelper(HOST);
        helper.setFrom("영감님", FROM,"");
        helper.send(TO,"hellow","asd {0}","id");
    }
    
    //@Test
    public void sendMail() throws Exception{
        Mails mail = new Mails(HOST);
        mail.setFrom("영감님", FROM);
        mail.addTo("Me", FROM);
        mail.addTo("zzz", TO);
        //mail.setFile("d:/o123.zip", "123.zip", "good~.");
        //mail.setFile("d:/o123.zip", "1234.zip", "good~.");
        mail.send("Apache", "2nd - <img src=\"http://www.apache.org/images/asf_logo_wide.gif\"> {0}", "zzz");
    }
    

}
