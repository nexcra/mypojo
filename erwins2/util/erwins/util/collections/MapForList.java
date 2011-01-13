package erwins.util.collections;



/**
 * 이하의 간단 버전이다.
 * private Map<String,List<File>> map = new HashMap<String,List<File>>();
 */

public class MapForList<T> extends MapForKeyList<String,T>{

	public MapForList(MapType type) {
		super(type);
	}
	
    
}