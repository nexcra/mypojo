package erwins.util.spring.batch.component;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

import erwins.util.lib.ReflectionUtil;
import erwins.util.root.exception.TextParseException;
import erwins.util.spring.batch.CsvItemReader.CsvMapper;
import erwins.util.spring.batch.CsvItemWriter.CsvAggregator;
import erwins.util.text.StringUtil;

/**
 * 1. 스프링 컨버터로 수정할것!
 * 2. 익스 / 인클루드 만들것! (이것도 스프링 컨버터에..)
 *  */
public class ReflextionCsvConverter<T> implements CsvMapper<T>,CsvAggregator<T>{
	
	private final Class<T> persistentClass;
	private List<Field> fieldList;
	//private DateTimeFormatter dateTimeFormatter;
	private Logger log = LoggerFactory.getLogger(this.getClass());
	
	public static <T> ReflextionCsvConverter<T> create(Class<T> persistentClass,String ... fildNames){
		ReflextionCsvConverter<T> mapper = new ReflextionCsvConverter<T>(persistentClass,fildNames);
		return mapper;
	}
    
	/** 
	 * 필드 이름으로 정렬해준다. 이름순 = CSV 기록순
	 * 필드가 추가되면 기존 순서가 틀려질 수 있으니 주의해서 사용해야 한다. 운영되는 업무에는 사용하면 안됨 
	 *  */
	private ReflextionCsvConverter(Class<T> persistentClass,String ... fildNames) {
        this.persistentClass = persistentClass;
        
        if(fildNames.length==0){
        	fieldList = ReflectionUtil.getAllDeclaredFields(persistentClass);
        	Collections.sort(fieldList, new Comparator<Field>() {
    			@Override
    			public int compare(Field arg0, Field arg1) {
    				return arg0.getName().compareTo(arg1.getName());
    			}
    		});
        }else{
        	fieldList = Lists.newArrayList();
        	Map<String,Field> fieldMap = ReflectionUtil.getAllDeclaredFieldMap(persistentClass);
        	for(String fildName : fildNames){
        		Field field = fieldMap.get(fildName);
        		Preconditions.checkArgument(field != null , "field가 존재하지 않습니다. " + fildName);
        		fieldList.add(field);
        	}
        }
    }
    

	@SuppressWarnings("unchecked")
	@Override
	public T mapLine(String[] line, int lineNumber) throws Exception {
		T vo = ReflectionUtil.newInstance(persistentClass);
		for(int i=0;i < fieldList.size();i++){
			Field field = fieldList.get(i);
			String stringValue = line[i];
			Class<?> type = field.getType();
			boolean empty = stringValue.equals("");
			
			try{
				if(empty){
					//nulll로 오버라이드 한다. 주의!
					ReflectionUtil.setField(field, vo, null); 
				}else{
					Object value = null;
					
					if(String.class.isAssignableFrom(type)){
						value = stringValue;
					}else if(ReflectionUtil.isAssignableFrom(type, Long.class,long.class)){
						value = Long.valueOf(stringValue);
					}else if(ReflectionUtil.isAssignableFrom(type, Integer.class,int.class)){
						value = Integer.valueOf(stringValue);
					}else if(ReflectionUtil.isAssignableFrom(type, Boolean.class,boolean.class)){
						value = Boolean.parseBoolean(stringValue);					
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
			}catch(Throwable e){
				String msg = MessageFormat.format("line -> vo 변환중 예외. field:[{0}] value:[{1}] /  lineNumber:[{2}] / 전체자료:[{3}]"
						, field.getName(),stringValue,lineNumber,StringUtil.join(line, ","));
				throw new TextParseException(msg,e);
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
	
	/** 순서 확인용 */ 
	public List<String> header() {
		List<String> fieldNameList = Lists.newArrayList();
		for(int i=0;i < fieldList.size();i++){
			Field field = fieldList.get(i);
			fieldNameList.add(field.getName());
		}
		return fieldNameList;
	}
	
	

	

}
