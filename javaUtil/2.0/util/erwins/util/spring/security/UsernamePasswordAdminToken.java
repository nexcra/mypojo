package erwins.util.spring.security;

import java.util.Collection;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

@SuppressWarnings("serial")
public class UsernamePasswordAdminToken extends UsernamePasswordAuthenticationToken{

	/** 인증 완료만 있다. */
	public UsernamePasswordAdminToken(Object principal, Object credentials, Collection<? extends GrantedAuthority> authorities) {
		super(principal, credentials, authorities);
	}
	
	
    
	
}
