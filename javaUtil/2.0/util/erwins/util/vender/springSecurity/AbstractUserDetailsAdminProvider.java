package erwins.util.vender.springSecurity;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

/**
 * 원래 프로바이더 앞에 와야 한다. 
 * 비번 정합 여부와 상관없이 강제 로그인을 하기 위한 로직이다.
 * 반드시 강력한 수단을 통해서 인증해야 한다. */
public abstract class AbstractUserDetailsAdminProvider implements AuthenticationProvider{

	private UserDetailsService userDetailsService;
	
	/** null을 리턴하면 다음 프로바이더로 작업을 넘긴다. */
	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		
		String username = (authentication.getPrincipal() == null) ? "NONE_PROVIDED" : authentication.getName();
		
		if(!preConditionChecks((UsernamePasswordAuthenticationToken) authentication)) return null;
		
		UserDetails user = userDetailsService.loadUserByUsername(username);
		if(user==null) return null; //예외로 처리되어야 한다. 여기서는 처리하지 않는다.
		
		Object details = authenticationChecks(user, (UsernamePasswordAuthenticationToken) authentication);
		
		UsernamePasswordAuthenticationToken result = new UsernamePasswordAuthenticationToken(user,
                authentication.getCredentials(), user.getAuthorities());
        result.setDetails(details);
		return result;
	}
	
	/** 강제 로그인 가능한지 체크. 이 체크 이후  */
	protected abstract boolean preConditionChecks(UsernamePasswordAuthenticationToken authentication);
	
	/** 인증 성공하면 Details를 리턴한다. 이 detail을 가지고 다른 토큰(WebAuthenticationDetails)과 구분하자.  
	 * 인증 실패시 null 리턴 */
	protected abstract Object authenticationChecks(UserDetails userDetails,UsernamePasswordAuthenticationToken authentication);
	

	@Override
	public boolean supports(Class<?> authentication) {
		return (UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication));
	}
	

	public UserDetailsService getUserDetailsService() {
		return userDetailsService;
	}

	public void setUserDetailsService(UserDetailsService userDetailsService) {
		this.userDetailsService = userDetailsService;
	}
	
	


    
    
	
}
