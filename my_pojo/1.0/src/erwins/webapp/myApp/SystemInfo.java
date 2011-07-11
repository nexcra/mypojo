package erwins.webapp.myApp;

import erwins.util.lib.SystemUtil;


/**
 * 서버 정보에 대한 전반적인 사항을 다룬다.
 * 이걸 이용해서 실서버인지 판단하는, 하드코딩된 static 클래스가 필요하다?
 **/
public abstract class SystemInfo extends SystemUtil{
    
    private static String[] SERVER_IP = {""} ; //기본값
    private static Boolean server = null;
    
    static{
    	if(IS_OS_LINUX || IS_OS_HP_UX || IS_OS_SOLARIS || IS_OS_UNIX) server =  true;
        else{
        	server = false;
        	for(String eachIp : SERVER_IP) if(eachIp.equals(getServerIp())){
        		server =  true;
        	}
        }
    }
    
    /** 테스트용임!! NT장비가 들어가는 곳에서는 사용하면 안된다. */
    public static boolean isServer(){
        return server;
    }
    

}