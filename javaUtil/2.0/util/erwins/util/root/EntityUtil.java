package erwins.util.root;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;




/** lib에 종속적인 유틸 모음? */
public abstract class EntityUtil{

	/**
	 * null safe하게 IdEntity를 비교한다.
	 * EntityId <= 이게 들어가면 ID만 읽어도 객체를 로드하려고 한다. 즉 PK인것을 하이버네이트가 인지하지 못한다. 
	 * Reflection으로 읽어야 안전한듯.
	 */
	public static <ID extends Serializable> boolean isEqualId(EntityId<ID> a, EntityId<ID> b){
	    if(a==null || b==null) return false;
	    if(a.getId().equals(b.getId())) return true;
	    return false;
	}

	/**
	 * List를 Map으로 교체한다.
	 */
	@SuppressWarnings("rawtypes")
	public static <T extends EntityId> Map<String,T> toMap(List<T> list) {
	    Map<String,T> map = new HashMap<String,T>();
	    for(T obj : list) map.put(obj.getId().toString(), obj);
	    return map;
	}
    
    

}