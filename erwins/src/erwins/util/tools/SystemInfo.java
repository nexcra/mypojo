package erwins.util.tools;

import static org.apache.commons.lang.SystemUtils.IS_OS_HP_UX;
import static org.apache.commons.lang.SystemUtils.IS_OS_LINUX;
import static org.apache.commons.lang.SystemUtils.IS_OS_SOLARIS;
import static org.apache.commons.lang.SystemUtils.IS_OS_UNIX;

import java.net.InetAddress;
import java.net.UnknownHostException;

import erwins.util.lib.Maths;


/**
 * 서버 정보에 대한 전반적인 사항을 다룬다.
 * @author erwins(my.pojo@gmail.com)
 **/
public abstract class SystemInfo{
    
    private static String[] SERVER_IP ; //기본값
    private static String IP ;
    private static boolean server = true;
    
    static{
        try {
            IP = InetAddress.getLocalHost().getHostAddress();
            SERVER_IP = new String[]{"192.168.1.132" //sysb 공용서버
            };
            if(IS_OS_LINUX || IS_OS_HP_UX || IS_OS_SOLARIS || IS_OS_UNIX) server =  true;
            else{
            	server = false;
            	for(String eachIp : SERVER_IP) if(eachIp.equals(IP)){
            		server =  true;
            	}
            }
        }
        catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }
    
    /** 테스트용임!! NT장비가 들어가는 곳에서는 사용하면 안된다. */
    public static boolean isServer(){
        return server;
    }
    
    /**
     * 좀더 정확한 값을 알기 위해?? 측정 직전에 GC한다. 
     */
    public static Long nowUsedMemory(){
        Runtime.getRuntime().gc();
        return Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
    }
    public static String nowUsedMemoryStr(){
        double memory = nowUsedMemory();
        return String.valueOf(memory / 1000 / 1000) + "MB";
    }
    
    /** 현재 heap 메모리를 리턴한다. 단위는 MB이다. */
    public static double totalMemory(){
    	return Maths.round(Runtime.getRuntime().totalMemory() / 1024.0 / 1024.0,1);
    }
    
    /**
     * @param 현재 가동중인 JVM의 IP 
     */
    public static String getServerIp(){
        return IP;
    }
    
    /**
     * command를 실행하면 얼마나 많은 메모리를 소모하는지?
     */
    public static String memoryTest(Runnable command){
        long before = nowUsedMemory();
        command.run();
        double after = nowUsedMemory() -  before;
        String result = String.valueOf(after / 1000 / 1000) + "MB";
        return result;
    }

}