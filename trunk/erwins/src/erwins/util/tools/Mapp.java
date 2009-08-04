package erwins.util.tools;

import java.math.BigDecimal;
import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONObject;
import erwins.util.lib.Maths;
import erwins.util.morph.JSMapper;
import erwins.util.openApi.Google;


/**
 * 통계 또는 기타 목적용 map ..  <br>
 * 비지니스 로직의 Entity로 절대 사용하지 말것!
 * Key와 Object를 유연하게 사용하기 위해서 특정 generic을 사용하지 않았다.
 * @author erwins
 */

public class Mapp extends MappRoot {
    
    public Mapp(){}
    
    @SuppressWarnings("unchecked")
    public Mapp(HttpServletRequest req){
        Enumeration<String> enumeration = req.getParameterNames();
        String name = null;
        String[] values = null;
        
        while (enumeration.hasMoreElements()) {
            name = enumeration.nextElement();            
            values = req.getParameterValues(name);  //null을 리턴하지는 않는다.
            if (values.length == 1) put(name, values[0]);
            else  put(name, values);
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
    //                                    기타 잡스킬
    // ===========================================================================================    

    public String googleChart(int width,int height){
        return Google.getChart(this,width,height);
    }
    public JSONObject json(){
        return JSMapper.instance().getObject(this);
    }
    
}