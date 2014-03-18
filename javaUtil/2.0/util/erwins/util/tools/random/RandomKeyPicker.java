package erwins.util.tools.random;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.NavigableMap;
import java.util.Random;
import java.util.TreeMap;

import erwins.util.tools.random.RandomKeyPicker.RandomObject;

/** count는 랜덤하게 픽하게 될 부가상수이다. count / sum이 픽될 확율이 된다. */
public class RandomKeyPicker<OBJ,T extends RandomObject<OBJ>> implements RandomPick<OBJ>{
    
    private NavigableMap<Integer, OBJ> map = new TreeMap<Integer, OBJ>();
    private Random random = new Random();
    private int sum;

    /** 인라인 용 ??  테스트 안해봄 */
	@SuppressWarnings("unchecked")
	public RandomKeyPicker(OBJ[] keys,int[] values){
    	List<T> list = new ArrayList<T>();
    	for(int i=0;i<keys.length;i++){
			SimpleRandomObject<OBJ> inst =  new SimpleRandomObject<OBJ>(keys[i],values[i]);
    		list.add((T) inst);
    	}
    	init(list);
    }
    
    /** current의 크기가 Integer.MAX_VALUE(2147483647)를 넘으면 안된다. */
    public RandomKeyPicker(List<T> list){
        init(list);
    }

	private void init(List<T> list) {
		int current = 0;
        for (T each : list) {
            map.put(current, each.getRandomObject());
            current += each.getCount();
        }
        sum = current;
	}
    
    public OBJ getRandom(){
        int search = random.nextInt(sum);
        Entry<Integer, OBJ> e = map.floorEntry(search);
        if(e==null) throw new RuntimeException("e is not null. but null input!");
        return e.getValue();
    }
    
    public static interface RandomObject<T>{
        public T getRandomObject();
        public void setRandomObject(T randomObject);
        public int getCount();
        public void setCount(int count);
    }
    public static class SimpleRandomObject<T> implements RandomObject<T>{
    	private T randomObject;
    	private int count;
		public SimpleRandomObject(T randomObject, int count) {
			super();
			this.randomObject = randomObject;
			this.count = count;
		}
		@Override
		public T getRandomObject() {
			return randomObject;
		}
		@Override
		public void setRandomObject(T randomObject) {
			this.randomObject = randomObject;
		}
		@Override
		public int getCount() {
			return count;
		}
		@Override
		public void setCount(int count) {
			this.count = count;
		}
    	
    }

}
