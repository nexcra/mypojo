package erwins.util.morph;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;
import net.sf.json.util.JSONUtils;
import erwins.util.lib.CollectionUtil;
import erwins.util.lib.ReflectionUtil;
import erwins.util.lib.StringUtil;
import erwins.util.root.Singleton;
import erwins.util.valueObject.ValueObject;

/** BeanToJson 와 비슷하게 만들려 했으나 실패 ㅠㅠ */
@Singleton
@SuppressWarnings("rawtypes")
public abstract class MapToBeanRoot {

	private final List<MapToBeanConfigFetcher> configs = new ArrayList<MapToBeanConfigFetcher>();

	/** JSON에서 지원하는 기본값 값이 커지면 int->long 등 알아서 변한다. */
	protected static final MapToBeanConfigFetcher DEFAULT = new MapToBeanBaseConfig(new Class[] { String.class },
			new MapToBeanConfigFetcher() {
				@Override
				public Object fetch(Field field, Map map) {
					Object value = map.get(field.getName());
					if(JSONUtils.isNull(value)) return null;
					return value;
				}
			});
	protected static final MapToBeanConfigFetcher INTEGER = new MapToBeanBaseConfig(new Class[] {Integer.class,int.class},
			new MapToBeanConfigFetcher() {
		@Override
		public Object fetch(Field field, Map map) {
			Object value = map.get(field.getName()); 
			if(JSONUtils.isNull(value)) return null;
			if(Long.class.isInstance(value)) return ((Long)value).intValue();
			if(Integer.class.isInstance(value)) return value;
			String strValue = value.toString();
			if(StringUtil.isEmpty(strValue)) return null;
			return Integer.parseInt(strValue);
		}
	});
	protected static final MapToBeanConfigFetcher LONG = new MapToBeanBaseConfig(new Class[] {Long.class,long.class },
			new MapToBeanConfigFetcher() {
		@Override
		public Object fetch(Field field, Map map) {
			Object value = map.get(field.getName()); 
			if(JSONUtils.isNull(value)) return null;
			if(Long.class.isInstance(value)) return value;
			if(Integer.class.isInstance(value)) return ((Integer)value).longValue();
			String strValue = value.toString();
			if(StringUtil.isEmpty(strValue)) return null;
			return Long.parseLong(strValue);
		}
	});
	protected static final MapToBeanConfigFetcher BIG_DECIMAL = new MapToBeanBaseConfig(new Class[] {BigDecimal.class },
			new MapToBeanConfigFetcher() {
		@Override
		public Object fetch(Field field, Map map) {
			Object value = map.get(field.getName()); 
			if(JSONUtils.isNull(value)) return null;
			if(BigDecimal.class.isInstance(value)) return value;
			String strValue = value.toString();
			if(StringUtil.isEmpty(strValue)) return null;
			return new BigDecimal(strValue);
		}
	});
	
	protected static final MapToBeanConfigFetcher VALUE_OBJECT = new MapToBeanBaseConfig(new Class[] {ValueObject.class },
			new MapToBeanConfigFetcher() {
		@Override
		public Object fetch(Field field, Map map) {
			Object value = map.get(field.getName()); 
			if(JSONUtils.isNull(value)) return null;
			ValueObject valueObject = (ValueObject) ReflectionUtil.newInstance(field.getType());
			valueObject.initValue(value);
			return valueObject;
		}
	});
	
	/** Long타입만 지원한다. 지저분한 년월일시분초 등은 안함. 나중에 Calendar도 작성  */
	protected static final MapToBeanConfigFetcher DATE = new MapToBeanBaseConfig(new Class[] {Date.class },
			new MapToBeanConfigFetcher() {
		@Override
		public Object fetch(Field field, Map map) {
			Object value = map.get(field.getName()); 
			if(JSONUtils.isNull(value)) return null;
			if(Number.class.isInstance(value)) return new Date(((Number)value).longValue());
			return null;
		}
	});
	
	protected static final MapToBeanConfigFetcher BOOLEAN = new MapToBeanBaseConfig(new Class[] {Boolean.class,boolean.class },
			new MapToBeanConfigFetcher() {
		@Override
		public Object fetch(Field field, Map map) {
			Object value = map.get(field.getName()); 
			if(JSONUtils.isNull(value)) return null;
			return toBoolean(value);
		}
	});
	
    public static Boolean toBoolean(Object obj){
        if(obj==null) return null;
        else if(obj instanceof Boolean) return (Boolean)obj;
        String value = obj.toString();
        if(StringUtil.isEqualsIgnoreCase(value, "Y","1","ON","true")) return true;
        else if(StringUtil.isEqualsIgnoreCase(value, "N","0","OFF","false")) return false;
        else return null;
    }
	
	protected static final MapToBeanConfigFetcher ENUM =new MapToBeanConfigFetcher() {
		@SuppressWarnings("unchecked")
		@Override
		public Object fetch(Field field, Map map) {
			Class type = field.getType();
			if (!type.isEnum()) return null;
			Object value = map.get(field.getName());
			if (JSONUtils.isNull(value)) return null;
			return ReflectionUtil.getEnumInstance(type, (String) value);
		}
	};

/*	*//**
	 * JSONObject는 특이하게 null속성인 Object를 리턴한다. ㅅㅂ isEmpty()에서 에러난다. 우리는 Map을
	 * 범용적으로 사용하고 싶음으로 일케 처리하주자.
	 *//*
	public static boolean JSONUtils.isNull(Object obj) {
		if (obj == null) return true;
		if (JSONObject.class.isInstance(obj) && ((JSONObject) obj).JSONUtils.isNullObject()) return true;
		return false;
	}*/

	public void addConfig(MapToBeanConfigFetcher config) {
		configs.add(config);
	}
	
	/** JSON을 바로 변환하기 용. */
	public <T> T build(String json, Class<T> clazz) {
		JSONObject jsonObject = JSONObject.fromObject(json);
		return build(jsonObject,clazz);
	}

	public <T> T build(Map obj, Class<T> clazz) {
		T instance = ReflectionUtil.newInstance(clazz);
		List<Field> fields = ReflectionUtil.getAllDeclaredFields(clazz);
		for (Field field : fields) {
			field.setAccessible(true);
			for (MapToBeanConfigFetcher each : configs) {
				Object input = each.fetch(field, obj);
				if (input == null) continue;
				try {
					ReflectionUtil.setField(field, instance, input);
				} catch (IllegalStateException e) {
					// ststic final에 입력할때 예외를 무시한다. (어케 처리할지 모르겠음 ㅠㅠ)
				}
			}
		}
		return instance;
	}

	/** class와 매칭되는걸 쉽게 찾게하기 위해 만든 헬퍼클래스 */
	public static class MapToBeanBaseConfig implements MapToBeanConfigFetcher {
		private final Class<?>[] ableClass;
		private final MapToBeanConfigFetcher fetcher;

		public MapToBeanBaseConfig(Class<?>[] ableClass, MapToBeanConfigFetcher fetcher) {
			this.ableClass = ableClass;
			this.fetcher = fetcher;
		}

		@Override
		public Object fetch(Field field, Map map) {
			if (CollectionUtil.isAssignableFrom(field.getType(), ableClass)) return fetcher.fetch(field, map);
			return null;
		}
	}

	/** 결과로 Object를 반환한다. null이면 부합되는 조건이 아닐 경우이다. */
	public static interface MapToBeanConfigFetcher {
		public Object fetch(Field field, Map map);
	}
}