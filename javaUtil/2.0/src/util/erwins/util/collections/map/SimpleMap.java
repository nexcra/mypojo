package erwins.util.collections.map;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.commons.lang.StringEscapeUtils;

import erwins.util.exception.BusinessException;
import erwins.util.lib.DayUtil;
import erwins.util.lib.FormatUtil;
import erwins.util.lib.MathUtil;
import erwins.util.lib.ReflectionUtil;
import erwins.util.lib.RegEx;
import erwins.util.lib.StringUtil;
import erwins.util.reflexive.Connectable;
import erwins.util.root.Pair;


/**
 * null과 ID등의 처리를 이곳에서 집중 체크한다.
 */

@SuppressWarnings("serial")
public class SimpleMap<KEY> implements Map<KEY,Object>,Serializable{
	
	protected final Map<KEY,Object> theMap;
	
	public SimpleMap(){
		theMap = new HashMap<KEY,Object>();
	}
	public SimpleMap(Map<KEY,Object> map){
		theMap = map;
	}
    
    // ===========================================================================================
    //                                    변환기
    // ===========================================================================================
    /**
     * 모든 String문자열은 이곳을 통과한다.
     * 이곳에 조건을 추가한다.
     * 주의.. value는 전달되는 래퍼런스가 아니다. 불변객체이다. return을 받아야 한다.
     */
    public static Boolean toBoolean(Object obj){
        if(obj==null) return null;
        else if(obj instanceof Boolean) return (Boolean)obj;
        String value = obj.toString();
        if(StringUtil.isEqualsIgnoreCase(value, "Y","1","ON","true")) return true;
        else if(StringUtil.isEqualsIgnoreCase(value, "N","0","OFF","false")) return false;
        else return null;
    }
    
    public static Integer toInteger(String value){
        if(value==null || value.equals("")) return 0;
        return Integer.parseInt(value);
    }
    
    public static Long toLong(String value){
        if(value==null || value.equals("")) return 0L;
        return Long.parseLong(value);
    }
    
    // ===========================================================================================
    //                             일반 반환        
    // ===========================================================================================    
    
    /**
     * 문자 값을 반환한다.
     */
    public String getString(Object key) {
        Object obj =  theMap.get(key);
        if(obj==null) return "";
        return obj.toString();
    }
    
    /**
     * 문자값중 숫자형만 남겨서 반환한다.
     */
    public String getNumericString(Object key) {
        return StringUtil.getNumericStr(get(key));
    }
    
    /** 8자리 숫자를 Calendar형식으로 반환한다. 시각은 0시로 초기화된다. */
    public Calendar getCalendarBy8Char(Object key) {
    	String str =  StringUtil.getNumericStr(get(key));
    	if(str==null) return null;
    	return DayUtil.getCalendar(str);
    }
    
    /**
     * req에서 null safe String문자열을 추출한다.
     */
    public String[] getStrings(String key){
        Object temp = theMap.get(key);
        if(temp==null) return new String[]{};
        else if(!(temp instanceof Object[])) return new String[]{temp.toString()};
        String[] values = (String[])temp;
        String[] strings = new String[values.length];
        for(int i=0;i<values.length;i++) strings[i] = StringUtil.nvl(values[i]);
        return strings;
    }
    
    public Integer getInteger(Object key) {
        Object obj =  theMap.get(key);
        if(obj==null) return null;
        if(obj instanceof Integer) return (Integer) obj;
        if(obj instanceof Number) return ((Number) obj).intValue();
        String value = obj.toString();
        if(value.equals("")) return 0;
        return toInteger(value);
    }
    
    public Integer[] getIntegers(String str){
        String[] values = getStrings(str);
        if(values==null) return new Integer[]{};
        Integer[] Longs = new Integer[values.length];
        for(int i=0;i<values.length;i++) Longs[i] = toInteger(values[i]);
        return Longs;
    }    
    
