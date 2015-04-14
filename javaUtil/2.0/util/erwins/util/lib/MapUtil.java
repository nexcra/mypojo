package erwins.util.lib;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.google.common.collect.Maps;


/** 
 * 보통 통계치를 받아서 map으로 구성할때 사용
 * 실무 사용은 지양
 *  */
public abstract class MapUtil {
	
	/** csv로 컨버트할때 사용. 미리 map을 구성해놓고 인라인으로 사용하자. */
	public static String[] toStringArray(Map<String,Object> map,String ... keys){
		String[] result = new String[keys.length];
		for(int i=0;i<keys.length;i++){
			Object value = map.get(keys[i]);
			if(value instanceof BigDecimal){
				BigDecimal decimal = (BigDecimal) value;
				value = decimal.toPlainString();
			}
			result[i] = value.toString();
		}
		return result;
		
	}
	
	public static Map<String,Object> sum(List<Map<String,Object>> list){
		Map<String,Object> sum = Maps.newHashMap();
		for(Map<String,Object> each : list){
			for(Entry<String, Object> e : each.entrySet()){
				String key = e.getKey();
				Object value = e.getValue();
				boolean numeric = value instanceof Number;
				if(!numeric) continue;
				Number num = (Number) value;
				if(num instanceof BigDecimal){
					BigDecimal input = (BigDecimal) num;
					BigDecimal exist = (BigDecimal) sum.get(key);
					if(exist==null) sum.put(key, input);
					else sum.put(key, exist.add(input));
				}else{
					long input = num.longValue();
					Long exist = (Long) sum.get(key);
					if(exist==null) sum.put(key, value);
					else sum.put(key, input + exist);
				}
			}
		}
		return sum;
		
	}
	
	
}
