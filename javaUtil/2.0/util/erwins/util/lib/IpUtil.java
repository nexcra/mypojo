package erwins.util.lib;

import java.util.Collection;
import java.util.Map;
import java.util.regex.Pattern;

import javax.validation.ValidationException;

import erwins.util.text.StringUtil;

/**
 * IpUtil.addIpExpression(map, "222.222.222.100 ~ 249");
   IpUtil.validateIp(map.keySet());
 *  */
public abstract class IpUtil{
    
    private static final String GOOGLE_BOT = "74.125.19.";
    
    private static final String GAE_TASK_QUEUE = "0.1.0.2";

	public static boolean isBot(String ip){
    	if(ip.startsWith(GOOGLE_BOT)) return true; 
    	if(ip.equals(GAE_TASK_QUEUE)) return true;
    	return false;
    }
	
    /** IP표현식으로 IP를 입력해준다. 다음 표현식을 인식한다
     *  111.111.111.*  / 222.222.222.100 ~ 160 */
    public static void addIpExpression(Map<String,String> map,String ipExpression){
        if(StringUtil.isMatch(ipExpression,"*")){
            String prefix = StringUtil.getFirst(ipExpression, "*");
            push(map,ipExpression, prefix,0,255);
        }else if(StringUtil.isMatch(ipExpression,"~")){
            String[] pair = StringUtil.split(ipExpression,"~");
            assert pair.length == 2;
            String[] splitedFirstIp =  StringUtil.getExtentions(pair[0].trim());
            String prefix = splitedFirstIp[0].trim();
            int startIp = Integer.parseInt(splitedFirstIp[1].trim());
            int endIp = Integer.parseInt(pair[1].trim());
            push(map,ipExpression, prefix+".",startIp,endIp);
        }else map.put(ipExpression, ipExpression);
    }
    
    private static void push(Map<String,String> map,String each, String prefix,int start,int end) {
        end = end + 1;
        for(int i=start;i<end;i++){
            map.put(prefix+i, each); 
        }
    }
    
    private static final Pattern IS_IP = Pattern.compile("\\b((25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\b");
    
    public static boolean isIp(String ip){
    	return IS_IP.matcher(ip).matches();
    }
    
    public static void validateIp(Collection<String> ips) throws IsNotIpException{
    	for(String ip : ips){
    		boolean isIp = isIp(ip);
    		if(!isIp) throw new IsNotIpException(ip);
    	}
    }
    
    public static class IsNotIpException extends ValidationException{
		private static final long serialVersionUID = -7151055612324199091L;
		public final String ip;
		private IsNotIpException(String ip) {
			super(ip + " is not ip valid expression");
			this.ip = ip;
		}
		private IsNotIpException(String ip,Throwable cause) {
			super(ip + " is not ip valid expression",cause);
			this.ip = ip;
		}
    }

}
