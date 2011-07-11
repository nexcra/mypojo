package erwins.webapp.myApp.translator;

import org.apache.commons.lang.RandomStringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.View;

import erwins.util.lib.FormatUtil;
import erwins.util.lib.RandomStringUtil;
import erwins.util.lib.StringUtil;
import erwins.util.lib.security.Cryptor;
import erwins.util.lib.security.MD5s;
import erwins.webapp.myApp.AjaxView;

@Controller
@RequestMapping("/translator/*")
public class TranslatorController {
	
	@RequestMapping("/page")
	public String page() {
		return "translator/page";
	}
	@RequestMapping("/hash")
    public View getHash(@RequestParam(defaultValue="") String HASH_VALUE){
        String hashed = MD5s.hash(HASH_VALUE) ;
        return new AjaxView(hashed);
    }
	@RequestMapping("/encrypt")
	public View encrypt(@RequestParam(defaultValue="") String ENCRYPT_VALUE
			,@RequestParam(defaultValue="erwins") String ENCRYPT_KEY){
		Cryptor c = new Cryptor().generateKeyByString(ENCRYPT_KEY);
		String en = c.encryptBase64(ENCRYPT_VALUE); 
		return new AjaxView(en);
	}
	@RequestMapping("/decrypt")
	public View decrypt(@RequestParam(defaultValue="") String DECRYPT_VALUE
			,@RequestParam(defaultValue="erwins") String DECRYPT_KEY){
		Cryptor c = new Cryptor().generateKeyByString(DECRYPT_KEY);
		String en = c.decryptBase64(DECRYPT_VALUE); 
		return new AjaxView(en);
	}
	@RequestMapping("/randomBid")
	public View randomBid(){
		return new AjaxView(FormatUtil.toBid(RandomStringUtil.makeRandomBid()));
	}
	@RequestMapping("/randomSid")
	public View randomSid(){
		//return new AjaxView(FormatUtil.toSid(RandomStringUtil.makeRandomSid()));
		return new AjaxView(FormatUtil.toSid(getSid()));
	}
	/** Days가 안되서 걍 하드코딩 */ 
	private String getSid() {
		int yy = 12;
		while (true) {
            String bid = RandomStringUtils.randomNumeric(13);
            int birth = Integer.parseInt(bid.substring(0,2));
            if (StringUtil.isSid(bid)) {
                int value = Integer.parseInt(String.valueOf(bid.charAt(6)));
                int age = yy - birth;
                switch(value){
                    case 1: case 2: age += 100; break;
                    case 3: case 4: break;
                    default : continue;
                }
                if(age > 20 && age < 60) return bid;
            }
        }
	}
}
