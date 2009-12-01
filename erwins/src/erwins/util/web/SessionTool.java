
package erwins.util.web;

import java.util.Calendar;

import javax.servlet.http.HttpSession;

import erwins.util.lib.Days;

/**
 */
public class SessionTool<USER>{
    
    private USER user;
    
    private HttpSession session;
    
    public SessionTool(HttpSession session,USER user){
        this.session = session;
        this.user = user;
    }

    public USER getUser() {
        return user;
    }
    
    /**
     * 최종 접속 시간과 현재 시간과의 차이.
     */    
    public long[] getInterval(){
        Calendar lastAccessTime = Calendar.getInstance();
        lastAccessTime.setTimeInMillis(session.getLastAccessedTime());
        return Days.betweenTime(lastAccessTime, Calendar.getInstance());
    }
    
    /**
     * 최종 접속 시간과 현재 시간과의 차이.
     */    
    public String getIntervalStr(){
        return Days.toString(getInterval());
    }
    
    public String getDurationStr(){
        long[] result = getDuration();
        return Days.toString(result);
    }

    /** 지속시간 : 마지막 접속시간 - 최초접속시간  */
    public long[] getDuration() {
        Calendar lastAccessTime = Calendar.getInstance();
        lastAccessTime.setTimeInMillis(session.getLastAccessedTime());
        Calendar createTime = Calendar.getInstance();
        createTime.setTimeInMillis(session.getCreationTime());
        return Days.betweenTime(createTime,lastAccessTime);
    }
    
    /**
     * 아래의 아이피는 주로 구글봇이다.
     * 66.249.71.75  / 66.249.65.164
     */
    public boolean isBot(){
        long[] result = getDuration();
        for(long each : result) if(each != 0L) return false;
        return true;
    }
     
    public Calendar getLastAccessedTime(){
        Calendar lastAccessTime = Calendar.getInstance();
        lastAccessTime.setTimeInMillis(session.getLastAccessedTime());
        return lastAccessTime;
    }
    public Calendar getCreationTime(){
        Calendar createTime = Calendar.getInstance();
        createTime.setTimeInMillis(session.getCreationTime());
        return createTime;
    }
    public String getLastAccessedTimeStr(){
        return Days.DATE.get(getLastAccessedTime());
    }
    public String getCreationTimeStr(){
        return Days.DATE.get(getCreationTime());
    }
    public HttpSession getSession() {
        return session;
    }
    
    
    
    
    
    
}