    /**
     * 특이한 경우(DB의 키값)로  공백문자시 null을 리턴한다.
     * null이 허용되지 않는 원시형은 사용될 수 없다.
     */
    public Integer getIntegerId(Object key) {
    	Object obj =  theMap.get(key);
        if(obj==null) return null;
        if(obj instanceof Integer) return (Integer) obj;
        if(obj instanceof Number) return ((Number) obj).intValue();
        String value = obj.toString();
        if(obj.toString().equals("")) return null;
        return Integer.parseInt(value);
    }
    
    public Integer[] getIntegerIds(String str){
        String[] values = getStrings(str);
        if(values==null) return new Integer[]{};
        Integer[] integers = new Integer[values.length];
        for(int i=0;i<values.length;i++)
            integers[i] = (values[i].equals("") || values[i].equals("0")) ? null : Integer.parseInt(values[i]);;
        return integers;
    }
    
    public Long getLong(String key){        
        Object obj =  theMap.get(key);
        if(obj instanceof Long) return (Long)obj;
        return toLong((String)obj);
    }
    
    /**
     * 특이한 경우(DB의 키값)로 0 또는 공백문자시 null을 리턴한다.
     * null이 허용되지 않는 원시형은 사용될 수 없다.
     */
    public Long getLongId(String key){
        Object obj =  theMap.get(key);
        if(obj==null) return null;
        if(obj instanceof Long) return ((Long) obj).longValue();
        String str = obj.toString();
        if(obj.toString().equals("")) return null;
        return Long.parseLong(str);
    }
    
    public Long[] getLongIds(String str){
        String[] values = getStrings(str);
        if(values==null) return new Long[]{};
        Long[] longs = new Long[values.length];
        for(int i=0;i<values.length;i++)
            longs[i] = (values[i].equals("") || values[i].equals("0") ) ? null : Long.parseLong(values[i]);;
        return longs;
    }
    
    /**
     * null이면 null을 리턴한다.
     */
    public Long getLongId(Object key) {
        Object obj =  theMap.get(key);
        if(obj==null) return null;
        if(obj instanceof Number) return ((Number) obj).longValue();
        String str = obj.toString();
        if(obj.toString().equals("")) return null;
        return Long.parseLong(str);
    }

    public Long[] getLongs(String str){
        String[] values = getStrings(str);
        if(values==null) return new Long[]{};
        Long[] longs = new Long[values.length];
        for(int i=0;i<values.length;i++) longs[i] = toLong(values[i]);
        return longs;
    }
    
    /**
     * null safe한 BigDecimal 값을 반환한다.
     * ""이 들어올 경우 0을 리턴한다.
     */
    public BigDecimal getDecimal(Object key) {
        Object obj =  theMap.get(key);
        if(obj instanceof BigDecimal) return (BigDecimal) obj;
        else if(obj instanceof Number) return new BigDecimal((Integer)obj);
        else if(obj instanceof Double) return new BigDecimal((Double)obj);
        else{
            if(obj==null || obj.toString().equals("")) return BigDecimal.ZERO;
            return StringUtil.getDecimal(obj.toString());
        }
    }
    public BigDecimal[] getDecimals(String key){
        String[] values = getStrings(key);
        if(values==null) return new BigDecimal[]{};
        BigDecimal[] decimals = new BigDecimal[values.length];
        for(int i=0;i<values.length;i++) decimals[i] = StringUtil.getDecimal(values[i]);
        return decimals;
    }    
    
    /**
     * 특수 케이스.. 나중에 수정하자.
     * String 문자열을 boolean값으로 치환한다.
     * null이 입력되거나 해당하는 값이 없을 경우 null을 리턴한다. 이때는 디폴트를 사용하자.
     */    
    public Boolean getBoolean(String key){
        Object obj =  theMap.get(key);
        return toBoolean(obj);
    }
    
    public Boolean[] getBooleans(String key){
        String[] values = getStrings(key);
        if(values==null) return new Boolean[]{};
        Boolean[] strings = new Boolean[values.length];
        for(int i=0;i<values.length;i++) strings[i] = toBoolean(values[i]);
        return strings;
    }
    
