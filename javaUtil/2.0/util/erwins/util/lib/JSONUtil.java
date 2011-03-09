package erwins.util.lib;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * JSONObject의 경우 JSONArray등에 포함(add or put)되면 포함된 그당시의 상태가 저장된다. (즉 래퍼런스를 잃는다.)
 * 따라서 이를 수정할려면 기존 래퍼런스를 수정하느것이 아니라 get시리즈로 꺼낸다음 수정해야한다.
 */
public abstract class JSONUtil{
    
	/** list의 Json에서 fieldName가 동일한것을 가져온다. 즉 fieldName를 키로 간주한다.  */
	public static JSONObject getByKey(JSONArray list,String keyFieldName,String compareValeu) {
		for(Object temp : list){
			JSONObject each = (JSONObject)temp;
			String key = (String)each.get(keyFieldName);
			if(key!=null && key.equals(compareValeu)) return each;
		}
		return null;
	}
}