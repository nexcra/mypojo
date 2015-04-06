package erwins.util.spring.web;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.google.common.collect.Lists;

import erwins.util.text.StringUtil;


/** 
 * 심플 클래스 이름으로 Enum을 찾아주는 서포터
 * */ 
public class EnumFinder{
	
	private List<String> packageNames = Lists.newArrayList();
	private Map<String,List<Enum<?>>> cache = new ConcurrentHashMap<String, List<Enum<?>>>();
	
	public EnumFinder(){};
	public EnumFinder(List<String> packageNames){
		this.packageNames = packageNames;
	};
	
	public void addPackageName(String packageName){
		packageNames.add(packageName);
	}
	
	@SuppressWarnings("unchecked")
	public List<Enum<?>> findBySimpleName(String inputSimpleName) {
		String simpleName = StringUtil.capitalize(inputSimpleName); 
		
		List<Enum<?>> exist = cache.get(simpleName);
		if(exist!=null) return exist; 
		
		Class<Enum<?>> clazz = null;
		for(String packageName : packageNames){
			try {
				System.out.println(packageName + "." + simpleName);
				clazz =  (Class<Enum<?>>) Class.forName(packageName + "." + simpleName);
			} catch (ClassNotFoundException e) {
				continue;
			}
		}
		if(clazz==null) throw new IllegalArgumentException(inputSimpleName + " 에 해당하는 Enum을 찾을 수 없습니다.");
		List<Enum<?>> result = Lists.newArrayList(clazz.getEnumConstants());
		cache.put(simpleName, result);
		return result;
	}
	

}