    // ===========================================================================================
    //                                   특수? 반환 
    // ===========================================================================================
    
    
    /**
     * Enum 값을 반환한다. 표준 작성만 가능하다. (Constructor가 있어야 한다.)
     */
    @SuppressWarnings("unchecked")
	public <T extends Enum<?>> T getEnum(Class<T> clazz,String key){
        if(isEmpty(key)) return null;
        if(Pair.class.isAssignableFrom(clazz)){
        	return (T) ReflectionUtil.getEnumPairInstance((Class<Enum<?>>) clazz, getString(key));	
        }
        return ReflectionUtil.getEnumInstance(clazz, getString(key));
    }

    
    /**
     * BigDecimal 들의 합을 리턴한다.
     */
    public BigDecimal getSum(Object ... keys) {
        if(keys.length == 0) keys = this.keySet().toArray();
        BigDecimal sum = BigDecimal.ZERO;
        for(Object key : keys )sum =  sum.add(getDecimal(key));
        return sum;
    }
    
    /**
     * BigDecimal 들의 합에 대한 key의 rate를 구한다.
     * 저장된 모든 객체는 Decimal이어야 한다.
     * 소수점 이하 8자리까지 구한다. ex) 0.23564875
     */
    public BigDecimal getRate(Object key) {
        BigDecimal sum = getSum();
        BigDecimal value = getDecimal(key);        
        if(MathUtil.isZero(value,sum)) return BigDecimal.ZERO;
        return value.divide(sum,8,BigDecimal.ROUND_HALF_UP); //미검증!!
    }
    
    // ===========================================================================================
    //                                    문자열 변환
    // ===========================================================================================
    
    /**
     * HTML,javaScript 값으로 escape한다.
     */
    public String toEscapedStr(Object key) {
        return StringEscapeUtils.escapeHtml(getString(key));
        //return Encoders.escapeJavaScript(Encoders.escapeXml(str));
    }

    /**
     * 주민등록번호를 변환한다.
     */
    public String toSid(Object key) {
        return FormatUtil.toSid(getString(key));
    }
    
    /**
     * 전화번호로 변환한다.
     */
    public String toTel(Object key) {
        return FormatUtil.toTel(getString(key));
    }
    
    /**
     * yyyy-MM-dd형식으로 변환한다.
     */
    public String toDate(Object key) {
        return FormatUtil.toDate(getString(key));
    }
    
    /**
     * yyyy-MM-dd형식으로 변환한다.
     */
    public String toWon(Object key) {
        return FormatUtil.toWon(getDecimal(key));
    }

    /**
     * 사업자등록번호를 변환한다.
     */
    public String toBid(Object key) {
        return FormatUtil.toBid(getString(key));
    }
    
    /**
     * 숫자를 ##,###원 형태로 리턴한다.
     * 오라클 number가 BigDecimal로 매핑됨으로 이것을 표준으로 한다.
     */
    public String toNumeric(Object key) {         
        Object value = theMap.get(key);
        if (value == null) return "";
        if(value instanceof BigDecimal){
            BigDecimal d = (BigDecimal)value;
            return FormatUtil.INT.get(d);
        }if(value instanceof Integer){
            Integer d = (Integer)value;
            return FormatUtil.INT.get(d);
        }
            
        return value.toString();
    }
    
    // ===========================================================================================
    //                             검사        
    // ===========================================================================================
    
    /**
     * key가 비어있는지 검사. 
     */
    public boolean isEmpty(Object key){
        Object value = get(key);
        if(value==null || value.toString().equals("")) return true;
        return false;
    }
    
    /**
     * key가 null인지 검사. null과 ""은 다르다! 
     */
    public boolean isNull(Object key){
        Object value = get(key);
        if(value==null) return true;
        return false;
    }
    
    /**
     * 값의 배열여부를 반환한다.
     */
    public boolean isArrayValue(Object key) {
        Object value = get(key);
        return value instanceof Object[];
    }
    
    // ===========================================================================================
    //                             비교        
    // ===========================================================================================    
    
    public boolean isLargeThan(Object key,BigDecimal B) {
        return MathUtil.isLarge(getDecimal(key), B,false);
    }    
    public boolean isLargeThan(Object key,int B) {
        return MathUtil.isLarge(getDecimal(key), new BigDecimal(B),false);
    }
    public boolean isLargeThan(Object key,BigDecimal B,Boolean isSame) {
        return MathUtil.isLarge(getDecimal(key), B,isSame);
    }    
    public boolean isLargeThan(Object key,int B,Boolean isSame) {
        return MathUtil.isLarge(getDecimal(key), new BigDecimal(B),isSame);
    }
    
