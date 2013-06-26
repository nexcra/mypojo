package erwins.util.tools.random;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/** count는 랜덤하게 픽하게 될 부가상수이다. count / sum이 픽될 확율이 된다. */
public class RandomList<T> implements RandomPick<T>{
    
    private final List<T> list;
    private Random random = new Random();

	public RandomList(List<T> list){
    	this.list = list;
    }
    
    public T getRandom(){
    	int index = random.nextInt(list.size());
    	return list.get(index);
    }
    
    /** 중복되지 않은 랜덤한 max개를 리턴한다.  groovy용?
     * T는 기본형만 되는걸 사용한자. (String 등..) */
    public List<T> getRandomUniqList(int max){
    	if(list.size() < max) throw new IllegalArgumentException("too large max count");
    	Map<T,Boolean> map = new HashMap<T,Boolean>();
    	while(map.size() < max){
    		int index = random.nextInt(list.size());
        	T value =  list.get(index);
    		map.put(value, Boolean.TRUE);
    	}
    	List<T> result = new ArrayList<T>();
    	result.addAll(map.keySet());
    	return result;
    }

}
