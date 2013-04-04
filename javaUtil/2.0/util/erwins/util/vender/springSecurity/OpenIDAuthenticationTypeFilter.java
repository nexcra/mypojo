package erwins.util.vender.springSecurity;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.security.openid.OpenIDAuthenticationFilter;


public class OpenIDAuthenticationTypeFilter extends OpenIDAuthenticationFilter{
    
    private static final String GOOGLE_DISCOVER = "https://www.google.com/accounts/o8/id";
	
    /** 디폴트 설정 */
    private String openIdType = "openIdType";
    private Map<String,String> providers = new HashMap<String,String>();
    
    /** 기본 타입들 추가. */
    @Override
    public void afterPropertiesSet() {
        super.afterPropertiesSet();
        if(!providers.containsKey("myid")) providers.put("myid", "{0}.myid.net");
    }
    
    
    /** 타입에 맞게 포매팅 해준다. */
	@Override
    protected String obtainUsername(HttpServletRequest req) {
	    String userName = super.obtainUsername(req);
	    
	    String type = req.getParameter(openIdType);
	    if("google".equals(type)) return GOOGLE_DISCOVER;
	    
	    String template = providers.get(type);
	    if(template==null) return userName;
	    
	    return MessageFormat.format(template, userName);
    }

    public String getOpenIdType() {
        return openIdType;
    }

    public void setOpenIdType(String openIdType) {
        this.openIdType = openIdType;
    }

    public Map<String, String> getProviders() {
        return providers;
    }

    public void setProviders(Map<String, String> providers) {
        this.providers = providers;
    }

}
