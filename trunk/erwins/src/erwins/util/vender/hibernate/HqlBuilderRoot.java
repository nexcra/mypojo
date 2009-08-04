package erwins.util.vender.hibernate;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;

import erwins.util.tools.Latch;

/**
 * 일단 1뎁스의 초 간단버전만 지원 ㅠㅠ
 * left join의 경우 fetch시 paging이 작동하지 않음으로 주의할것?  (테스트 못해봄)
 * N:1관계만 연관조인이 가능.
 */
public class HqlBuilderRoot implements HqlBuilder{
    
    private StringBuilder hql = new  StringBuilder();
    private StringBuilder count = new  StringBuilder();
    
    private Latch where = new Latch();
    
    private Latch open;
    
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
    
    /* (non-Javadoc)
     * @see erwins.util.vender.hibernate.HqlBuilderInterface#select(java.lang.String)
     */
    public HqlBuilder select(String str){
        add("select");
        add(hql,"distinct");
        add(hql,str);
        add(count,"count( distinct "+str+")");
        return this;
    }
    
    /* (non-Javadoc)
     * @see erwins.util.vender.hibernate.HqlBuilderInterface#from(java.lang.String)
     */
    public HqlBuilder from(String str){
        add("from");
        add(str);
        return this;
    }
    
    /* (non-Javadoc)
     * @see erwins.util.vender.hibernate.HqlBuilderInterface#join(java.lang.String, boolean)
     */
    public HqlBuilder join(String str,boolean fetch){
        add("inner join");
        add(str);
        if(fetch) add(hql,"fetch");
        return this;
    }
    
    /* (non-Javadoc)
     * @see erwins.util.vender.hibernate.HqlBuilderInterface#join(java.lang.String)
     */
    public HqlBuilder join(String str){
        return join(str,false);
    }
    
    /* (non-Javadoc)
     * @see erwins.util.vender.hibernate.HqlBuilderInterface#leftJoin(java.lang.String, boolean)
     */
    public HqlBuilder leftJoin(String str,boolean fetch){
        add("left join");
        add(str);
        if(fetch) add(hql,"fetch");
        return this;
    }
    /* (non-Javadoc)
     * @see erwins.util.vender.hibernate.HqlBuilderInterface#leftJoin(java.lang.String)
     */
    public HqlBuilder leftJoin(String str){
        return leftJoin(str,false);
    }
    
    /* (non-Javadoc)
     * @see erwins.util.vender.hibernate.HqlBuilderInterface#open()
     */
    public HqlBuilder open(){
        add("or");
        add("(");
        open = new Latch();
        return this;
    }
    
    public HqlBuilder close(){
        add(")");
        open = null;
        return this;
    }
    
    /**
     * 최초에만 where을 붙인다.
     */
    private void where(String field){
        if(where.isFirst()) add("where");
        else{
            if(open==null || !open.isFirst()){
                if(open==null) add("and");
                else add("or");    
            }
        }
        add(field);
    }
    
    // ===========================================================================================
    //                                    조건 추가.
    // ===========================================================================================
    
    /* (non-Javadoc)
     * @see erwins.util.vender.hibernate.HqlBuilderInterface#eq(java.lang.String, java.lang.Object)
     */
    public HqlBuilder eq(String field,Object obj){
        where(field);
        add("=");
        add("?");
        param.add(obj);
        return this;
    }
    
    /* (non-Javadoc)
     * @see erwins.util.vender.hibernate.HqlBuilderInterface#like(java.lang.String, java.lang.String)
     */
    public HqlBuilder like(String field,String obj){
        where(field);
        add("like");
        add("?");
        param.add("%"+obj+"%");
        return this;
    }
    /* (non-Javadoc)
     * @see erwins.util.vender.hibernate.HqlBuilderInterface#iLike(java.lang.String, java.lang.String)
     */
    public HqlBuilder iLike(String field,String obj){
        where("UPPER("+field+")");
        add("like");
        add("?");
        param.add("%"+obj.toUpperCase()+"%");
        return this;
    }
    
    // ===========================================================================================
    //                                    hibernate
    // ===========================================================================================    
    
    /**
     * 기타 경우의 수를 삭제해준다. 추후 정규식으로 바꾸자.
     */
    private String getText(StringBuilder builder){
        String text = builder.toString();
        return text.replaceAll("or \\( \\)","");
    }
    
    /* (non-Javadoc)
     * @see erwins.util.vender.hibernate.HqlBuilderInterface#count(org.hibernate.Session)
     */
    public Query count(Session session){
        Query q = session.createQuery(getText(count));
        initParameter(q);
        return q;
    }
    
    /* (non-Javadoc)
     * @see erwins.util.vender.hibernate.HqlBuilderInterface#query(org.hibernate.Session)
     */
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
