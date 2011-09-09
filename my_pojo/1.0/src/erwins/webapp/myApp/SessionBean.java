package erwins.webapp.myApp;

import java.io.Serializable;

import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;

@SuppressWarnings("serial")
@Component
@Scope(value = "session", proxyMode = ScopedProxyMode.TARGET_CLASS)
/** 전부 Serializable해야한다. 로그 같은거 넣지 말자. */
public class SessionBean implements Serializable{
	
	private boolean first = true;
	public boolean isFirst() {
		return first;
	}
	public void setFirst(boolean first) {
		this.first = first;
	}
	

}
