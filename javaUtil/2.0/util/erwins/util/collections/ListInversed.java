
package erwins.util.collections;

import java.util.LinkedList;

/** LinkedList에  Max를 넘어갈 경우 사라지는 push를 추가한다. */
@SuppressWarnings("serial")
public class ListInversed<T> extends LinkedList<T>{
	
	private final int maxSize;
	
	public ListInversed(int maxSize) {
		this.maxSize = maxSize;
	}
	
	public T pushAndRemoveMaxValue(T e){
		T ret = null;
		if(maxSize <= size()){
			ret = removeLast();
		}
		addFirst(e);
		return ret;
	}
}
