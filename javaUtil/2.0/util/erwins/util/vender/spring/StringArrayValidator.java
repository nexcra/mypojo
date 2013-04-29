package erwins.util.vender.spring;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.nio.charset.Charset;
import java.text.MessageFormat;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.validation.ValidationException;

import org.apache.ibatis.session.ResultContext;

import au.com.bytecode.opencsv.CSVReader;

import com.google.common.base.Objects;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import erwins.util.lib.CharEncodeUtil;
import erwins.util.lib.ReflectionUtil;
import erwins.util.lib.StringUtil;
import erwins.util.vender.apache.Poi;
import erwins.util.vender.apache.PoiSheetReader2002;
import erwins.util.vender.etc.OpenCsv;
import erwins.util.vender.etc.OpenCsvMybatisResultHandler;
import erwins.util.vender.spring.CsvItemReader.CsvMapper;
import erwins.util.vender.spring.CsvItemWriter.CsvAggregator;
import erwins.util.vender.spring.StringValidator.FieldToStringAble;


/** CSV나 엑셀 업로드시 사용자 입력값을 검증하는 벨리데이터
 * 아직 엑셀은 지원 안함?? */
public class StringArrayValidator<T> implements CsvMapper<T>,CsvAggregator<T>{
	
	private Class<T> clazz;
	private Map<LineMetadata,List<StringValidator>> validatorMap = Maps.newTreeMap();
	private Map<String,Field> fieldMap = Maps.newHashMap();
	
	public StringArrayValidator(Class<T> clazz){
		this.clazz = clazz;
		init();
	}

	/** 별도로 init() 해주어야함 */
	public StringArrayValidator(){
		this.clazz = ReflectionUtil.genericClass(this.getClass(), 0);
		init();
	}

	
	protected void init() {
		List<Field> fields = ReflectionUtil.getAllDeclaredFields(clazz);
		for(Field each : fields){
			each.setAccessible(true);
			fieldMap.put(each.getName(), each);
		}
	}
    
    public void add(Integer index,String fieldName,String name,StringValidator ... validator){
    	validatorMap.put(new LineMetadata(index,fieldName,name), Lists.newArrayList(validator));
    }
    
    /** XLS의 경우 CSV와는 다르게, 시트명을 추가로 입력해준다 */
    public List<T> validateXls(PoiSheetReader2002 sheet,boolean skipFirst) throws InputStringViolationException{
    	List<T> list = Lists.newArrayList();
    	String sheetName = sheet.getSheetName();
        List<String[]> data = sheet.list();
        for(int i=0;i<data.size();i++){
        	if(skipFirst && i==0) continue;
        	String[] lines = data.get(i);
        	try {
				list.add(validateLine(lines, i+1));
			} catch (InputStringViolationException e) {
				e.setSheetName(sheetName);
				throw e;
			}
        }
        return list;
    }
    
    
    /** 벨리데이션 체크 후 즉시 예외를 던진다 */
    public void validateCsv(InputStream in,boolean skipFirst) throws InputStringViolationException{
    	CSVReader reader = new CSVReader(new InputStreamReader(in, Charset.forName("MS949")));
        try {
        	int row = 1;
        	if(skipFirst) {
        		reader.readNext();
        		row++;
        	}
            for(String[] lines=reader.readNext();lines != null;lines=reader.readNext()){
            	validateLine(lines, row);
            }
        } catch (IOException e) {
			throw new RuntimeException(e);
		} finally {
			OpenCsv.closeQuietly(reader);
        }
    }
    
    /** 인메모리에서 모두 처리할때 사용한다 */
    public List<T> validateCsvAndReturnValue(InputStream in,boolean skipFirst) throws InputStringViolationException{
    	CSVReader reader = new CSVReader(new InputStreamReader(in, Charset.forName("MS949")));
    	List<T> list = Lists.newArrayList();
        try {
        	int row = 1;
        	if(skipFirst) {
        		reader.readNext();
        		row++;
        	}
            for(String[] lines=reader.readNext();lines != null;lines=reader.readNext()){
            	T vo = validateLine(lines, row);
            	list.add(vo);
            }
        } catch (IOException e) {
			throw new RuntimeException(e);
		} finally {
			OpenCsv.closeQuietly(reader);
        }
        return list;
    }
    
