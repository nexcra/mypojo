package erwins.util.vender.spring;

import org.springframework.mock.web.MockServletContext;

/**
 * 테스트용 설정파일에 붙여넣기 하세요.
 * ex) <bean id="servletContext" class="erwins.util.vender.spring.SpringMockServletContext" p:realPath="" /> 
 * 파라메터는 WEB-INF 직전까지의 경로 입력    ex) D:/~~~~/~~~/
 */
public class SpringMockServletContext extends MockServletContext{
	
	private String realPath;
	
	public void setRealPath(String realPath) {
		if(!realPath.endsWith("/")) realPath += "/";
		this.realPath = realPath;
	}

	@Override
	public String getRealPath(String path) {
		return realPath + path;
	}
	
}
