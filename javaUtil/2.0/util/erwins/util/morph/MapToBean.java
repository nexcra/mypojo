package erwins.util.morph;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import net.sf.json.util.JSONUtils;
import erwins.util.lib.CollectionUtil;
import erwins.util.lib.ReflectionUtil;
import erwins.util.root.DomainObject;
import erwins.util.root.Singleton;

/**
 */

/**
 * JSONObject.fromObject(domainByJson).toString()과는 호환되지 않는다. 
 * 이놈은 Integer가 null이면 0으로 변환한다.. 이밖에 Date와 null처리 방식에 차이가 있다.  
 * JSONObject에 있는 변형 기능이 맘에들지 않아 제작했다.
 * JSONObject.toBean(demoJson)에 비해서 10배 정도 빠르긴 하다. ㅠㅠ 
 * 
 *    향후 하이버네이트 등에서 사용할지 모르니 일단 남겨놓는다.
 *    */
@Singleton
@SuppressWarnings("rawtypes")
@Deprecated
public class MapToBean extends MapToBeanRoot{

	public static MapToBean create() {
		final MapToBean instance = new MapToBean();
		instance.addConfig(DEFAULT);
		instance.addConfig(INTEGER);
		instance.addConfig(LONG);
		instance.addConfig(BIG_DECIMAL);
		instance.addConfig(ENUM);
		instance.addConfig(BOOLEAN);
		instance.addConfig(VALUE_OBJECT);
		/** DomainObject인 경우 */
		instance.addConfig(new MapToBeanConfigFetcher() {
			@Override
			public Object fetch(Field field, Map map) {
				if (!DomainObject.class.isAssignableFrom(field.getType())) return null;
				Map value = (Map) map.get(field.getName());
				if (JSONUtils.isNull(value)) return null;
				Object domain = instance.build(value, field.getType());
				return domain;
			}
		});
		
		/**
		 * OneToMany.class
		 * LIST로 매핑되는 객체는 LIST이면 좋지만, Array일 수도 있는것이다. 다행히 JSONArray는 LIST기반인듯.
		 */
		instance.addConfig(new MapToBeanBaseConfig(new Class[] { Collection.class }, new MapToBeanConfigFetcher() {
			@SuppressWarnings("unchecked")
			@Override
			public Object fetch(Field field, Map map) {
				Object value = map.get(field.getName());
				if (JSONUtils.isNull(value)) return null;
				Class generic = ReflectionUtil.getGeneric(field);
				if (Collection.class.isInstance(value)) {
					Collection valueList = (Collection) value;
					if (String.class == generic) return valueList;
					if (DomainObject.class.isAssignableFrom(generic)) {
						Collection<Object> list = new ArrayList();
						for (Object each : valueList)
							list.add(instance.build((Map) each, generic));
						return list;
					}
				}
				/** 논리적으로 Array일 경우 단일객체만 되며 DomainObject는 불가능하다.
				 * request등에서는 Collection으로 변경해 주어야 한다. */
				if (Object[].class.isInstance(value)) {
					Object[] array = (Object[])value;
					return CollectionUtil.toList(array);
				}else if (String.class.isInstance(value)) {
					//request의 다중 Array이나 값이 1개 뿐이라 []로 넘어오지 않았을 경우
					return CollectionUtil.toList(value);
				}
				return null;
			}
		}));
		return instance;
	}

}