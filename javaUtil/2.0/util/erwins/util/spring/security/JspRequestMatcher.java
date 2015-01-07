package erwins.util.spring.security;

import javax.servlet.http.HttpServletRequest;

import org.springframework.security.web.util.RequestMatcher;

import erwins.util.web.WebUtil;


/** 
 * 요청이 ajax가 아니라면 통과한다.
 * 스프링 시큐리티에서 권한 실패시 엔트리 포인트로 이동하는데, 그때 저장할 request를 선별하는데 사용한다.
 * 즉 ajax요청이 들어왔다면 엔트리 포인트로 이동시, request를 저장하지 않는다.
 *  */
@Deprecated
public class JspRequestMatcher implements RequestMatcher {

    public boolean matches(HttpServletRequest request) {
    	boolean ajax = WebUtil.isAjax(request);
        return !ajax;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof JspRequestMatcher;
    }

    @Override
    public int hashCode() {
        return 1;
    }
	
}
