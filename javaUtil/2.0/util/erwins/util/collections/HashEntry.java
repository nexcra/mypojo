package erwins.util.collections;


/** key를 해시로 사용하는 엔트리.  기본적으로 key , value 구조이다.   */
public class HashEntry<T>{
	
	private String key;
	private T value;
	
	public HashEntry(String key,T value){
		this.key = key;
		this.value = value;
	}
	
	public HashEntry(){}
	
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public T getValue() {
		return value;
	}
	public void setValue(T value) {
		this.value = value;
	}
	
	@Override
	public int hashCode() {
		return key.hashCode();
	}
	
	@SuppressWarnings({ "rawtypes" })
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		HashEntry other = (HashEntry) obj;
		if (key == null) {
			if (other.key != null)
				return false;
		} else if (!key.equals(other.key))
			return false;
		return true;
	}
	
	@Override
	public String toString() {
		return key + " / "+ value.toString();
	}

}
