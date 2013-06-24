
package erwins.util.vender.hibernate;


/**
 * 조인 같은거 안됩니다 ㅋㅋ.  Hql쓰세요.
 * Criterion의 경우 조인하면 다가져 옴으로 페이징처리가 안된다.
 * Restrictions.like 이런거 처럼 사용하면 됨.
 * implements  Criterion
 */
@SuppressWarnings("serial")
public class CriterionMap {
    
    /*private SearchMap map;
    private Conjunction body = Restrictions.conjunction();
    private Disjunction or;
    
    public CriterionMap(SearchMap map) {
        this.map = map;
    }
    *//** 
     * 내부적으로 쓰는게 아니라면 외부에서 직접 파라메터를 접근할때 사용된다.
     * AND가 먼저 전부 기록된 후에 OR이 와야 한다. 
     * *//*
    public void add(Criterion s){
        if(or!=null) or.add(s);
        else body.add(s);
    }
    
    public CriterionMap or(){
        or = Restrictions.disjunction();
        body.add(or);
        return this;
    }
    
    public CriterionMap ilike(String key){
        if(!map.isEmpty(key)) add(Restrictions.ilike(key,map.getString(key),MatchMode.ANYWHERE));
        return this;
    }
    public CriterionMap ilike(String field,String key){
        if(!map.isEmpty(key)) add(Restrictions.ilike(field,map.getString(key),MatchMode.ANYWHERE));
        return this;
    }
    
    *//** 걍 닥치고 문자열임. *//*
    public CriterionMap eq(String key){
        if(!map.isEmpty(key)) add(Restrictions.eq(key,map.get(key)));
        return this;
    }
    
    *//** 문자열의 날자 비교할때 등등. *//*
    public CriterionMap between(String key, String small,String large){
    	if(!map.isEmpty(small)) add(Restrictions.ge(key,map.getNumericString(small)));
        if(!map.isEmpty(large)) add(Restrictions.le(key,map.getNumericString(large)));
        return this;
    }
    
    *//** key뒤에 Min,Max를 붙여서 검색한다. *//*
    public CriterionMap between(String key){
    	between(key,key+"Min",key+"Max");
    	return this;
    }
    
    public <T extends Enum<?>> CriterionMap eq(String key,Class<T> clazz){
        if(!map.isEmpty(key)) add(Restrictions.eq(key, map.getEnum(clazz, key)));
        return this;
    }
    
	public SearchMap getMap() {
		return map;
	}  
    
     ================================================================================== 
	                                    위임메소드                                                
	 ================================================================================== 
    
	
	public boolean equals(Object obj) {
		return body.equals(obj);
	}
	public TypedValue[] getTypedValues(Criteria arg0, CriteriaQuery arg1) throws HibernateException {
		return body.getTypedValues(arg0, arg1);
	}
	public int hashCode() {
		return body.hashCode();
	}
	public String toSqlString(Criteria crit, CriteriaQuery criteriaQuery) throws HibernateException {
		return body.toSqlString(crit, criteriaQuery);
	}
	public String toString() {
		return body.toString();
	}*/
    

}