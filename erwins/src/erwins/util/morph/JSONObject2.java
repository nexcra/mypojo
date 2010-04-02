
package erwins.util.morph;

import net.sf.json.JSONObject;

public class JSONObject2 {
    
	public final JSONObject json = new JSONObject();
	public JSONObject2 put(String key,Object value){
		json.put(key,value);
		return this;
	}
	public JSONObject2 put(String key,Object value,String nullValue){
		json.put(key, value==null ? nullValue : value);
		return this;
	}
	@Override
	public String toString() {
		return json.toString();
	}

}