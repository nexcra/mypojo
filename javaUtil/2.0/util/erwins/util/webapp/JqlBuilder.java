package erwins.util.webapp;

import java.util.ArrayList;
import java.util.List;

import erwins.util.collections.map.SearchMap;
import erwins.util.counter.Latch;

/**
 * 일단 1뎁스의 간단버전만 지원
 */
public class JqlBuilder{
    
	private final SearchMap map;
	
	public JqlBuilder(SearchMap map){
		this.map = map;
	}
	
	private StringBuilder where = new  StringBuilder();
	private StringBuilder parameterInfo = new  StringBuilder();
    
    public String getWhere() {
		return where.toString();
	}
	public String getParameterInfo() {
		return parameterInfo.toString();
	}
	

	private Latch first = new Latch();
    private Latch openOr;
    
    private List<Object> param = new ArrayList<Object>();
    
    public Object[] getParam() {
		return param.toArray();
	}
	public void setParam(List<Object> param) {
		this.param = param;
	}
	public JqlBuilder open(){
    	where.append("(");
        openOr = new Latch();
        return this;
    }
    
    public JqlBuilder close(){
    	where.append(")");
        openOr = null;
        return this;
    }
    
    
    // ===========================================================================================
    //                                    build
    // ===========================================================================================
    public JqlBuilder eqString(String key){
    	if(map.isEmpty(key)) return this;
    	String value = map.getString(key);
    	append(key,"String");
    	param.add(value);
        return this;
    }
    public JqlBuilder eqInteger(String key){
    	if(map.isEmpty(key)) return this;
    	Integer value = map.getInteger(key);
    	append(key,"Integer");
    	param.add(value);
    	return this;
    }

	private void append(String key,String type) {
		if(!first.next()){
			if(openOr !=null && openOr.next()) where.append(" && "); 
			else where.append(" || ");
			parameterInfo.append(" , ");
		}
		where.append(key);
    	where.append(" == ");
    	where.append(key);
    	where.append("Param");
        
    	parameterInfo.append(type);
    	parameterInfo.append(" ");
    	parameterInfo.append(key);
    	parameterInfo.append("Param");
	}
    
    /*
    public HqlBuilder ge(String field,Object obj){
    	if(obj==null) return this;
    	where(field);
    	add(">=");
    	add("?");
    	param.add(obj);
    	return this;
    }
    
    public HqlBuilder le(String field,Object obj){
    	if(obj==null) return this;
    	where(field);
    	add("<=");
    	add("?");
    	param.add(obj);
    	return this;
    }
    
    public HqlBuilder ne(String field,Object obj){
        if(obj==null) return this;
        where(field);
        add("!=");
        add("?");
        param.add(obj);
        return this;
    }
    
    *//** 문자열의 날자 비교할때 등등. *//*
    public HqlBuilder between(String field, Object small,Object large){
    	if(small!=null) ge(field, small);
    	if(large!=null) le(field, large);
        return this;
    }*/
	
	
	/* ================================================================================== */
	/*                                    위임                                                */
	/* ================================================================================== */
	public boolean isPaging() {
		return map.isPaging();
	}
	public Integer getPageNo() {
		return map.getPageNo();
	}
	public int getSkipResults() {
		return map.getSkipResults();
	}
	public Integer getPagingSize() {
		return map.getPagingSize();
	}
}
