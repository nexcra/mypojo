package erwins.util.tools;

import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.HttpServletRequest;

import org.openid4java.consumer.ConsumerException;
import org.openid4java.consumer.ConsumerManager;
import org.openid4java.consumer.InMemoryConsumerAssociationStore;
import org.openid4java.consumer.InMemoryNonceVerifier;
import org.openid4java.consumer.VerificationResult;
import org.openid4java.discovery.Discovery;
import org.openid4java.discovery.DiscoveryException;
import org.openid4java.discovery.DiscoveryInformation;
import org.openid4java.discovery.Identifier;
import org.openid4java.message.AuthRequest;
import org.openid4java.message.MessageException;
import org.openid4java.message.ParameterList;

import erwins.util.lib.StringUtil;

public abstract class OpenIDHelper {
	
	private ConsumerManager manager;
	private Discovery discovery = new Discovery();
	private static final String SESSION_KEY = "openid-discover";
	private static final String GOOGLE_DISCOVER = "https://www.google.com/accounts/o8/id";
    
    protected abstract String getReturnUrl();
    protected abstract String getTrustRoot();
    
    /** 일단 사용 안함. */
    public static enum OpenIDProvider{
    	MY_ID("{0}.myid.net"),
    	DAUM("openid.daum.net/{0}");
    	private final String template;
    	private OpenIDProvider(String template){
    		this.template = template;
    	}
    	public String getUrl(String id){
    		return StringUtil.format(template,  id);
    	}
    }
	
	public OpenIDHelper(){
		try {
			manager = new ConsumerManager();
		} catch (ConsumerException e) {
			throw new RuntimeException(e);
		}
		//인증기관에 인증하는동안 요청을 저장할 장소.  클러스터링 되어있다면 DB나 RMI등  적절한 통신 필요. 
        manager.setAssociations( new InMemoryConsumerAssociationStore() );
        // 인증 응답 메시지의 nonce 를 추적. 제한시간 있음.
        manager.setNonceVerifier( new InMemoryNonceVerifier( (int) TimeUnit.HOURS.toSeconds(1)));
	}
	
	/** 이상한 url을 넣어도 표준에 맞게 변형해 준다. 인증요청은 세션에 저장. 
	 *  DiscoveryException : 이상한 URL일때 던진다.
	 *  ConsumerException : openId 사이트를 찾을 수 없을때? 던진다. 
	 *  사실 둘다 이상한거일때 던지는듯.*/
	@SuppressWarnings("rawtypes")
	public String openIdRedirect(HttpServletRequest req, String userInput) throws DiscoveryException,ConsumerException {
		AuthRequest auth;
		try {
			Identifier identifier = discovery.parseIdentifier(userInput);
			String identifierUrl = identifier.getIdentifier();
			
			List discoveries = manager.discover(identifierUrl);
			DiscoveryInformation discovered = manager.associate(discoveries);
			
			req.getSession().setAttribute(SESSION_KEY, discovered);
			auth = manager.authenticate(discovered, getReturnUrl(),getTrustRoot());
		} catch (MessageException e) {
			throw new RuntimeException(e);
		}
        return auth.getDestinationUrl(true);
	}
	
	@SuppressWarnings("rawtypes")
	public String googleRedirect(HttpServletRequest req){
		AuthRequest auth;
		try {
			List discoveries = manager.discover(GOOGLE_DISCOVER);
			DiscoveryInformation discovered = manager.associate(discoveries);
			req.getSession().setAttribute(SESSION_KEY, discovered);
			auth = manager.authenticate(discovered,getReturnUrl(),getTrustRoot());
		} catch (MessageException e) {
			throw new RuntimeException(e);
		} catch (ConsumerException e) {
			throw new RuntimeException(e);
		} catch (DiscoveryException e) {
			throw new RuntimeException(e);
		}
		return auth.getDestinationUrl(true);
	}
	
	/** 인증 외에 닉네임 등의 부가 정보는 안주느듯.. 요청 해야 하나?
	 * openID의 경우 ID를 리턴하고, Google의 경우 ID를 URL과 같이 리턴한다. 이 ID는 메일계정으로 변환 가능하다. (어케하는지는??) */
	public String verification(HttpServletRequest req){
		ParameterList paramList = new ParameterList(req.getParameterMap());
		DiscoveryInformation di = (DiscoveryInformation) req.getSession().getAttribute("openid-discover");
		if (di == null) return null;

		String receiveURL = req.getRequestURL() + "?" + req.getQueryString();
		VerificationResult verification = null;
		try {
			verification = manager.verify(receiveURL, paramList, di);
			if (verification == null) return null;
			Identifier id = verification.getVerifiedId();
			if (id == null)  return null;
			return id.getIdentifier();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	


}