    // ===========================================================================================
    //                             입력        
    // ===========================================================================================    
    
    /** key에 해당하는 값이 비었으면 입력 */
    public void putIfEmpty(KEY key,Object value){
        if(isEmpty(key)) theMap.put(key,value);
    }
    
    /** key에 해당하는 값이 null이면 입력 */
    public void putIfNull(KEY key,Object value){
        if(isNull(key)) theMap.put(key,value);
    }
    
    /** value에 해당하는 값이 null이 아닐때만 입력 */
    public void putIfValueNotNull(KEY key,Object value){
        if(value==null) return;
        theMap.put(key,value);
    }
    /** value에 해당하는 값이 비어있지 않을때만 입력 */
    public void putIfValueNotEmpty(KEY key,Object value){
        if(ReflectionUtil.isEmpty(value)) return;
        theMap.put(key,value);
    }
    
    /**
     * 산식을 연산하여 map에 입력한다.
     * ex) map.put("summ", Maths.DIVIDE, B,C); 
     */
    public BigDecimal put(KEY key,MathUtil math,BigDecimal ... decimal){
        BigDecimal value = getDecimal(key);
        value = math.run(value,decimal);
        theMap.put(key,value);
        return value;
    }    
    /**
     * put명령 사용시 기존 수와 비교하여 더 큰수만 입력 
     * 주로 최대값을 얻을 때 사용된다.
     */
    public void putMaxInteger(KEY key,Integer value){
        Integer integer = (Integer)get(key);
        if(integer == null || integer < value) theMap.put(key, value);
    }
    /**
     * put명령 사용시 기존 수와 비교하여 더 큰수만 입력
     * 주로 최대값을 얻을 때 사용된다. 
     */
    public void putMaxDecimal(KEY key,BigDecimal value){
        BigDecimal decimal = (BigDecimal)get(key);
        if(decimal == null || decimal.compareTo(value) < -1) theMap.put(key, value);
    }
    
    /**
     * put명령 사용시 +1 
     * 디폴트로 Integer를 사용함. 기존값이 BigDecimal일 경우 BigDecimal을 더함.
     */
    public void plus(KEY key){
        Object obj = get(key);
        if(obj==null || obj instanceof Integer){
        	plus(key,1);
        }else if(obj instanceof BigDecimal){
        	plus(key,BigDecimal.ONE);
        }else{
            throw new RuntimeException(obj + " only numerical type allowed here!");
        }
    }
    
    /** 기존 값이 있으면 문자열을 계속 더해나간다. */
    public void appendString(KEY key,String value){
    	Object obj = get(key);
    	if(obj==null) theMap.put(key, value); 
    	else if(obj instanceof String){
        	String exist = (String)obj;
        	exist += value;
        	theMap.put(key,exist);
        }else throw new RuntimeException("existing value must be String. but " + obj.getClass().getName());
    }
    
    /**
     * put명령 사용시 기존 수를 더하여 입력 
     * 간단한 연산에 사용
     */
    public void plus(KEY key,Integer value){
        Integer integer = (Integer)get(key);
        if(integer == null) put(key, value);
        else theMap.put(key, integer + value);
    }
    
    /** null safe한 String을 입력 */
    public void plus(KEY key,String value){
    	if(StringUtil.isEmpty(value)) return;
    	plus(key,new BigDecimal(value));
    }
    
    /**
     * put명령 사용시 기존 수를 더하여 입력 
     * 간단한 연산에 사용
     */
    public void plus(KEY key,BigDecimal value){
    	theMap.put(key, getDecimal(key).add(value));
    }
    
