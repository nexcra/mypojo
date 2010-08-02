package erwins.util.tools;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSON;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import erwins.util.lib.Clazz;
import erwins.util.lib.Maths;
import erwins.util.lib.RegEx;
import erwins.util.lib.Strings;
import erwins.util.morph.JDissolver;
import erwins.util.openApi.Google;
import erwins.util.vender.apache.Log;
import erwins.util.vender.apache.LogFactory;


/**
 * 통계 또는 기타 목적용 map ..  <br>
 * 비지니스 로직의 Entity로 절대 사용하지 말것!
 * Key와 Object를 유연하게 사용하기 위해서 특정 generic을 사용하지 않았다.
 * @author erwins
 */

public class Mapp extends MappRoot {
    
    protected Log log = LogFactory.instance(this.getClass());
    
    public Mapp(){}
    
    /** putAll(req.getParameterMap()) 을 쓰지 않는다. -> 이놈은 전부 배열로 들어감. */
    @SuppressWarnings("unchecked")
    public Mapp(HttpServletRequest req){
    	
        Enumeration<String> enumeration = req.getParameterNames();
        
        while (enumeration.hasMoreElements()) {
            String name = enumeration.nextElement();            
            String[] values = req.getParameterValues(name);  //null을 리턴하지는 않는다.
            if (values.length == 1) put(name, values[0]);
            else  put(name, values);
        }
        
        //이하는 간이 테스트 로직
        if(log.isDebugEnabled()){
            Enumeration<String> parameterNames =  req.getParameterNames();
            List<String> empty = new ArrayList<String>();
            JSONObject parameter = new JSONObject();            
            JSONObject parameters = new JSONObject();
            
            while(parameterNames.hasMoreElements()){
                String name = parameterNames.nextElement();
                String[] values = req.getParameterValues(name);
                if(values.length==0) continue;                
                if(values.length==1){
                    if(values[0].equals("")) empty.add(name);                       
                    else parameter.put(name, values[0]);                     
                }else{
                    JSONArray array = new JSONArray();
                    for(int i=0;i<values.length;i++) array.add(i,values[i]);
                    parameters.put(name, array);
                }
            }
            if(empty.size()!=0) log.debug("[HTML] Named.. But Empty : " + Strings.joinTemp(empty,","));
            if(!parameter.isEmpty()) log.debug("[HTML] Single Parameter : " + parameter);
            if(!parameters.isEmpty()) log.debug("[HTML] Array Parameters : " + parameters);
        }

    }
    
    // ===========================================================================================
    //                             비교        
    // ===========================================================================================    
    
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
    
    
    // ===========================================================================================
    //                             입력        
    // ===========================================================================================    
    
    public <T,O> void putAll(T[] keys,O[] values){
        if(keys.length != values.length) throw new IllegalArgumentException("args size must be same!");
        for(int i=0;i<keys.length;i++){
            put(keys[i],values[i]);
        }
    }
    
    /** key에 해당하는 값이 비었으면 입력 */
    public void putIfEmpty(Object key,Object value){
        if(isEmpty(key)) put(key,value);
    }
    
    /** key에 해당하는 값이 null이면 입력 */
    public void putIfNull(Object key,Object value){
        if(isNull(key)) put(key,value);
    }
    
    /** value에 해당하는 값이 null이 아닐때만 입력 */
    public void putIfValueNotNull(Object key,Object value){
        if(value==null) return;
        put(key,value);
    }
    /** value에 해당하는 값이 비어있지 않을때만 입력 */
    public void putIfValueNotEmpty(Object key,Object value){
        if(Clazz.isEmpty(value)) return;
        put(key,value);
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
     * 주로 최대값을 얻을 때 사용된다.
     */
    public void putMaxInteger(Object key,Integer value){
        Integer integer = (Integer)get(key);
        if(integer == null || integer < value) put(key, value);
    }
    /**
     * put명령 사용시 기존 수와 비교하여 더 큰수만 입력
     * 주로 최대값을 얻을 때 사용된다. 
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
    
    /** 기존 값이 있으면 문자열을 계속 더해나간다. */
    public void appendString(Object key,String value){
    	Object obj = get(key);
    	if(obj==null) put(key, value); 
    	else if(obj instanceof String){
        	String exist = (String)obj;
        	exist += value;
        	put(key,exist);
        }else throw new RuntimeException("existing value must be String. but " + obj.getClass().getName());
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
    
    /**
     *  filterKey와 매치되는 key의 값을 해당 RegEx로 replace한다.
     **/
    public void replace(String filterKey,RegEx regEx,String replaced){
        filterKey = filterKey.toLowerCase();
        for(Map.Entry<Object,Object> entry : this.entrySet()){
            String key = entry.getKey().toString().toLowerCase();
            if(!Strings.contains(key, filterKey)) continue;
            Object obj = entry.getValue();
            if(!(obj instanceof String)) continue;
            String returned =  regEx.replace(obj.toString(), replaced);
            put(entry.getKey(),returned);
        }
    }
    
    // ===========================================================================================
    //                                    소팅
    // ===========================================================================================    
    
    public static final Comparator<Entry<Object,Object>> ASC = new Comparator<Entry<Object,Object>>() {
		@SuppressWarnings("unchecked")
		@Override
		public int compare(Entry<Object,Object> o1, Entry<Object,Object> o2) {
			return ((Comparable)o1.getValue()).compareTo(o2.getValue());
		}
	};
	public static final Comparator<Entry<Object,Object>> DESC = new Comparator<Entry<Object,Object>>() {
		@SuppressWarnings("unchecked")
		@Override
		public int compare(Entry<Object,Object> o1, Entry<Object,Object> o2) {
			return ((Comparable)o2.getValue()).compareTo(o1.getValue());
		}
	};
    
    /** value를 기준으로 정렬된 List를 리턴한다.  */
    public List<Entry<Object,Object>> sortByValue(Comparator<Entry<Object,Object>> comparator) {
    	List<Entry<Object,Object>> list = new ArrayList<Entry<Object,Object>>();
		for(Entry<Object,Object> each : this.entrySet()) list.add(each);
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

    // ===========================================================================================
    //                                    기타 잡스킬
    // ===========================================================================================    

    public String googleChart(int width,int height){
        return Google.getChart(this,width,height);
    }
    public JSON json(){
        return JDissolver.instance().build(this);
    }
    
}