    /** 성능에 문제가 된다면 리플렉션 뺀 버전 만들자. */
    public T validateLine(String[] lines,int row) throws InputStringViolationException{
    	T vo = ReflectionUtil.newInstance(clazz);
    	for(Entry<LineMetadata,List<StringValidator>> entry : validatorMap.entrySet()){
    		LineMetadata meta = entry.getKey();
    		try {
				List<StringValidator> validators = entry.getValue();
				String value = lines.length > meta.index ? lines[meta.index] : "";
				value = value==null ? "" : value.trim();
				Object successValue = null;
				for(StringValidator each : validators){
					Object thisResult = each.validate(value, row, meta); 
					if(thisResult!=null) successValue= thisResult;
					
				}
				Field field = fieldMap.get(meta.fieldName);
				ReflectionUtil.setField(field, vo, successValue);
			} catch (ValidationException e) {
				throw e;
			} catch (Exception e) {
				String msg = "validateLine중 예외. fieldName : {0} ";
				throw new RuntimeException(MessageFormat.format(msg, meta.fieldName),e);
			}
    	}
    	return vo;
    }
    
    public String[] header(){
    	String[] header = new String[validatorMap.size()];
    	for(LineMetadata each : validatorMap.keySet()){
    		header[each.index] = each.name;
    	}
    	return header;
    }
    
    public static class LineMetadata implements Comparable<LineMetadata>{
    	public final String name;
    	public final String fieldName;
    	public final Integer index;
		public LineMetadata(Integer index,String fieldName,String name) {
			this.name = name;
			this.index = index;
			this.fieldName = fieldName;
		}
		@Override
		public int compareTo(LineMetadata o) {
			return index.compareTo(o.index);
		}
    }
    
    /** CSV에 입력된 데이터(item)가 비어있지 않다면 DB데이터를 해당 데이터로 수정해서 리턴한다.
     * @return DB데이터의 변경 여부  */
	public boolean updateIfNotEmpty(T item,T db,String ...  ignoreFieldNames) {
		boolean dirty = false;
    	for(LineMetadata meta : validatorMap.keySet()){
    		if(StringUtil.isEquals(meta.fieldName, ignoreFieldNames)) continue;
    		Field field = fieldMap.get(meta.fieldName);
    		Object value1 = ReflectionUtil.getField(field, item);
    		if(value1==null || value1.toString().equals("")) continue;
    		Object value2 = ReflectionUtil.getField(field, db);
    		if(value1.equals(value2)) continue;
    		ReflectionUtil.setField(field, db, value1);
    		dirty = true;
    	}
		return dirty;
	}
    
    /** CSV에 입력되는 해당 객체가 동일한지 판단한다.
     * null과 "" 는 동일한것으로 간주
     * 양쪽 다 null 필드라면 동일한것으로 간주  */
	public boolean isEquals(T item,T db,String ...  ignoreFieldNames) {
    	for(LineMetadata meta : validatorMap.keySet()){
    		if(StringUtil.isEquals(meta.fieldName, ignoreFieldNames)) continue;
    		Field field = fieldMap.get(meta.fieldName);
    		Object value1 = ReflectionUtil.getField(field, item);
    		if(value1==null) value1 = "";
    		Object value2 = ReflectionUtil.getField(field, db);
    		if(value2==null) value2 = "";
    		if(!Objects.equal(value1, value2)) return false;
    	}
		return true;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public String[] aggregate(T item) {
		String[] result = new String[validatorMap.size()];
    	for(Entry<LineMetadata, List<StringValidator>> entry : validatorMap.entrySet()){
    		LineMetadata meta = entry.getKey();
    		Field field = fieldMap.get(meta.fieldName);
    		Object value = ReflectionUtil.getField(field, item);
    		Object transValue = value;
    		
    		for(StringValidator each : entry.getValue()){
    			if(each instanceof FieldToStringAble){
    				try {
						FieldToStringAble toString = (FieldToStringAble) each;
						Object eachTransValue = toString.fieldToString(value); 
						if(eachTransValue!=null) transValue =  eachTransValue;
					} catch (Exception e) {
						//무시한다
					}
    			}
    		}
    		if(transValue==null) transValue = "";
    		result[meta.index] = transValue.toString();
    	}
		return result;
	}

	@Override
	public T mapLine(String[] lines, int lineNumber) throws Exception {
		return validateLine(lines, lineNumber);
	}
	
	/** bybatis 스트림 CSV 다운로드용 핸들러 */
	public OpenCsvMybatisResultHandler getOpenCsvMybatisResultHandler(){
		OpenCsvMybatisResultHandler handler = new OpenCsvMybatisResultHandler() {
			@Override
			public String[] resultContexttoCsv(ResultContext arg0) {
				@SuppressWarnings("unchecked")
				T item = (T)arg0.getResultObject();
				return aggregate(item);
			}
		};
		handler.setEncoding(CharEncodeUtil.C_MS949);
		return  handler;
	}
	
	/** 간단히 xls를 만들때 사용한다 */
    public void writePoi(Poi p,String sheetName,Collection<T> groups){
        p.addSheet(sheetName, this.header());
        for(T each : groups){
            p.addValuesArray(aggregate(each));
        }
    }
	

}