    /**
     *  filterKey와 매치되는 key의 값을 해당 RegEx로 replace한다.
     **/
    public void replace(String filterKey,RegEx regEx,String replaced){
        filterKey = filterKey.toLowerCase();
        for(Map.Entry<KEY,Object> entry : theMap.entrySet()){
            String key = entry.getKey().toString().toLowerCase();
            if(!StringUtil.contains(key, filterKey)) continue;
            Object obj = entry.getValue();
            if(!(obj instanceof String)) continue;
            String returned =  regEx.replace(obj.toString(), replaced);
            theMap.put(entry.getKey(),returned);
        }
    }
    
    /** iBatis 등에서 코드를 일괄 변형할때 사용한다.  아.. ㅄ같아. */
	@SuppressWarnings("rawtypes")
	public void replace(KEY key,Connectable<Serializable,Connectable> parent){
    	Object code = get(key);
    	for(Connectable each :  parent.getChildren()){
    		if(each.getId().equals(code)){
    			theMap.put(key, each.getName());
    			return;
    		}
    	}
    	throw new BusinessException("{0} is not found for {1}",code,parent.getId());
    }
    
    // ===========================================================================================
    //                                    소팅
    // ===========================================================================================    
    
    public static final Comparator<Entry<String,Object>> ASC = new Comparator<Entry<String,Object>>() {
		@SuppressWarnings({ "unchecked", "rawtypes" })
		@Override
		public int compare(Entry<String,Object> o1, Entry<String,Object> o2) {
			return ((Comparable)o1.getValue()).compareTo(o2.getValue());
		}
	};
	public static final Comparator<Entry<String,Object>> DESC = new Comparator<Entry<String,Object>>() {
		@SuppressWarnings({ "unchecked", "rawtypes" })
		@Override
		public int compare(Entry<String,Object> o1, Entry<String,Object> o2) {
			return ((Comparable)o2.getValue()).compareTo(o1.getValue());
		}
	};
    
    /** value를 기준으로 정렬된 List를 리턴한다.  */
    public List<Entry<KEY,Object>> sortByValue(Comparator<Entry<KEY,Object>> comparator) {
    	List<Entry<KEY,Object>> list = new ArrayList<Entry<KEY,Object>>();
		for(Entry<KEY,Object> each : theMap.entrySet()) list.add(each);
		Collections.sort(list,comparator);
		return list;
    }
    
    /** 정렬된 Iterator를 리턴한다. 귀찮아서..  */
    public Iterator<Object> sortedItorator() {
        SortedMap<Object,Object> sorted = new TreeMap<Object,Object>();
        sorted.putAll(this);
        return sorted.values().iterator();
    }    
    
    /** 간단 캐스팅용... 쓸일이 없다. Entry가 안되서 ㅠㅠ */
    @SuppressWarnings("unchecked")
	public <T> Set<T> entrySet(Class<T> a) {
    	return (Set<T>)values();
    }    
    
    /* ================================================================================== */
	/*                                     위임메소                                               */
	/* ================================================================================== */
    
	public int size() {
		return theMap.size();
	}
	public boolean isEmpty() {
		return theMap.isEmpty();
	}
	public boolean containsKey(Object key) {
		return theMap.containsKey(key);
	}
	public boolean containsValue(Object value) {
		return theMap.containsValue(value);
	}
	public Object get(Object key) {
		return theMap.get(key);
	}
	public Object put(KEY key, Object value) {
		return theMap.put(key, value);
	}
	public Object remove(Object key) {
		return theMap.remove(key);
	}
	public void putAll(Map<? extends KEY, ? extends Object> m) {
		theMap.putAll(m);
	}
	public void putAll(KEY[] keys,Object[] values){
        if(keys.length != values.length) throw new IllegalArgumentException("args size must be same!");
        for(int i=0;i<keys.length;i++){
        	theMap.put(keys[i],values[i]);
        }
    }
	public void clear() {
		theMap.clear();
	}
	public Set<KEY> keySet() {
		return theMap.keySet();
	}
	public Collection<Object> values() {
		return theMap.values();
	}
	public Set<java.util.Map.Entry<KEY, Object>> entrySet() {
		return theMap.entrySet();
	}
	public boolean equals(Object o) {
		return theMap.equals(o);
	}
	public int hashCode() {
		return theMap.hashCode();
	}
    
    
    
    
    
}