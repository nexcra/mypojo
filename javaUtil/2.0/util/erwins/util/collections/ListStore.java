package erwins.util.collections;

import java.util.List;

import com.google.common.collect.Lists;

/** 임시로 List를 저장하고 참조를 갱신하는 클래스.
 * ItemReader에서  특정 조건별로 데이터를 끊어서 List로 리턴할떄 사용된다. */
@Deprecated
public class ListStore<T>{
	
	private List<T> stored = Lists.newArrayList();
	private int maxSize = 0;
	public ListStore(int maxSize){
		this.maxSize = maxSize;
	}
	/** maxSize와 같거나 크면 false를 리턴한다. */
	public boolean add(T e){
		stored.add(e);
		return stored.size() < maxSize;
	}
	
	/** 
	 * 기존 리스트를 리턴하고 새로운 리스트를 저장한다. */
	public List<T> clearAndGet(T ... append){
		List<T> forReturm = stored;
		stored = Lists.newArrayList(append);
		return forReturm;
	}
	public boolean isEmpty() {
		return stored.isEmpty();
	}
    

    

}
