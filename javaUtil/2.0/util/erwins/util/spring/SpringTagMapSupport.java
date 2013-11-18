package erwins.util.spring;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableMap;


/** 
 * EL에서 MAP 형식으로 HTML을 구성하도록 도와주는 헬퍼
 * */ 
public abstract class SpringTagMapSupport implements Map<String,Map<String,String>>{
	
	protected Splitter splitter = Splitter.on('|').omitEmptyStrings().trimResults();
	protected Map<String,Map<String,String>> springTag = new ConcurrentHashMap<String, Map<String,String>>();
	protected Map<String,String> ALL = ImmutableMap.of("ALL","전체");
	

	@Override
	public void clear() {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public boolean containsKey(Object key) {
		//return springTag.containsKey(key);
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean containsValue(Object arg0) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Set<java.util.Map.Entry<String, Map<String, String>>> entrySet() {
		throw new UnsupportedOperationException();
	}


	@Override
	public boolean isEmpty() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Set<String> keySet() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Map<String, String> put(String arg0, Map<String, String> arg1) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void putAll(Map<? extends String, ? extends Map<String, String>> arg0) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Map<String, String> remove(Object arg0) {
		throw new UnsupportedOperationException();
	}

	@Override
	public int size() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Collection<Map<String, String>> values() {
		throw new UnsupportedOperationException();
	}
	
	

}

