package erwins.util.tools;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Set;

import lombok.Data;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import erwins.util.lib.CompareUtil;
import erwins.util.lib.ReflectionUtil;



/**
 * 간단비교툴 
 * 기능들 추가하자.
 */
@Data
public class CompareTool{
    
	/** eq 매치한다.  */
	private Set<Class<?>> ignore = Sets.newHashSet();
	/** eq 매치한다.  */
	private Set<String> ignoreField = Sets.newHashSet();
	
	public CompareTool addIgnore(Class<?> clazz){
		ignore.add(clazz);
		return this;
	}
	public CompareTool addIgnore(String fieldName){
		ignoreField.add(fieldName);
		return this;
	}
    
    /** 두개를 비교해서 틀린점을 리턴한다. */
    public <T> List<DirtyInfo> dirtyField(T before,T after){
    	List<DirtyInfo> dirtyFields = Lists.newArrayList();
    	List<Field> fields = ReflectionUtil.getAllDeclaredFields(before.getClass()); 
    	
    	for(Field field : fields){
    		if(ignore.contains(field.getType())) continue;
    		if(ignoreField.contains(field.getName())) continue;
    		Object aValue = ReflectionUtil.getField(field, before);
    		Object bValue = ReflectionUtil.getField(field, after);
    		boolean isEqual = CompareUtil.isEqualIgnoreNull(aValue, bValue);
    		if(!isEqual) {
    			DirtyInfo di = new DirtyInfo();
    			di.setField(field);
    			di.setBeforeValue(aValue);
    			di.setAfterValue(bValue);
    			dirtyFields.add(di);
    		}
    	}
    	return dirtyFields;
    }
    
    @Data
    public static class DirtyInfo{
    	private Field field;
    	private Object beforeValue;
    	private Object afterValue;
    	@Override
    	public String toString(){
    		return field.getName() + " : " + beforeValue + " => " + afterValue;
    	}
    }

}
