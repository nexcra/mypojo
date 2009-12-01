
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
    /** 내부적으로 쓰는게 아니라면 외부에서 직접 파라메터를 접근할때 사용된다. */
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
    
    /**
     * 그냥 꺼내쓰니깐 아마 String바께 안될듯.
     */
    public CriteriaBuilder eq(String key){
        if(!map.isEmpty(key)) add(Restrictions.eq(key,map.get(key)));
        return this;
    }
    
    public <T extends Enum<?>> CriteriaBuilder eq(String key,Class<T> clazz){
        if(!map.isEmpty(key)) add(Restrictions.eq(key, map.getEnum(clazz, key)));
        return this;
    }
    
    public Conjunction get() {
        return and;
    }
    
    
    
    
    

}