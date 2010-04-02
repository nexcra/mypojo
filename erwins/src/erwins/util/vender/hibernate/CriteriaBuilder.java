
package erwins.util.vender.hibernate;

import org.hibernate.criterion.*;

import erwins.util.tools.SearchMap;

/**
 * 조인 같은거 안됩니다 ㅋㅋ.  Hql쓰세요.
 */
public class CriteriaBuilder {
    
    private SearchMap map;
    private Conjunction and = Restrictions.conjunction();
    private Disjunction or;
    
    public CriteriaBuilder(SearchMap map) {
        this.map = map;
    }
    /** 
     * 내부적으로 쓰는게 아니라면 외부에서 직접 파라메터를 접근할때 사용된다.
     * AND가 먼저 전부 기록된 후에 OR이 와야 한다. 
     * */
    public void add(Criterion s){
        if(or!=null) or.add(s);
        else and.add(s);
    }
    
    public CriteriaBuilder or(){
        or = Restrictions.disjunction();
        and.add(or);
        return this;
    }
    
    public CriteriaBuilder ilike(String key){
        if(!map.isEmpty(key)) add(Restrictions.ilike(key,map.getStr(key),MatchMode.ANYWHERE));
        return this;
    }
    public CriteriaBuilder ilike(String field,String key){
        if(!map.isEmpty(key)) add(Restrictions.ilike(field,map.getStr(key),MatchMode.ANYWHERE));
        return this;
    }
    
    /** 걍 닥치고 문자열임. */
    public CriteriaBuilder eq(String key){
        if(!map.isEmpty(key)) add(Restrictions.eq(key,map.get(key)));
        return this;
    }
    
    /** 문자열의 날자 비교할때 등등. */
    public CriteriaBuilder between(String key, String small,String large){
    	if(!map.isEmpty(small)) add(Restrictions.ge(key,map.getNumericStr(small)));
        if(!map.isEmpty(large)) add(Restrictions.le(key,map.getNumericStr(large)));
        return this;
    }
    
    /** key뒤에 Min,Max를 붙여서 검색한다. */
    public CriteriaBuilder between(String key){
    	between(key,key+"Min",key+"Max");
    	return this;
    }
    
    public <T extends Enum<?>> CriteriaBuilder eq(String key,Class<T> clazz){
        if(!map.isEmpty(key)) add(Restrictions.eq(key, map.getEnum(clazz, key)));
        return this;
    }
    
    public Conjunction get() {
        return and;
    }
    
    public SearchMap getMap() {
    	return map;
    }

}