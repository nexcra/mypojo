
package erwins.util.counter;

import java.util.HashMap;
import java.util.Map;



/**
 * 그냥 이 패키지에 넣었다. ㅠㅠ
 * 단순히 key가 한번이상 불려졌는지 체크한다.
 */
public class StringLatch{
    
	private Map<String,Boolean> map = new HashMap<String,Boolean>();
    
    public boolean exist(String key){
    	Boolean obj = map.get(key);
        if(obj==null){
        	map.put(key, Boolean.TRUE);
            return false;
        }
        return true;
    }
}