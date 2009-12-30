package erwins.util.tools;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.apache.commons.lang.SystemUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import erwins.util.lib.Maths;


/**
 * 서버 정보에 대한 전반적인 사항을 다룬다.
 * @author erwins(my.pojo@gmail.com)
 **/
public abstract class SystemInfo{
    
    private static Log log = LogFactory.getLog(SystemInfo.class); 
    
    private static String[] SERVER_IP ; //기본값
    private static String IP ;
    
    static{
        try {
            IP = InetAddress.getLocalHost().getHostAddress();
            SERVER_IP = new String[]{"218.156.67.18"};
        }
        catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }
    
    /**
     * 1. Window Xp(일반적인 개발장비)가 아니면 true를 리턴한다.
     * 2. XP장비지만 등록된 IP이면 true를 리턴한다.
     */
    public static boolean isServer(){
        if(!SystemUtils.IS_OS_WINDOWS_XP) return true;
        if(isServerByIp()) return true;        
        return false;
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
     * 등록된 IP의 서버인지?
     */
    private static boolean isServerByIp(){
        for(String ip : SERVER_IP) if(ip.equals(IP)) return true;
        return false;
    }

    public static void setServerIp(String[] serverIp){
        SERVER_IP = serverIp;
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
    public static String memoryTest(Runnable command,boolean console){
        long before = nowUsedMemory();
        command.run();
        double after = nowUsedMemory() -  before;
        String result = String.valueOf(after / 1000 / 1000) + "MB";
        if(console) log.info(result);
        return result;
    }

}