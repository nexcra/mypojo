package erwins.util.lib;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.apache.commons.lang.SystemUtils;


/**
 * 서버 정보에 대한 전반적인 사항을 다룬다.
 * 이걸 이용해서 실서버인지 판단하는, 하드코딩된 static 클래스가 필요하다?
 **/
public abstract class SystemUtil extends SystemUtils{
    
    private static String IP ;
    
    static{
        try {
            IP = InetAddress.getLocalHost().getHostAddress();
        }
        catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }
    
    public static Long nowUsedMemory(){
        //Runtime.getRuntime().gc(); 좀더 정확한 값을 알기 위해?? 측정 직전에 GC한다. => 쓸모없음
        return Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
    }
    public static String nowUsedMemoryStr(){
        double memory = nowUsedMemory();
        return String.valueOf(memory / 1000 / 1000) + "MB";
    }
    
    /** 현재 heap 메모리를 리턴한다. 단위는 MB이다. */
    public static double totalMemory(){
    	return MathUtil.round(Runtime.getRuntime().totalMemory() / 1024.0 / 1024.0,1);
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