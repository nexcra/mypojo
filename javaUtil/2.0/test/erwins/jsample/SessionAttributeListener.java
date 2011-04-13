
package erwins.jsample;

import javax.servlet.http.HttpSessionAttributeListener;
import javax.servlet.http.HttpSessionBindingEvent;

/**
 * Web-Inf에 등록할 필요가 없음
 * <listener>
        <listener-class>erwins.domain.user.User</listener-class>
    </listener>
 * @author erwins(quantum.object@gmail.com)
 **/
public class SessionAttributeListener implements HttpSessionAttributeListener{
    
    public void attributeAdded(HttpSessionBindingEvent arg0) {
        //Object obj = arg0.getValue();
        //if(obj instanceof User)  ~ do  
            
    }
    public void attributeRemoved(HttpSessionBindingEvent arg0) {
        
    }
    public void attributeReplaced(HttpSessionBindingEvent arg0) {
        
    }
  
}
