package erwins.util.tools;

import java.math.BigDecimal;
import java.util.*;

import net.sf.json.JSONObject;

import org.apache.commons.lang.StringEscapeUtils;

import erwins.util.lib.*;
import erwins.util.morph.JSONs;
import erwins.util.openApi.Google;


/**
 * 통계 또는 기타 목적용 map ..  <br>
 * 비지니스 로직의 Entity로 절대 사용하지 말것!
 * Key와 Object를 유연하게 사용하기 위해서 특정 generic을 사용하지 않았다.
 * @author erwins
 */

public class Mapp extends HashMap<Object,Object>  {
    
    private static final long serialVersionUID = 1L;
    
    public Mapp(){}
    
    // ===========================================================================================
    //                             검사        
    // ===========================================================================================
    
    /**
     * key가 비어있는지 검사. 
     */
    public boolean isEmpty(Object key){
        Object value = get(key);
        if(value==null || value.toString().equals("")) return true;
        else return false;
    }
    
    /**
     * key가 null인지 검사. null과 ""은 다르다! 
     */
    public boolean isNull(Object key){
        Object value = get(key);
        if(value==null) return true;
        else return false;
    }
    
    /**
     * 값의 배열여부를 반환한다.
     */
    public boolean isArrayValue(Object key) {
        Object value = get(key);
        return value instanceof Object[];
    }
    
    // ===========================================================================================
    //                             입력        
    // ===========================================================================================    
    
    /** 비었으면 입력 */
    public void putIfEmpty(Object key,Object value){
        if(isEmpty(key)) put(key,value);
    }
    
    /** null이면 입력 */
    public void putIfNull(Object key,Object value){
        if(isNull(key)) put(key,value);
    }
    
    /**
     * 산식을 연산하여 map에 입력한다.
     * ex) map.put("summ", Maths.DIVIDE, B,C); 
     */
    public BigDecimal put(Object key,Maths math,BigDecimal ... decimal){
        BigDecimal value = getDecimal(key);
        value = math.run(value,decimal);
        put(key,value);
        return value;
    }    
    /**
     * put명령 사용시 기존 수와 비교하여 더 큰수만 입력 
     */
    public void putMaxInteger(Object key,Integer value){
        Integer integer = (Integer)get(key);
        if(integer == null || integer < value) put(key, value);
    }
    /**
     * put명령 사용시 기존 수와 비교하여 더 큰수만 입력 
     */
    public void putMaxDecimal(Object key,BigDecimal value){
        BigDecimal decimal = (BigDecimal)get(key);
        if(decimal == null || decimal.compareTo(value) < -1) put(key, value);
    }
    
    /**
     * put명령 사용시 +1 
     * 디폴트로 Integer를 사용함. 기존값이 BigDecimal일 경우 BigDecimal을 더함.
     */
    public void plus(Object key){
        Object obj = get(key);
        if(obj==null || obj instanceof Integer){
            plus(key,1);
        }else if(obj instanceof BigDecimal){
            plus(key,BigDecimal.ONE);
        }else{
            throw new RuntimeException(obj + " only numerical type allowed here!");
        }
    }
    
    /**
     * put명령 사용시 기존 수를 더하여 입력 
     * 간단한 연산에 사용
     */
    public void plus(Object key,Integer value){
        Integer integer = (Integer)get(key);
        if(integer == null) put(key, value);
        else put(key, integer + value);
    }
    
    /**
     * put명령 사용시 기존 수를 더하여 입력 
     * 간단한 연산에 사용
     */
    public void plus(Object key,BigDecimal value){
        put(key, getDecimal(key).add(value));
    }


    // ===========================================================================================
    //                             반환        
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
     * Integer 값을 반환한다.
     */
    public Integer getInteger(Object key) {
        return (Integer) super.get(key);
    }
    
    /**
     * Enum 값을 반환한다. 표준 작성만 가능하다. (Constructor가 있어야 한다.)
     */
    public <T extends Enum<?>> T getEnum(Class<T> clazz,String key){
        return Clazz.getEnum(clazz, getStr(key));
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
            else return new BigDecimal(obj.toString());
        }
    }
    
    /**
     * null이면 null을 리턴한다.
     */
    public Long getLongId(Object key) {
        Object obj =  super.get(key);
        if(obj==null) return null;
        if(obj instanceof Number) return ((Number) obj).longValue();
        else {
            String str = obj.toString();
            if(obj.toString().equals("")) return null;
            else return Long.parseLong(str);
        }
    }
    /**
     * null이면 null을 리턴한다.
     */
    public Integer getIntId(Object key) {
        Object obj =  super.get(key);
        if(obj==null) return null;
        if(obj instanceof Number) return ((Number) obj).intValue();
        else {
            String str = obj.toString();
            if(obj.toString().equals("")) return null;
            else return Integer.parseInt(str);
        }
    }
    
    public boolean isLargeThan(Object key,BigDecimal B) {
        return Maths.isLarge(getDecimal(key), B,false);
    }    
    public boolean isLargeThan(Object key,int B) {
        return Maths.isLarge(getDecimal(key), new BigDecimal(B),false);
    }
    public boolean isLargeThan(Object key,BigDecimal B,Boolean isSame) {
        return Maths.isLarge(getDecimal(key), B,isSame);
    }    
    public boolean isLargeThan(Object key,int B,Boolean isSame) {
        return Maths.isLarge(getDecimal(key), new BigDecimal(B),isSame);
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
        if(Maths.isZeroAny(value,sum)) return BigDecimal.ZERO;
        else return value.divide(sum,8,BigDecimal.ROUND_HALF_UP); //미검증!!
    }
    
    /**
     * 정렬된 Iterator를 리턴한다. 귀찮아서.. 
     */
    public Iterator<Object> getSortedItorator() {
        SortedMap<Object,Object> sorted = new TreeMap<Object,Object>();
        sorted.putAll(this);
        return sorted.values().iterator();
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
        }else{
            return value.toString();
        }
    }

    public String toPercent(Object key) {         
        Object value = super.get(key);
        if (value == null) return "";
        if(value instanceof BigDecimal){
            BigDecimal d = (BigDecimal)value;
            return Formats.PERCENT.get(d);
        }if(value instanceof Integer){
            Integer d = (Integer)value;
            return Formats.PERCENT.get(d);
        }else{
            return value.toString();
        }
    } 
    
    // ===========================================================================================
    //                                    기타 잡스킬
    // ===========================================================================================    

    public String googleChart(int width,int height){
        return Google.getChart(this,width,height);
    }
    public JSONObject json(){
        return JSONs.get(this);
    }
    
}