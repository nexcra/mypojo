package erwins.util.spring.batch.component;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import erwins.util.lib.ReflectionUtil;
import erwins.util.spring.batch.CsvItemReader.CsvMapper;
import erwins.util.spring.batch.CsvItemWriter.CsvAggregator;

/**
 * 1. 스프링 컨버터로 수정할것!
 * 2. 익스 / 인클루드 만들것! (이것도 스프링 컨버터에..)
 *  */
public class ReflextionCsvConverter<T> implements CsvMapper<T>,CsvAggregator<T>{
	
	private final Class<T> persistentClass;
	private List<Field> fieldList;
	//private DateTimeFormatter dateTimeFormatter;
	private Logger log = LoggerFactory.getLogger(this.getClass());
	
	public static <T> ReflextionCsvConverter<T> create(Class<T> persistentClass){
		ReflextionCsvConverter<T> mapper = new ReflextionCsvConverter<T>(persistentClass);
		return mapper;
	}
    
	/** 필드 이름으로 정렬해준다. 이름순 = CSV 기록순 */
	private ReflextionCsvConverter(Class<T> persistentClass) {
        this.persistentClass = persistentClass;
        fieldList = ReflectionUtil.getAllDeclaredFields(persistentClass);
        Collections.sort(fieldList, new Comparator<Field>() {
			@Override
			public int compare(Field arg0, Field arg1) {
				return arg0.getName().compareTo(arg1.getName());
			}
		});
    }

	@SuppressWarnings("unchecked")
	@Override
	public T mapLine(String[] line, int arg1) throws Exception {
		T vo = ReflectionUtil.newInstance(persistentClass);
		for(int i=0;i < fieldList.size();i++){
			Field field = fieldList.get(i);
			String stringValue = line[i];
			Class<?> type = field.getType();
			boolean empty = stringValue.equals("");
			
			if(empty){
				//nulll로 오버라이드 한다. 주의!
				ReflectionUtil.setField(field, vo, null); 
			}else{
				Object value = null;
				if(String.class.isAssignableFrom(type)){
					value = stringValue;
				}else if(Long.class.isAssignableFrom(type)){
					value = Long.valueOf(stringValue);
				}else if(Integer.class.isAssignableFrom(type)){
					value = Integer.valueOf(stringValue);
				//}else if(Date.class.isAssignableFrom(type)){
					//value = dateTimeFormatter.parseDateTime(stringValue).toDate();
				}else if(BigDecimal.class.isAssignableFrom(type)){
					value = new BigDecimal(stringValue);
				}else if(Enum.class.isAssignableFrom(type)){
					value = ReflectionUtil.getEnumInstance((Class<Enum<?>>)type, stringValue);
				}else{
					log.warn("컬럼명 {}는 클래스 {}에 없는 필드입니다.",field.getName(),persistentClass.getSimpleName());
				}
				ReflectionUtil.setField(field, vo, value); 
			}
			
		}
		return vo;
	}
	
	@Override
	public String[] aggregate(T vo) {
		String[] line = new String[fieldList.size()];
		for(int i=0;i < fieldList.size();i++){
			Field field = fieldList.get(i);
			Object value = ReflectionUtil.getField(field, vo);
			//변환작업은 알아서 할것. or 스프링 컨버터랑 연계함
			line[i] = value==null ? "" : value.toString();
		}
		return line;
	}
	
	

	

}
