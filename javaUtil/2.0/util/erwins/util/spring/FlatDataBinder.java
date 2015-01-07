package erwins.util.spring;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import lombok.Data;

import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.DataBinder;
import org.springframework.validation.Validator;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import erwins.util.lib.CompareUtil;
import erwins.util.lib.ReflectionUtil;
import erwins.util.spring.batch.component.CsvItemReader.CsvMapper;
import erwins.util.spring.batch.component.CsvItemWriter.CsvAggregator;

/** 스프링 DataBinder 를 사용하는 컬렉션 바인더. 로우별 변환 / 역변환을 제공
 *  1. CSV / XLS 등의 Flat 데이터를 VO로 변환 & 예외처리
 *  2. DB or 로직에서 가져온 VO를 Flat 데이터로 변환 
 *   (JSTL은 상관없으니 JS용 GRID or Flex 등에서는 Flat화 해주어야 하는 경우가 가끔 있다. JQGrid는 aa.bb를 지원해서 flat화가 필요 없다.)
 * 이러한 바인더는 비용이 높기 때문에 VO변환비용조차 아까운 대량 처리에는 적합하지 않다.
 * 입력/반환되는 array size는 maxArraySize보다 크거나 같아야 한다.
 * bind는 스래드 안전하다.!
 * 제너릭 안할려다가..  너무 불편해서 다시 달았다.
 * 
 * 데이터 타입변환

	* Spring DataBinder 를 사용한 데이터 바인딩
		* 노트북 기준 10만건 1스래드 34초     개당 0.36밀리초
		* 노트북 기준 10만건 4스래드  10.24초  
		* 노트북 기준 10만건 8스래드  9.69초
	* VO변환테스트 100만건.  Long,String 2개 프로퍼티
		* VO 수동생성 / 미생성 : 0.43초 동일
		* Reflection 0.55
		* DataBinder : 7.5초.  초당 13만개 임으로 일반 로직에서는 아무 문제 없음
 * 
 *  */
@Data
public class FlatDataBinder<T> implements InitializingBean{

	private Class<?> clazz;
	private Integer maxArraySize;
	private List<LineMetadata> lineMetadatas;
	/** notnull 등은 업무에 따라 틀려지기때문에 개별 설정해준다. validation의 group을 사용하기 힘들어서 이걸로 대체 */
	private String[] requiredFields;
	/** 편의상 맵으로도 하나 만들어둔다. */
	private Map<String,LineMetadata> lineMetadataMap = Maps.newHashMap();
	@Resource
	private Validator validator;
	@Resource
	private ConversionService conversionService;
	
	@Override
	public void afterPropertiesSet() throws Exception {
		Preconditions.checkNotNull(lineMetadatas, "lineMetadatas is required");
		Preconditions.checkState(lineMetadatas.size() > 0);
		Collections.sort(lineMetadatas);
		if(maxArraySize==null) maxArraySize = lineMetadatas.get(lineMetadatas.size()-1).getIndex() + 1;
		if(validator==null){
			LocalValidatorFactoryBean tempValidator = new LocalValidatorFactoryBean(); 
			tempValidator.afterPropertiesSet();
			validator = tempValidator;
		}
		if(conversionService==null) conversionService = new DefaultConversionService();
		if(clazz==null) clazz = ReflectionUtil.genericClass(this.getClass(), 0);
		for(LineMetadata each :lineMetadatas){
			lineMetadataMap.put(each.getFieldName(), each);
		}
	}
	
	/** 예외 발생시 확인용 */
	public LineMetadata getLineMetadata(String fieldName){
		return lineMetadataMap.get(fieldName);
	}
	
	public String[] headers(){
		String[] headers = new String[maxArraySize];
		for(LineMetadata each :lineMetadatas){
			headers[each.getIndex()] = each.getName();
		}
		return headers;
	}
	
	/** CompareUtil.isEqualIgnoreNull 로 비교한다. 그냥 CompareUtil를 쓰는게 더 나을듯 */
	public boolean isEquals(T a,T b){
		ExpressionParser parser = new SpelExpressionParser();
		for(LineMetadata each :lineMetadatas){
			Object aValue = SpringUtil.elValue(parser, each.getFieldName(), a);
			Object bValue = SpringUtil.elValue(parser, each.getFieldName(), b);
			boolean equals = CompareUtil.isEqualIgnoreNull(aValue, bValue);
			if(!equals) return false;
		}
		return true;
	}
	
