package erwins.util.tools.random;

import java.util.Map.Entry;
import java.util.NavigableMap;
import java.util.Random;
import java.util.TreeMap;

import erwins.util.validation.Precondition;

/** count는 랜덤하게 픽하게 될 부가상수이다. count / sum이 픽될 확율이 된다. */
public class RandomPicker<T> implements RandomPick<T>{
    
    private NavigableMap<Integer, T> map = new TreeMap<Integer, T>();
    private Random random = new Random();
    private int sum;

    public RandomPicker(){}
    
    /** 인라인 용 */
	public RandomPicker(T[] valueSet,int[] rateSet){
    	Precondition.isEquals(valueSet.length, rateSet.length);
		int current = 0;
		for(int i=0;i<valueSet.length;i++){
			map.put(current, valueSet[i]);
            current += rateSet[i];
		}
        sum = current;
    }
	
	/** 스래드 안전하지 않다. */
	public  void addItem(int weight,T value){
		map.put(sum, value);
		sum += weight;
	}
	    
    public T getRandom(){
        int search = random.nextInt(sum);
        Entry<Integer, T> e = map.floorEntry(search);
        if(e==null) throw new RuntimeException("e is not null. but null input!");
        return e.getValue();
    }
    

}
