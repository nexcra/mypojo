package erwins.util.vender.hibernate;

import java.util.Calendar;

import org.hibernate.Query;
import org.hibernate.Session;

import erwins.util.collections.map.SearchMap;
import erwins.util.lib.DayUtil;
import erwins.util.lib.StringUtil;

/**
 * Map버전
 * 데코레이터 패턴을 사용한다.
 */
public class HqlBuilderMap implements HqlBuilder{
    
    private SearchMap map;
    private HqlBuilder builder;
    
    public HqlBuilderMap(SearchMap map) {
        this.map = map;
        this.builder = new HqlBuilderRoot();
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
    
    public HqlBuilderMap ge(String field,Object key){
    	if(map.isEmpty(key)) return this;
    	Object parameter = map.get(key);
    	builder.ge(field,parameter);
    	return this;
    }
    
    public HqlBuilderMap le(String field,Object key){
    	if(map.isEmpty(key)) return this;
    	Object parameter = map.get(key);
    	builder.le(field,parameter);
    	return this;
    }
    
    /** 복수형을 담는다!! 그리고 map에서 안가져온다~!! */
    public HqlBuilderMap in(String field,Object[] obj){
        builder.in(field, obj);
        return this;
    }
    
    /** 나중에 수정하기.. true / false로 */
    public HqlBuilderMap isNull(String field){
        builder.isNull(field);
        return this;
    }
    /** 나중에 수정하기.. true / false로 */
    public HqlBuilderMap isNotNull(String field){
    	builder.isNotNull(field);
    	return this; 
    }
    
    public HqlBuilderMap eq(String field){
        return eq(field,getExt(field));
    }
    public HqlBuilderMap ne(String field){
        return ne(field,getExt(field));
    }
    public HqlBuilderMap ge(String field){
    	return ge(field,getExt(field));
    }
    public HqlBuilderMap le(String field){
    	return le(field,getExt(field));
    }
    
    public HqlBuilderMap like(String field){
        return like(field,getExt(field));
    }
    public HqlBuilderMap like(String field,String key){
    	if(map.isEmpty(key)) return this;
    	builder.like(field, map.getString(key));
    	return this;
    }
    public HqlBuilderMap iLike(String field){
        return iLike(field,getExt(field));
    }    
    public HqlBuilderMap iLike(String field,String key){
        if(map.isEmpty(key)) return this;
        builder.iLike(field, map.getString(key));
        return this;
    }
    
    /** 자동으로 숫자형으로 바꾼다. 주의! */
    public HqlBuilderMap between(String field, Object small,Object large){
    	builder.between(field, map.getNumericString(small), map.getNumericString(large));
        return this;
    }
    
    /** between과 동일하나 Date형식으로 바꿔준다. 
     * 모든 조건은 =가 들어감으로 max에 +1을 해준다.  */
    public HqlBuilderMap betweenByDate(String field, Object small,Object large){
    	Calendar min = map.getCalendarBy8Char(small);
    	Calendar max = map.getCalendarBy8Char(large);
    	builder.between(field,min==null ? null : min.getTime()
    			, max==null ? null : DayUtil.addCalendar(max,1).getTime());
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
     * ex1) hql.eq("a.someType", SomeType.class); 라고 입력하면
     * ==> a.someType = :somType
     * ==> setParameter(map.getEnum('someType'));
     * ex2) "a.agent.id"  => "agent.id" 로 바꿔 준다
     */
    private String getExt(String str){
        return StringUtil.getFirstAfter(str, ".");
    }
    
    /** key뒤에 Min,Max를 붙여서 검색한다. 자동으로 숫자형으로 바꾼다. 주의! */
    public HqlBuilder between(String key){
    	String mapKey = StringUtil.getExtention2(key);
    	between(key,mapKey+"Min",mapKey+"Max");
    	return this;
    }
    
    /** between의 Date버전. DB의 컬럼이 Day나 String이 아니라 TimeStamp계열일때 사용한다. */
    public HqlBuilder betweenByDate(String key){
    	String mapKey = StringUtil.getExtention2(key);
    	betweenByDate(key,mapKey+"Min",mapKey+"Max");
    	return this;
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
    
    public String hqlStringForCount(){
        return builder.hqlStringForCount();
    }
    public String hqlString(){
        return builder.hqlString();
    }

    
    
}
