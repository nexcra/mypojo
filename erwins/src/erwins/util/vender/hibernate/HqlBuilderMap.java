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
        Object parameter = map.get(key);
        builder.eq(field,parameter);
        return this;
    }
    
    public HqlBuilderMap ne(String field,Object key){
        if(map.isEmpty(key)) return this;
        Object parameter = map.get(key);
        builder.ne(field,parameter);
        return this;
    }
    
    /** 복수형을 담는다!! 그리고 map에서 안가져온다~!! */
    public HqlBuilderMap in(String field,Object[] obj){
        builder.in(field, obj);
        return this;
    }
    
    /** 나중에 수정하기.. true / false로 */
    public HqlBuilderMap isNull(String field){
        return isNull(field);
    }
    /** 나중에 수정하기.. true / false로 */
    public HqlBuilderMap isNotNull(String field){
        return isNotNull(field);
    }
    
    public HqlBuilderMap eq(String field){
        return eq(field,getExt(field));
    }
    public HqlBuilderMap ne(String field){
        return ne(field,getExt(field));
    }
    
    public HqlBuilderMap like(String field,String key){
        if(map.isEmpty(key)) return this;
        builder.like(field, map.getStr(key));
        return this;
    }
    public HqlBuilderMap iLike(String field,String key){
        if(map.isEmpty(key)) return this;
        builder.iLike(field, map.getStr(key));
        return this;
    }
    
    /**
     *  map에서 조건을 가져와서 할당한다.
     *  key값이 true이면 역 정렬(DESC)이다.  
     * */
    public HqlBuilder orderBy(String ... orderBy){
        for(String each : orderBy){
            Boolean value = map.getBoolean(each);
            if(value==null) continue;
            builder.orderBy(each, value);
        }
        return this;
    }
    
    // ===========================================================================================
    //                                    추가된거
    // ===========================================================================================
    
    public HqlBuilderMap eqInt(String field,Object key){
        if(map.isEmpty(key)) return this;
        Object parameter = map.get(key);
        if(parameter instanceof Object[]){
            Object[] parameters = (Object[])parameter;
            for(Object each : parameters){
                builder.eq(field,Integer.parseInt(each.toString()));
            }
        }else{
            builder.eq(field,Integer.parseInt(parameter.toString()));
        }
        return this;
    }
    public <T extends Enum<?>> HqlBuilderMap eq(String field,String key,Class<T> clazz){
        if(map.isEmpty(key)) return this;
        builder.eq(field, map.getEnum(clazz, key));
        return this;
    }
    public <T extends Enum<?>> HqlBuilderMap eq(String field,Class<T> clazz){
        eq(field,getExt(field),clazz);
        return this;
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
        builder.from(str);
        return this;
    }

    public HqlBuilder join(String str, boolean fetch) {
        builder.join(str, fetch);
        return this;
    }

    public HqlBuilder join(String str) {
        builder.join(str);
        return this;
    }

    public HqlBuilder leftJoin(String str, boolean fetch) {
        builder.leftJoin(str, fetch);
        return this;
    }

    public HqlBuilder leftJoin(String str) {
        builder.leftJoin(str);
        return this;
    }

    public HqlBuilder open() {
        builder.open();
        return this;
    }
    
    public HqlBuilder close() {
        builder.close();
        return this;
    }

    public Query query(Session session) {
        return builder.query(session);
    }

    public HqlBuilder select(String str) {
        builder.select(str);
        return this;
    }

    public HqlBuilder closeSubQuery() {
        builder.closeSubQuery();
        return this;
    }

    public HqlBuilder groupBy(String groupby) {
        builder.groupBy(groupby);
        return this;
    }
    
    /** 내장객체를 그대로 호출해준다. */
    public HqlBuilder orderBy(String orderBy,boolean desc){
        builder.orderBy(orderBy, desc);
        return  this;
    }    

    public HqlBuilder openSubQuery(String id) {
        builder.openSubQuery(id);
        return this;
    }
    
    public String test1(){
        return builder.test1();
    }
    public String test2(){
        return builder.test2();
    }
    
    
    
}
