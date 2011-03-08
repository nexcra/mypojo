package erwins.util.collections;

import java.io.Serializable;

/**
 * 이하의 간단 버전이다.
 * private Map<String,List<File>> map = new HashMap<String,List<File>>();
 * 제작 직후 key를 선정해서 Map으로 변경하면 키 기준으로 데이터를 모아준다.
 * MapForList<Trx> userMap = new MapForList<Trx>(MapType.Tree);
 * ex) for (Trx each : entry.getValue()) userMap.add(each.getSysUserName(), each);
 */

@SuppressWarnings("serial")
public class MapForList<T> extends MapForKeyList<String,T> implements Serializable{

	public MapForList(MapType type) {
		super(type);
	}
	
    
}