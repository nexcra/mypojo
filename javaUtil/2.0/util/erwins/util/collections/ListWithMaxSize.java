package erwins.util.collections;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import com.google.common.collect.Lists;


/**
 * 최대 한계가 있는 간이 로그저장 용도로 사용 
 * size를 넘어가면 add할때 이전 데이터를 지우고 넣는다.
 * 스래드 세이프하지 않다.  */
public class ListWithMaxSize<T> implements List<T>{
	
	private List<T> list =  Lists.newArrayList(); 
	private final int size;
	
	public ListWithMaxSize(int size){
		this.size = size;
	}
	
	public boolean add(T e) {
		boolean result =  list.add(e);
		while(list.size() > size) list.remove(0);
		return result;
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

	public Iterator<T> iterator() {
		return list.iterator();
	}

	@SuppressWarnings("unchecked")
	public T[] toArray() {
		return (T[]) list.toArray();
	}

	@SuppressWarnings("hiding")
	public <T> T[] toArray(T[] a) {
		return list.toArray(a);
	}

	public boolean remove(Object o) {
		return list.remove(o);
	}

	public boolean containsAll(Collection<?> c) {
		return list.containsAll(c);
	}

	public boolean addAll(Collection<? extends T> c) {
		throw new UnsupportedOperationException();
	}

	public boolean addAll(int index, Collection<? extends T> c) {
		throw new UnsupportedOperationException();
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

	public T get(int index) {
		return list.get(index);
	}

	public T set(int index, T element) {
		throw new UnsupportedOperationException();
	}

	public void add(int index, T element) {
		throw new UnsupportedOperationException();
	}

	public T remove(int index) {
		return list.remove(index);
	}

	public int indexOf(Object o) {
		return list.indexOf(o);
	}

	public int lastIndexOf(Object o) {
		return list.lastIndexOf(o);
	}

	public ListIterator<T> listIterator() {
		return list.listIterator();
	}

	public ListIterator<T> listIterator(int index) {
		return list.listIterator(index);
	}

	public List<T> subList(int fromIndex, int toIndex) {
		return list.subList(fromIndex, toIndex);
	}
	
	
	
}