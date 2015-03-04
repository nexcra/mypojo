package erwins.util.spring.security;

import java.util.Collection;
import java.util.Set;

import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.SecurityConfig;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.google.common.base.Splitter;
import com.google.common.collect.Sets;

public abstract class SecurityUtil{
    
	/** 가지고있는 권한중 하나라도 설정된 권한과 일치한다면, 통과한다. */
	public static boolean isAble(Collection<? extends GrantedAuthority> auths,Collection<? extends ConfigAttribute> configs){
		for(GrantedAuthority auth : auths){
			String authString = auth.getAuthority();
			for(ConfigAttribute config : configs){
				if(config.getAttribute().equals(authString)) return true;
			}
		}
		return false;
	}
	
	/** 간단 변환 */
	public static ConfigAttribute authToConfig(GrantedAuthority auth){
		return new SecurityConfig(auth.getAuthority());
	}
	
	/** ,로 연결된 권한을 집합으려 변경한다. 
	 * 일반적인 경우에는 권한그룹을 사용함으로 사용되지 않는다 */
	public static Set<GrantedAuthority> stringToAuth(String roleString){
		Iterable<String> roles = Splitter.on(',').omitEmptyStrings().trimResults().split(roleString);
		Set<GrantedAuthority> authorities = Sets.newHashSet();
		for(String each : roles) authorities.add(new SimpleGrantedAuthority(each));
		return authorities;
	}
	
	/** 권한에 해당 계정 ID를 ROLE에 추가하면, 임시ACL같은 코딩을 작성할 수 있다. (자신과 관리자 권한만 수정가능) */
    public static void addDefaultRole(UserDetails user) {
    	@SuppressWarnings("unchecked")
		Collection<GrantedAuthority> authorities = (Collection<GrantedAuthority>) user.getAuthorities();
        authorities.add( getDefaultRole(user.getUsername()));
    }
    
    /** username(로그인한 유니크ID)으로 기본권한 생성  */
    public static GrantedAuthority getDefaultRole(String username) {
    	return new SimpleGrantedAuthority("USER_" + username);
    }
    
	
}
