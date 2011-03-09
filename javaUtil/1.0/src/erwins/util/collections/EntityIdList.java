package erwins.util.collections;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import erwins.util.root.EntityId;


/** 검색시 단순조회함으로 대용량에는 부적합하다. ID는 Object로 대체 가능해서 생략했다. */
@SuppressWarnings("unchecked")
public class EntityIdList<T extends EntityId> implements Iterable<T>{
	private List<T> list = new ArrayList<T>();
	
	private int order;
	/** 방금 get으로 가져온 객체의 순번을 리턴한다. */
	public int getOrder() {
		return order;
	}
	public void add(T item){
		if(item==null) return;
		list.add(item);
	}
	/** 성능이 딸리면 해시맵으로 변경 */
	public void addUnique(T item){
		if(item==null) return;
		T exist = getItem(item.getId());
		if(exist==null) add(item);
	}
	
	/** 방금 가져온 아이템의 index를 남긴다. */
	public T getItem(Object key){
		if(key==null) return null;
		int length = list.size();
		for(int i=0;i<length;i++) if(key.equals(list.get(i).getId())){
			order = i;
			return  list.get(i);
		}
		return null;
	}
	
	private static final  Comparator<EntityId> ORDER_BY_ID = new Comparator<EntityId>() {
		public int compare(EntityId o1, EntityId o2) {
			return ((Comparable)o1.getId()).compareTo(o2.getId());
		}
	};
	public void sort(){
		Collections.sort(list,ORDER_BY_ID);
	}
	public Iterator<T> iterator() {
		return list.iterator();
	}
}