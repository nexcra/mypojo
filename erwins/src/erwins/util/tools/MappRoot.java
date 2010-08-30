package erwins.util.tools;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.HashMap;

import org.apache.commons.lang.StringEscapeUtils;

import erwins.util.lib.Clazz;
import erwins.util.lib.Days;
import erwins.util.lib.Formats;
import erwins.util.lib.Maths;
import erwins.util.lib.Strings;
import erwins.util.root.Pair;


/**
 * Mapp이 길어져서 2개로 나눔.
 * null과 ID등의 처리를 이곳에서 집중 체크한다.
 * @author erwins
 */

public abstract class MappRoot extends HashMap<Object,Object>  {
    
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
        if(Strings.isEqualsIgnoreCase(value, "Y","1","ON","true")) return true;
        else if(Strings.isEqualsIgnoreCase(value, "N","0","OFF","false")) return false;
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
    //                                    method
    // ===========================================================================================    
    
    ///** 추후 사용할것! */
    //private String preFix = null;
    
/*    private String CollectionName = null;
    
    *//**
     * 자식 객체일 경우 collection 이름을 앞에 붙여준다.
     * @uml.property  name="collectionName"
     *//*
    public void setCollectionName(String parent) {
        this.CollectionName = parent;
    }
    
    *//**
     * preFixse여부를 결정한다.
     *//*
    protected String getKey(String key){
        return CollectionName!=null ? CollectionName + key : key;
    }*/
    
    // ===========================================================================================
    //                             일반 반환        
    // ===========================================================================================    
    
    /**
     * 문자 값을 반환한다.
     */
    public String getStr(Object key) {
        Object obj =  super.get(key);
        if(obj==null) return "";
        return obj.toString();
    }
    
    /**
     * 문자값중 숫자형만 남겨서 반환한다.
     */
    public String getNumericStr(Object key) {
        return Strings.getNumericStr(get(key));
    }
    
    /** 8자리 숫자를 Calendar형식으로 반환한다. */
    public Calendar getCalendar(Object key) {
    	String str =  Strings.getNumericStr(get(key));
    	if(str==null) return null;
    	return Days.getCalendar(str);
    }
    
    /**
     * req에서 null safe String문자열을 추출한다.
     */
    public String[] getStrs(String key){
        Object temp = super.get(key);
        if(temp==null) return new String[]{};
        else if(!(temp instanceof Object[])) return new String[]{temp.toString()};
        String[] values = (String[])temp;
        String[] strings = new String[values.length];
        for(int i=0;i<values.length;i++) strings[i] = Strings.nvl(values[i]);
        return strings;
    }
    
    /**
     * int 값을 반환한다.
     * ","을 치환해준다. 나중에 정규식으로 바꾸자.
     */
    public Integer getIntValue(Object key) {
        Object obj =  super.get(key);
        if(obj==null) return null;
        if(obj instanceof Integer) return (Integer) obj;
        if(obj instanceof Number) return ((Number) obj).intValue();
        String value = obj.toString();
        if(value.equals("")) return 0;
        return Integer.parseInt(value.replaceAll(",",""));
    }
    
    /**
     * 특이한 경우(DB의 키값)로 0 또는 공백문자시 null을 리턴한다.
     * null이 허용되지 않는 원시형은 사용될 수 없다.
     */
    public Integer getIntId(Object key) {
        Object obj =  super.get(key);
        if(obj==null) return null;
        if(obj instanceof Number) return ((Number) obj).intValue();
        String str = obj.toString();
        if(obj.toString().equals("")) return null;
        return Integer.parseInt(str);        
    }
    
    public Integer[] getIntIds(String str){
        String[] values = getStrs(str);
        if(values==null) return new Integer[]{};
        Integer[] integers = new Integer[values.length];
        for(int i=0;i<values.length;i++)
            integers[i] = (values[i].equals("") || values[i].equals("0")) ? null : Integer.parseInt(values[i]);;
        return integers;
    }
    
    /**
     * Integer 값을 반환한다.
     */
    public Integer getInteger(Object key) {
        Object obj =  super.get(key);
        if(obj instanceof Integer) return (Integer)obj;
        return toInteger((String)obj);
    }
    
    public Integer[] getIntegers(String str){
        String[] values = getStrs(str);
        if(values==null) return new Integer[]{};
        Integer[] Longs = new Integer[values.length];
        for(int i=0;i<values.length;i++) Longs[i] = toInteger(values[i]);
        return Longs;
    }
    
