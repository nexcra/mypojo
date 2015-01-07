package erwins.util.spring.batch.component;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Date;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.RowMapper;

import com.google.common.collect.Maps;

import erwins.util.lib.ReflectionUtil;

/**
 * 빈즈 규격에 맞지 않는 애들은 임시 조치해주자
 * Map<String,Field> fieldMap = ReflectionUtil.findFieldValue(mapper, "fieldMap");
	fieldMap.put("IMG_1_URL", fieldMap.get("IMG1_URL")); 
 * */
public class ReflectionRowMapper<T> implements RowMapper<T> {
	
	private final Class<T> persistentClass;
	private Map<String,Field> fieldMap;
	
	public static <T> ReflectionRowMapper<T> create(Class<T> persistentClass){
		ReflectionRowMapper<T> mapper = new ReflectionRowMapper<T>(persistentClass);
		return mapper;
	}
	
	/** hive에서 들고오면 죄다 소문자로 변경된다. */
	public static <T> ReflectionRowMapper<T> createLowerCase(Class<T> persistentClass){
		ReflectionRowMapper<T> mapper = new ReflectionRowMapper<T>(persistentClass);
		Map<String,Field> fieldMap = Maps.newHashMap();
		for(Entry<String, Field> entry:mapper.fieldMap.entrySet()){
			fieldMap.put(entry.getKey().toLowerCase(), entry.getValue());
		}
		mapper.fieldMap = fieldMap;
		return mapper;
	}
    
	private ReflectionRowMapper(Class<T> persistentClass) {
        this.persistentClass = persistentClass;
        fieldMap = ReflectionUtil.getAllDeclaredFieldUnderscoreMap(persistentClass);
    }
	
	private Logger log = LoggerFactory.getLogger(this.getClass());

	/** 없는거 추가하자 */
	@Override
	public T mapRow(ResultSet rs, int arg1) throws SQLException {
		ResultSetMetaData meta =  rs.getMetaData();
		T vo = ReflectionUtil.newInstance(persistentClass);
		for(int columnIndex=1;columnIndex<meta.getColumnCount()+1;columnIndex++){
			String columnName = meta.getColumnName(columnIndex);
			Field field = fieldMap.get(columnName);
			if(field==null){
				log.warn("컬럼명 {}는 클래스 {}에 없는 필드입니다.",columnName,persistentClass.getSimpleName());
				continue;
			}
			Class<?> type = field.getType();
			if(String.class.isAssignableFrom(type)){
				ReflectionUtil.setField(field, vo, rs.getString(columnIndex));
			}else if(Long.class.isAssignableFrom(type)){
				ReflectionUtil.setField(field, vo, rs.getLong(columnIndex));
			}else if(Integer.class.isAssignableFrom(type)){
				ReflectionUtil.setField(field, vo, rs.getInt(columnIndex));
			}else if(Date.class.isAssignableFrom(type)){
				ReflectionUtil.setField(field, vo, rs.getDate(columnIndex));
			}else if(BigDecimal.class.isAssignableFrom(type)){
				ReflectionUtil.setField(field, vo, rs.getBigDecimal(columnIndex));
			}
		}
		return vo;
	}
    

}