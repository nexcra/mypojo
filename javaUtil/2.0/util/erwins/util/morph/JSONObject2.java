
package erwins.util.morph;

import net.sf.json.JSONObject;
import erwins.util.valueObject.ValueObject;

public class JSONObject2 {
    
	public final JSONObject json = new JSONObject();
	
	public JSONObject2 put(String key,Object value){
		if(value==null) json.put(key,null);
		if(value instanceof ValueObject){
			json.put(key,((ValueObject) value).returnValue());
		}else json.put(key,value); 
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