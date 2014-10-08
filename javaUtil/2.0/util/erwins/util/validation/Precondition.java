package erwins.util.validation;

import java.lang.reflect.Field;
import java.util.Map;

import org.apache.commons.lang.exception.ExceptionUtils;

import com.google.common.base.Preconditions;

import erwins.util.lib.ReflectionUtil;

/**
 * Preconditions의 추가버전
 * catch 가능한 예외를 던진다.//UnsupportedOperationException
 * 
 * 스프링 시큐리티의 ThrowableAnalyzer를 활용할것
 * 
 * 나중에 기능 추가할것
 */
public abstract class Precondition extends ExceptionUtils{
	
	/**
	 * 사용자가 정확한 값을 입력했는가?
	 */
	public static void isPositive(Number count, String message) {
		if (count != null && count.doubleValue() > 0) return;
		throw new InputValidationException(message);
	}
/*
	*//**
	 * 빈값이 아님.
	 *//*
	public static void isNotEmpty(Object obj, String message){
		if(ReflectionUtil.isEmpty(obj)) throw new InputValidationException(message);
	}*/
	
/*	public static void isNotEmpty(Object obj){
		isNotEmpty(obj,"object is empty : " + obj);
	}
	*/
	public static void isTrue(boolean tf,String msg) {
		if(!tf) throw new InputValidationException(msg);
	}
	public static void isTrue(boolean tf) {
		isTrue(tf,"is not true");
	}

	public static void isEquals(Object a, Object b,String msg) {
		isTrue(a.equals(b),msg);
	}
	public static void isEquals(Object a, Object b) {
		isEquals(a,b,"is not same object!");
	}
	
	/** 리플렉션으로 해당 필드들의 널 여부를 체크한다. */
	public static void checkNotNulls(Object vo,String ... fieldNames) {
		Map<String,Field> map = ReflectionUtil.getAllDeclaredFieldMap(vo.getClass());
		String toString = vo.toString();
		for(String name : fieldNames){
			Field field = map.get(name);
			Preconditions.checkNotNull(field,name +" is required field : " + toString);
			Object value = ReflectionUtil.getField(field, vo);
			Preconditions.checkNotNull(value, name +" is required value : " + toString);
		}
	}


}
