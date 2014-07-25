package erwins.util.collections;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import com.google.common.collect.Lists;


/** 
 * CSV에 들어갈 String[]을 만들때 주로 사용된다.   
 * 널체크 하기 귀찮은 날코딩할때 사용 */
public class ListString implements List<String>{
	
	private List<String> list = Lists.newArrayList();
	private String nullData = "";
	
	/** 신규 추가 */
	public boolean addNulldafe(Object o){
		return list.add(o == null ? nullData : o.toString());
	}
	
	/** 신규 추가 */
	public ListString setNullData(String nullData){
		this.nullData = nullData;
		return this;
	}
	
	/** 신규 추가 */
	public String[] toArrayString(){
		return list.toArray(new String[list.size()]);
	}
	
	public static ListString newInstance(Object ... args){
		ListString data = new ListString();
		for(Object each : args) data.addNulldafe(each);
		return data;
	}

	public int size() {
		return list.size();
	}

	public boolean isEmpty() {
		return list.isEmpty();
	}

	public boolean contains(Object o) {
		return list.contains(o);
	}

	public Iterator<String> iterator() {
		return list.iterator();
	}

	public Object[] toArray() {
		return list.toArray();
	}

	public <T> T[] toArray(T[] a) {
		return list.toArray(a);
	}

	public boolean add(String e) {
		return list.add(e);
	}

	public boolean remove(Object o) {
		return list.remove(o);
	}

	public boolean containsAll(Collection<?> c) {
		return list.containsAll(c);
	}

	public boolean addAll(Collection<? extends String> c) {
		return list.addAll(c);
	}

	public boolean addAll(int index, Collection<? extends String> c) {
		return list.addAll(index, c);
	}

	public boolean removeAll(Collection<?> c) {
		return list.removeAll(c);
	}

	public boolean retainAll(Collection<?> c) {
		return list.retainAll(c);
	}

	public void clear() {
		list.clear();
	}

	public boolean equals(Object o) {
		return list.equals(o);
	}

	public int hashCode() {
		return list.hashCode();
	}

	public String get(int index) {
		return list.get(index);
	}

	public String set(int index, String element) {
		return list.set(index, element);
	}

	public void add(int index, String element) {
		list.add(index, element);
	}

	public String remove(int index) {
		return list.remove(index);
	}

	public int indexOf(Object o) {
		return list.indexOf(o);
	}

	public int lastIndexOf(Object o) {
		return list.lastIndexOf(o);
	}

	public ListIterator<String> listIterator() {
		return list.listIterator();
	}

	public ListIterator<String> listIterator(int index) {
		return list.listIterator(index);
	}

	public List<String> subList(int fromIndex, int toIndex) {
		return list.subList(fromIndex, toIndex);
	}
	
	
	
}
