package erwins.util.vender.hibernate;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;

import erwins.util.counter.Latch;
import erwins.util.lib.Strings;

/**
 * 일단 1뎁스의 초 간단버전만 지원 ㅠㅠ
 * and조건을 먼저 넣고 or조건을 넣어야 한다.
 * left join의 경우 fetch시 paging이 작동하지 않음으로 주의할것?  (테스트 못해봄)
 * N:1관계만 연관조인이 가능.
 */
public class HqlBuilderRoot implements HqlBuilder{
    
    private StringBuilder hql = new  StringBuilder();
    private StringBuilder count = new  StringBuilder();
    
    private Latch where = new Latch();
    private Latch orderBy = new Latch();
    
    private Latch open;
    private Latch sub;
    
    private List<Object> param = new ArrayList<Object>();
    
    private void add(String str){
        add(hql,str);
        add(count,str);
    }
    
    /** 한칸 자동 띄우기. */
    private static void add(StringBuilder builder,String str){
        builder.append(str);
        builder.append(" ");
    }
    
    // ===========================================================================================
    //                                    build
    // ===========================================================================================
    
    public HqlBuilder select(String str){
        add("select");
        add(hql,"distinct");
        add(hql,str);
        if(sub==null) add(count,"count( distinct "+str+")");
        else add(count,str);
        return this;
    }
    
    public HqlBuilder from(String str){
        add("from");
        add(str);
        return this;
    }
    
    public HqlBuilder join(String str,boolean fetch){
        add("inner join");
        if(fetch) add(hql,"fetch");
        add(str);
        return this;
    }
    
    public HqlBuilder join(String str){
        return join(str,false);
    }
    
    public HqlBuilder leftJoin(String str,boolean fetch){
        add("left join");
        add(str);
        if(fetch) add(hql,"fetch");
        return this;
    }
    
    public HqlBuilder leftJoin(String str){
        return leftJoin(str,false);
    }
    
    /** 특수한 경우이다. */
    public HqlBuilder openSubQuery(String con){
        sub = new Latch();
        add("where");
        add(con);
        add("in");
        add("(");
        return this;
    }
    
    public HqlBuilder closeSubQuery(){
        add(")");
        sub = null;
        return this;
    }
    
    public HqlBuilder open(){
        where("(");
        open = new Latch();
        return this;
    }
    
    public HqlBuilder close(){
        add(")");
        open = null;
        return this;
    }
    
    /** 보통 특이한 상황에서 사용함으로 문자열 통으로 받는다. 여기에 having도 같이 넣자. */
    public HqlBuilder groupBy(String str){
        add("group by");
        add(str);
        return this;
    }

    /** 거의 사용되지 않을듯. 디폴트로 모두 desc가 아니게 되버린다. */
    public HqlBuilder orderBy(String ... orderBy){
        for(String each : orderBy) orderBy(each,false);
        return this;
    }
    
    public HqlBuilder orderBy(String str,boolean desc){
        if(orderBy.next()) add(hql,"order by");
        else add(hql,","); 
        add(hql,str);
        if(desc) add(hql,"desc");
        return this;
    }
    
    //select b.bookName,b.grade from Book b order by b.grade desc, b.bookName 
    
    /**
     * 최초에만 where을 붙인다.
     */
    private void where(String field){
        if(where.next()) add("where");
        else{
            if(open==null || !open.next()){
                if(open==null) add("and");
                else add("or");
            }
        }
        add(field);
    }
    
    // ===========================================================================================
    //                                    조건 추가.
    // ===========================================================================================
    
    public HqlBuilder in(String field,Object[] obj){
        if(obj==null || obj.length==0) return this;
        where(field);
        add("in");
        add("(");
        add(Strings.iterateStr("?",",",obj.length));
        add(")");
        for(Object id : obj) param.add(id);
        return this;
    }
    
    public HqlBuilder isNull(String field){
        where(field);
        add("is");
        add("null");
        return this;
    }
    public HqlBuilder isNotNull(String field){
        where(field);
        add("is");
        add("not");
        add("null");
        return this;
    }
    
    public HqlBuilder eq(String field,Object obj){
        if(obj==null) return this;
        where(field);
        add("=");
        add("?");
        param.add(obj);
        return this;
    }
    
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
    
    public HqlBuilder like(String field,String obj){
        where(field);
        add("like");
        add("?");
        param.add("%"+obj+"%");
        return this;
    }
    
    public HqlBuilder iLike(String field,String obj){
        where("UPPER("+field+")");
        add("like");
        add("?");
        param.add("%"+obj.toUpperCase()+"%");
        return this;
    }
    
    /** 문자열의 날자 비교할때 등등. */
    public HqlBuilder between(String field, Object small,Object large){
    	if(small!=null) ge(field, small);
    	if(large!=null) le(field, large);
        return this;
    }
    
    // ===========================================================================================
    //                                    hibernate
    // ===========================================================================================    
    
    public String hqlStringForCount(){
        return getText(count);
    }
    public String hqlString(){
        return getText(hql);
    }
    
    /**
     * 기타 경우의 수를 삭제해준다. 
     */
    private String getText(StringBuilder builder){
        String text = builder.toString();
        text = text.replaceAll("\\s\\w+\\s\\(\\s*\\)","");
        return text;
    }
    
    public Query count(Session session){
        Query q = session.createQuery(getText(count));
        initParameter(q);
        return q;
    }
    
    public Query query(Session session){
        Query q = session.createQuery(getText(hql));
        initParameter(q);
        return q;
    }

    /**
     * 이 로직 때문에 할 수 없이 내부에서 Hibernate Query를 리턴한다.
     */
    private void initParameter(Query q) {
        for(int i=0;i<param.size();i++){
            Object obj = param.get(i);
            q.setParameter(i, obj);
        }
    }
}
