package erwins.util.tools;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** 라운드 로빈이다. 성능은 무시한다. 근데  쓸데 없음. ㅠㅠ */
public class RoundrobinPool<T>{
	
	private List<T> list = Collections.synchronizedList(new ArrayList<T>());
	private int size = 0;
	private int index = 0;
	
	/** 웬만하면 스래드 기동 전에 전부 add하자. */
	public void add(T t){
		list.add(t);
		size = list.size(); 
	}
	
	public T get(){
		return list.get(index++%size);
	}
}