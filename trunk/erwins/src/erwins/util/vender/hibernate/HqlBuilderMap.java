package erwins.util.vender.hibernate;

import org.hibernate.Query;
import org.hibernate.Session;

import erwins.util.lib.Strings;
import erwins.util.tools.SearchMap;

/**
 * Map버전
 * 데코레이터 패턴을 사용한다.
 */
public class HqlBuilderMap implements HqlBuilder{
    
    private SearchMap map;
    private HqlBuilder builder;
    
    public HqlBuilderMap(SearchMap map,HqlBuilder builder) {
        this.map = map;
        this.builder = builder;
    }
    
    public HqlBuilderMap eq(String field,Object key){
        if(map.isEmpty(key)) return this;
        builder.eq(field, map.get(key));
        return this;
    }
    
    public HqlBuilderMap like(String field,String key){
        if(map.isEmpty(key)) return this;
        builder.like(field, map.getStr(key));
        return this;
    }
    public HqlBuilderMap iLike(String field,String key){
        if(map.isEmpty(key)) return this;
        builder.like(field, map.getStr(key));
        return this;
    }
    
    // ===========================================================================================
    //                                    추가된거
    // ===========================================================================================
    
    public HqlBuilderMap eqInt(String field,Object key){
        if(map.isEmpty(key)) return this;
        builder.eq(field, map.getInteger(key));
        return this;
    }
    public <T extends Enum<?>> HqlBuilderMap eq(String field,String key,Class<T> clazz){
        if(map.isEmpty(key)) return this;
        builder.eq(field, map.getEnum(clazz, key));
        return this;
    }
    public <T extends Enum<?>> HqlBuilderMap eq(String field,Class<T> clazz){
        return eq(field,getExt(field),clazz);
    }
    
    /**
     * hql.eq("a.someType", SomeType.class); 라고 입력하면
     * ==> a.someType = :somType
     * ==> setParameter(map.getEnum('someType'));
     */
    private String getExt(String str){
        String value = Strings.getExtention(str);
        if(value.equals("")) throw new RuntimeException(str+" has no extention");
        return value;
    }
    
    
    // ===========================================================================================
    //                                    delegate method
    // ===========================================================================================

    public Query count(Session session) {
        return builder.count(session);
    }

    public HqlBuilder from(String str) {
        return builder.from(str);
    }

    public HqlBuilder join(String str, boolean fetch) {
        return builder.join(str, fetch);
    }

    public HqlBuilder join(String str) {
        return builder.join(str);
    }

    public HqlBuilder leftJoin(String str, boolean fetch) {
        return builder.leftJoin(str, fetch);
    }

    public HqlBuilder leftJoin(String str) {
        return builder.leftJoin(str);
    }

    public HqlBuilder open() {
        return builder.open();
    }
    
    public HqlBuilder close() {
        return builder.close();
    }

    public Query query(Session session) {
        return builder.query(session);
    }

    public HqlBuilder select(String str) {
        return builder.select(str);
    }
    
    
    
}