    public Long getLong(String key){        
        Object obj =  super.get(key);
        if(obj instanceof Long) return (Long)obj;
        return toLong((String)obj);
    }
    
    /**
     * 특이한 경우(DB의 키값)로 0 또는 공백문자시 null을 리턴한다.
     * null이 허용되지 않는 원시형은 사용될 수 없다.
     */
    public Long getLongId(String key){
        Object obj =  super.get(key);
        if(obj==null) return null;
        if(obj instanceof Long) return ((Long) obj).longValue();
        String str = obj.toString();
        if(obj.toString().equals("")) return null;
        return Long.parseLong(str);
    }
    
    public Long[] getLongIds(String str){
        String[] values = getStrs(str);
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
        Object obj =  super.get(key);
        if(obj==null) return null;
        if(obj instanceof Number) return ((Number) obj).longValue();
        String str = obj.toString();
        if(obj.toString().equals("")) return null;
        return Long.parseLong(str);
    }

    public Long[] getLongs(String str){
        String[] values = getStrs(str);
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
        Object obj =  super.get(key);
        if(obj instanceof BigDecimal) return (BigDecimal) obj;
        else if(obj instanceof Integer) return new BigDecimal((Integer)obj);
        else if(obj instanceof Double) return new BigDecimal((Double)obj);
        else{
            if(obj==null || obj.toString().equals("")) return BigDecimal.ZERO;
            return Strings.getDecimal(obj.toString());
        }
    }
    public BigDecimal[] getDecimals(String key){
        String[] values = getStrs(key);
        if(values==null) return new BigDecimal[]{};
        BigDecimal[] decimals = new BigDecimal[values.length];
        for(int i=0;i<values.length;i++) decimals[i] = Strings.getDecimal(values[i]);
        return decimals;
    }    
    
    /**
     * 특수 케이스.. 나중에 수정하자.
     * String 문자열을 boolean값으로 치환한다.
     * null이 입력되거나 해당하는 값이 없을 경우 null을 리턴한다. 이때는 디폴트를 사용하자.
     */    
    public Boolean getBoolean(String key){
        Object obj =  super.get(key);
        return toBoolean(obj);
    }
    public Boolean[] getBooleans(String key){
        String[] values = getStrs(key);
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
        	return (T) Clazz.getEnumPair((Class<Enum<?>>) clazz, getStr(key));	
        }
        return Clazz.getEnum(clazz, getStr(key));
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
        if(Maths.isZero(value,sum)) return BigDecimal.ZERO;
        return value.divide(sum,8,BigDecimal.ROUND_HALF_UP); //미검증!!
    }
    
    // ===========================================================================================
    //                                    문자열 변환
    // ===========================================================================================
    
    /**
     * HTML,javaScript 값으로 escape한다.
     */
    public String toEscapedStr(Object key) {
        return StringEscapeUtils.escapeHtml(getStr(key));
        //return Encoders.escapeJavaScript(Encoders.escapeXml(str));
    }

    /**
     * 주민등록번호를 변환한다.
     */
    public String toSid(Object key) {
        return Formats.toSid(getStr(key));
    }
    
    /**
     * 전화번호로 변환한다.
     */
    public String toTel(Object key) {
        return Formats.toTel(getStr(key));
    }
    
    /**
     * yyyy-MM-dd형식으로 변환한다.
     */
    public String toDate(Object key) {
        return Formats.toDate(getStr(key));
    }
    
    /**
     * yyyy-MM-dd형식으로 변환한다.
     */
    public String toWon(Object key) {
        return Formats.toWon(getDecimal(key));
    }

    /**
     * 사업자등록번호를 변환한다.
     */
    public String toBid(Object key) {
        return Formats.toBid(getStr(key));
    }
    
    /**
     * 숫자를 ##,###원 형태로 리턴한다.
     * 오라클 number가 BigDecimal로 매핑됨으로 이것을 표준으로 한다.
     */
    public String toNumeric(Object key) {         
        Object value = super.get(key);
        if (value == null) return "";
        if(value instanceof BigDecimal){
            BigDecimal d = (BigDecimal)value;
            return Formats.INT.get(d);
        }if(value instanceof Integer){
            Integer d = (Integer)value;
            return Formats.INT.get(d);
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
}