	@SuppressWarnings("unchecked")
	public T bind(String[] array,int lineNumber) throws BindException{
		DataBinder binder = bindWithoutClose(array,lineNumber);
		binder.close();
		return (T) binder.getTarget();
	}
	
	public CsvMapper<T> getCsvMapper(){
		return new CsvMapper<T>() {
			@Override
			public T mapLine(String[] lines, int lineNumber) throws Exception {
				return bind(lines,lineNumber);
			}
		};
	}
	
	/** 어디서 쓰는지 불명   */
	public DataBinder bindWithoutClose(String[] array,int lineNumber){
		MutablePropertyValues kv = toProperty(array);
		DataBinder  binder = new DataBinder(ReflectionUtil.newInstance(clazz),clazz.getSimpleName() + " - " + lineNumber);
		binder.setConversionService(conversionService);
		binder.setValidator(validator);
		if(requiredFields!=null) binder.setRequiredFields(requiredFields);
		binder.bind(kv);
		binder.validate();
		return binder;
	}
	
	/** 커스터마이징된 결과를 리턴한다. CSV같은거 업로드 처리용  */
	public FlatBindingResult bindWithResult(String[] array,int lineNumber){
		MutablePropertyValues kv = toProperty(array);
		DataBinder  binder = new DataBinder(ReflectionUtil.newInstance(clazz),clazz.getSimpleName() + " - " + lineNumber);
		binder.setConversionService(conversionService);
		binder.setValidator(validator);
		if(requiredFields!=null) binder.setRequiredFields(requiredFields);
		binder.bind(kv);
		binder.validate();
		
		BindingResult bindingResult = binder.getBindingResult();
		FlatBindingResult newResult = new FlatBindingResult(bindingResult,this);
		return newResult;
	}

	/**  
	 * array 가 더 크다면 큰 부분은 무시되어야한다.
	 * array 가 더 작다면 null처리 되어야한다.
	 *   */
	private MutablePropertyValues toProperty(String[] array) {
		//Preconditions.checkState(array.length >= maxArraySize, "too small array : " + maxArraySize);
		MutablePropertyValues kv = new MutablePropertyValues();
		for(LineMetadata each :lineMetadatas){
			String value = null; //인덱스에 없는 값은 무시.   이를 ""로 할지 null로 할지는 사용해보고 판단.
			if(each.getIndex() < array.length) value = array[each.getIndex()];
			kv.add(each.getFieldName(), value);
		}
		return kv;
	}
	
	/** 프로퍼티 에디터 적용은 나중에 만들자 */
	public String[] toStringArray(T vo){
		String[] result = new String[maxArraySize];
		ExpressionParser parser = new SpelExpressionParser();
		for(LineMetadata each :lineMetadatas){
			String stringValue = findStringValue(parser,each,vo);
			result[each.getIndex()] = stringValue;
		}
		return result;
	}
	
	public CsvAggregator<T> getCsvAggregator(){
		return new CsvAggregator<T>(){
			@Override
			public String[] aggregate(T item) {
				return toStringArray(item);
			}
		};
	}
	
	/** 프로퍼티 에디터 적용은 나중에 만들자. map의 경우 인덱스는 무시한다. */
	public Map<String,String> toMap(T vo){
		Map<String,String> result = Maps.newConcurrentMap();
		ExpressionParser parser = new SpelExpressionParser();
		for(LineMetadata each :lineMetadatas){
			String stringValue = findStringValue(parser,each,vo);
			result.put(each.getFieldName(), stringValue);
		}
		return result;
	}
	
	public List<Map<String,String>> toMap(List<T> vos){
		List<Map<String,String>> list = Lists.newArrayList();
		for(T each : vos) list.add(toMap(each));
		return list;
	}
	
	private String findStringValue(ExpressionParser parser,LineMetadata each,T vo){
		Object value = SpringUtil.elValue(parser, each.getFieldName(), vo);
		if(value==null) return null;
		if(conversionService.canConvert(value.getClass(), String.class)){
			return conversionService.convert(value, String.class);
		}
		return value.toString();
	}

}
