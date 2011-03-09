
package erwins.util.web;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

//<listener><listener-class>클래스풀경로</listener-class></listener>
/**
 * 세션 생성을 감시한다. WEB-INF에 등록 후 사용하자. 이 등록되는 객체는 완전 별개인 유령객체임.
 * 스프링에서 리스너를 지원 안하는듯~ 어쩔수 없이 static을 사용
 * @author  erwins(quantum.object@gmail.com)
 */
public abstract class AbstractDomainContext  implements HttpSessionListener{
    
    protected Log log = LogFactory.getLog(this.getClass());
    protected static long cumulatedSessionCount = 0;
    private static Map<String, HttpSession> sessions = new HashMap<String, HttpSession>();

    public synchronized void  sessionCreated(HttpSessionEvent evt) {
        HttpSession session = evt.getSession();
        cumulatedSessionCount++;
        sessions.put(session.getId(), session);
        log.debug("session Created");
    }

    public synchronized void sessionDestroyed(HttpSessionEvent evt) {
        HttpSession session = evt.getSession();
        sessions.remove(session.getId());
        log.debug("session Destroyed");
    }
    
    public long getNowSessionCount() {
    	return sessions.size();
    }
    
    /** 누적 생성되어있는 세션 수 : 즉 전체 방문자 수 */
    public long getCumulatedSessionCount() {
        return cumulatedSessionCount;
    }
    
    /** 초기화에 사용 */
    @SuppressWarnings("static-access")
	public void setCumulatedSessionCount(long cumulatedSessionCount) {
		this.cumulatedSessionCount = cumulatedSessionCount;
	}

	public Collection<HttpSession> getSessions(){
        return sessions.values();
    }
    
}
