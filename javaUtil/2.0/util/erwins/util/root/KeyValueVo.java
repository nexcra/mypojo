package erwins.util.root;

import lombok.Data;

@Data
public class KeyValueVo<K,V> implements KeyValue<K,V>{
	
	private K key;
	private V value;
	
}
