
package erwins.webapp.myApp;

import erwins.util.morph.MapToBean;
import erwins.webapp.myApp.user.SessionInfo;

/**
 * static을 모아놓자.
 */
public abstract class Current{
	
	public static final MapToBean to = MapToBean.create(); 
    private static final ThreadLocal<SessionInfo> INFO = new ThreadLocal<SessionInfo>();
    
    public static SessionInfo getInfo() {
    	SessionInfo info = INFO.get();
        if(info==null){
        	info = new SessionInfo();
        	INFO.set(info);
        }
		return info;
	}
    public static void clear() {
    	INFO.remove();
    }
    public static String menuName() {
    	Menu menu = getInfo().getMenu(); 
    	return menu==null ? "예외상황" :menu.getName();
    }

    
}
