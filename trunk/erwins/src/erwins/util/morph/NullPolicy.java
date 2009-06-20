package erwins.util.morph;


import java.math.BigDecimal;

import javax.servlet.http.HttpServletRequest;

import erwins.util.lib.Strings;


/**
 * request 등의 자료 변환 타입을 정의한다. <br> 1. 자식객체의 경우 name으로 property.~~형식을 사용한다. <br> 2. 키값, Date를 제외한 컬렉션 포함 모든 객체는 null safe하다. <br> 키값은 주로 부모 객체에만 존재하며 하이버네이트의 save or update 구분을 위해 사용된다.
 * @author  erwins(my.pojo@gmail.com)
 */
public abstract class NullPolicy{
    
    // ===========================================================================================
    //                                    변환기
    // ===========================================================================================
    /**
     * 모든 String문자열은 이곳을 통과한다. 
     * 이곳에 조건을 추가한다.
     * 주의.. value는 전달되는 래퍼런스가 아니다. 불변객체이다. return을 받아야 한다.
     */
    private String toStr(String key,String value){
        if(key.indexOf("Date")>-1) value = Strings.getNumericStr(value); //달력일자 귀찮아서 하드코딩
        return value;
    }
    private Boolean toBoolean(String value){
        if(value==null) return null;
        if(value.equalsIgnoreCase("Y") || value.equals("1")|| value.equalsIgnoreCase("ON")) return true;
        else if(value.equalsIgnoreCase("N")|| value.equals("0")) return false;
        else return null;
    }
    private BigDecimal toDecimal(String value){
        return Strings.getDecimal(value);
    }
    public Integer toInteger(String value){
        if(value==null || value.equals("")) return 0;
        return Integer.parseInt(value);
    }
    public Long toLong(String value){
        if(value==null || value.equals("")) return 0L;
        return Long.parseLong(value);
    }
    
    // ===========================================================================================
    //                                    method
    // ===========================================================================================    
    
    protected HttpServletRequest request;
    
    ///** 추후 사용할것! */
    //private String preFix = null;
       
    private String CollectionName = null;
    
    /**
     * 자식 객체일 경우 collection 이름을 앞에 붙여준다.
     * @uml.property  name="collectionName"
     */
    public void setCollectionName(String parent) {
        this.CollectionName = parent;
    }
    
    /**
     * preFixse여부를 결정한다.
     */
    private String getKey(String key){
        return CollectionName!=null ? CollectionName + key : key;
    }
    
    private String getByReq(String key){
        return request.getParameter(getKey(key));
    }
    private String[] getsByReq(String key){
        return request.getParameterValues(getKey(key));
    }
    
    /**
     * 특이한 경우(DB의 키값)로 0 또는 공백문자시 null을 리턴한다.
     * null이 허용되지 않는 원시형은 사용될 수 없다.
     */
    public Integer getIntId(String str){
        String value = getByReq(str);
        if(value==null || value.equals("") || value.equals("0")) return null;
        return Integer.parseInt(value);
    }
    
    public Integer[] getIntIds(String str){
        String[] values = getsByReq(str);
        if(values==null) return new Integer[]{};
        Integer[] integers = new Integer[values.length];
        for(int i=0;i<values.length;i++)
            integers[i] = (values[i].equals("") || values[i].equals("0")) ? null : Integer.parseInt(values[i]);;
        return integers;
    }
        
    
    /**
     * 특이한 경우(DB의 키값)로 0 또는 공백문자시 null을 리턴한다.
     * null이 허용되지 않는 원시형은 사용될 수 없다.
     */
    public Long getLongId(String str){
        String value = getByReq(str);
        if(value==null || value.equals("") || value.equals("0")) return null;
        return Long.parseLong(value);
    }
    
    public Long[] getLongIds(String str){
        String[] values = getsByReq(str);
        if(values==null) return new Long[]{};
        Long[] longs = new Long[values.length];
        for(int i=0;i<values.length;i++)
            longs[i] = (values[i].equals("") || values[i].equals("0") ) ? null : Long.parseLong(values[i]);;
        return longs;
    }
    
    public Integer getInteger(String key){
        String value = getByReq(key);
        return toInteger(value);
    }

    public Integer[] getIntegers(String str){
        String[] values = getsByReq(str);
        if(values==null) return new Integer[]{};
        Integer[] Longs = new Integer[values.length];
        for(int i=0;i<values.length;i++) Longs[i] = toInteger(values[i]);
        return Longs;
    }
    
    public Long getLong(String key){        
        String value = getByReq(key);
        return toLong(value);
    }

    public Long[] getLongs(String str){
        String[] values = getsByReq(str);
        if(values==null) return new Long[]{};
        Long[] longs = new Long[values.length];
        for(int i=0;i<values.length;i++) longs[i] = toLong(values[i]);
        return longs;
    }

    public BigDecimal getDeciaml(String key){
        String value = getByReq(key);        
        return toDecimal(value);
    }
    public BigDecimal[] getDeciamls(String key){
        String[] values = getsByReq(key);
        if(values==null) return new BigDecimal[]{};
        BigDecimal[] decimals = new BigDecimal[values.length];
        for(int i=0;i<values.length;i++) decimals[i] = toDecimal(values[i]);
        return decimals;
    }
    
    
    /**
     * 특수 케이스.. 나중에 수정하자.
     * String 문자열을 boolean값으로 치환한다.
     * null이 입력되거나 해당하는 값이 없을 경우 null을 리턴한다. 이때는 디폴트를 사용하자.
     */    
    public Boolean getBoolean(String key){
        String value = getByReq(key);
        return toBoolean(value);
    }
    public Boolean[] getBooleans(String key){
        String[] values = getsByReq(key);
        if(values==null) return new Boolean[]{};
        Boolean[] strings = new Boolean[values.length];
        for(int i=0;i<values.length;i++) strings[i] = toBoolean(values[i]);
        return strings;
    }
    
    
    /**
     * 일반 String문자열을 추출한다.
     * 특이한 경우로 파라메터에 date가 들어가면 only 숫자로 변경한다. 
     */
    public String getStr(String key){
        String value = getByReq(key);
        if(value==null || value.equals("")) return "";
        return toStr(key,value);
    }
    
    /**
     * 일반 String문자열을 추출한다.
     * 특이한 경우로 파라메터에 date가 들어가면 only 숫자로 변경한다. 
     */
    public String[] getStrs(String key){
        String[] values = getsByReq(key);
        if(values==null) return new String[]{};
        String[] strings = new String[values.length];
        for(int i=0;i<values.length;i++) strings[i] = toStr(key,values[i]);
        return strings;
    }
    
    
}