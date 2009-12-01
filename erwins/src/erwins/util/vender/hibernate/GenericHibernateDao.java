package erwins.util.vender.hibernate;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.util.*;

import org.hibernate.*;
import org.hibernate.criterion.*;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import erwins.util.lib.Sets;
import erwins.util.root.EntityInit;
import erwins.util.root.EntityUserValidator;
import erwins.util.tools.SearchMap;

/**
 * getOrder을 재정의 할것. <br> return 이 2개 이상일 경우 Object[]로 넘어온다. 주의!
 */
@SuppressWarnings("unchecked")
public abstract class GenericHibernateDao<Entity, ID extends Serializable> extends HibernateDaoSupport{

    private Class<Entity> persistentClass;

    
    public GenericHibernateDao() {
        this.persistentClass = (Class<Entity>)((ParameterizedType)getClass().getGenericSuperclass()).getActualTypeArguments()[0];
    }

    public Class<Entity> getPersistentClass() {
        return persistentClass;
    }
    
    /**
     * 데이터 입력 후 목록을 iBatis등을 사용하여 다시 쿼리할때 세션이 유지된다면 늦은 insert가 실행된다.
     * 이러한 경우 flush를 이용하여 쿼리를 강제로 실행 시켜야 한다. 커밋되지는 않는다.
     **/
    public void flush(){
        getSession().flush();
    }    
    
    /** Flush 이후 메모리(1차캐시?)에서 삭제한다. */
    public void flushAndClear(){
        getSession().flush();
        getSession().clear();
    }
    
    public Criteria getCriteria(){
        return getSession().createCriteria(getPersistentClass());
    }
    
    // ===========================================================================================
    //                                    save / delete
    // ===========================================================================================
    
    /**
     * DefaultEntity의 하위노드라면 초기값을 세팅해 준다.
     * ID가 null이 아니면 update라고 판단하고 수정 가능 여부를 판별한다.
     * 업데이트 방식중 명시적으로 makePersistent를 호출해서 전부 교체하는 방식에만 사용된다.
     */
    public Entity makePersistent(Entity client) {
        if(client instanceof EntityInit){
            EntityInit defaultEntity = (EntityInit)client;
            defaultEntity.initValue();
        }
        if(client instanceof EntityUserValidator){ //성능 때문에 코드 증가.
            EntityUserValidator<ID> castedClient = (EntityUserValidator)client;
            if(castedClient.getId()!=null){
                EntityUserValidator server = (EntityUserValidator)findById(castedClient.getId());
                server.validateUser();
                getSession().evict(server);  //중복객체 오류난다. 명시적으로 제거해주자.
            }
        }
        getSession().saveOrUpdate(client);
        return client;
    }
    
    /**
     *  소유주의 벨리데이션 체크를 검사한다.
     */
    private void validate(Entity entity) {
        if(entity instanceof EntityUserValidator){
            EntityUserValidator<ID> server = (EntityUserValidator)entity;
            server.validateUser();
        }
    }

    public void makeTransient(Entity entity) {
        validate(entity);
        getSession().delete(entity);
    }

    /**
     * 를 거치는 삭제메소드. 
     */
    public void makeTransient(ID id) {
        Entity entity = findById(id,false);
        validate(entity);
        getSession().delete(entity);
    }  
    
    // ===========================================================================================
    //                                      find
    // ===========================================================================================

    /**
     * 페이징 등에서 사용될 전체 카운트 수
     * 복잡한 검색은 iBatis를 사용하고 아니라면 이것을 사용한다.
     **/
    public int count(Criterion... criterion) {
        Criteria crit = getSession().createCriteria(getPersistentClass()).setProjection(
                Projections.projectionList().add(Projections.rowCount())
                );
        if(criterion != null) for (Criterion c : criterion) crit.add(c); //null이면 무시
        return Sets.getResultInt(crit.list());
    }
    
    /** 주어진 조건에 해당하는 자료가 1건 이상 있는지? */
    public boolean isExist(Criterion... criterion) {
        int count = count(criterion);
        if(count > 0) return true;
        return false;
    }
    
    /** 주어진 조건에 해당하는 자료가 1건 이상 있는지? */
    public boolean isExist(Collection<Criterion> col) {
        return isExist(col.toArray(new Criterion[col.size()]));
    }
    
    /** HQL버전이다. 반드시 1개의 숫자를 리턴해야 한다. */
    public boolean isExist(String hql,Object ... parameters) {
        Query query = super.getSession().createQuery(hql);
        for(int i=0;i<parameters.length;i++){
            query.setParameter(i, parameters[i]);    
        }
        Integer count = Sets.getResultCount(query.list()).intValue();
        return count > 0 ? true : false; 
    }
    
    /**
     * 수정을 원할경우 lazy로 얻어오자.??
     * 일반 select일 경우 lock을 얻을 필요가 없다.
     */
    public Entity findById(ID id, boolean lock) {
        Entity entity;
        if (lock) entity = (Entity) getSession().load(getPersistentClass(), id, LockMode.UPGRADE);
        else entity = (Entity) getSession().get(getPersistentClass(), id);
        return entity;
    }
    
    /**
     * id에 해당하는 객체가 없을경우 예외를 던진다.
     */
    public Entity findById(ID id) {
        Entity T =  findById(id,false);
        if(T==null) throw new RuntimeException(persistentClass.getSimpleName()+ " is not found (pk is "+id+"). you need debugging");
        return T;
    }
    

    public List<Entity> findAll(){
        return findBy();
    }
    
    /**
     * getOrder을 재정의 할것.
     */
    public Entity findUnique(Criterion... criterion) {
        return Sets.getResultUnique(findBy(criterion));
    }
    
    /**
     * 간단한 검색용 . getOrder을 재정의 할것.
     */
    public List<Entity> findBy(Criterion... criterion) {
        Criteria crit = getSession().createCriteria(getPersistentClass());
        for (Criterion c : criterion)  crit.add(c);
        for (Order c : getDefaultOrder()) crit.addOrder(c);
        crit.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);  //Set등의 배열이 있을 경우~
        return crit.list();
    }
    
    /** 조건이 있는 간단한 검샏용 */
    public List<Entity> findBy(Collection<Criterion> col) {
        return findBy(col.toArray(new Criterion[col.size()]));
    }
    
    /**
     * 프로시저 호출
     **/
    @Deprecated
    public void callSP() {        
        //"{ call PG_DBR_CONNECT.SP_RECEIPT () }"
    }
    
    // ===========================================================================================
    //                                    응용
    // ===========================================================================================

    /** totalCount기능 없음. 단순 페이징 처리만 됨. */
    public List<Entity> findBy(SearchMap map,Criterion ... criterion) {
        Criteria criteria = getCriteria();
        for (Order each : getDefaultOrder()) criteria.addOrder(each);
        for (Criterion c : criterion)  criteria.add(c);
        if(map.isPaging()){
            criteria.setFirstResult(map.getSkipResults());
            criteria.setMaxResults(map.getPagingSize());            
        }
        return criteria.list();
   }
    
    
    /**
     * HQL / Native SQL 구현시 페이징 적용 
     */
    protected void query(SearchMap map,Query query,String countSql){
        if(map.isPaging()){
            if(countSql!=null){
                Query c =  getSession().createSQLQuery(countSql);
                Long count = (Long)c.uniqueResult();
                map.setTotalCount(count.intValue());
            }
            query.setFirstResult(map.getSkipResults());
            query.setMaxResults(map.getPagingSize());            
        }
        map.setResult(query.list());
    }
    
    /**
     *  HqlBuilder를 이용한 페이징 처리기.
     */
    protected void query(SearchMap map,HqlBuilder hql) {
        Query query = hql.query(getSession());
        if(map.isPaging()){
            Long count = (Long)hql.count(getSession()).uniqueResult();
            map.setTotalCount(count.intValue());
            query.setFirstResult(map.getSkipResults());
            query.setMaxResults(map.getPagingSize());            
        }
        map.setResult(query.list());
    }
    
    /** map없이 사용할때. */
    protected List<Entity> query(HqlBuilder hql) {
        Query query = hql.query(getSession());
        return query.list();
    }
    
    /** map없이 사용할때. */
    protected Entity queryUnique(HqlBuilder hql) {
        Query query = hql.query(getSession());
        return (Entity)query.uniqueResult();
    }
    
    /** 대량의 데이터를 배치처리 or 2차캐싱 할때 사용하자. id값만을 가져온다. */
    public Iterator<Entity> iterator(HqlBuilder hql) {
        Query query = hql.query(getSession());
        query.iterate();
        return query.iterate();
    }
    
    /**
     * 간단한 SQL 적용 
     */
    public void querySql(SearchMap map,String sql,String count) {
        Query query =  getSession().createSQLQuery(sql);
        query(map,query,count);
    }
    
    protected void query(SearchMap map,CriteriaBuilder c) {
        Criteria criteria = getCriteria();
        for (Order each : getDefaultOrder()) criteria.addOrder(each);
        criteria.add(c.get());
        if(map.isPaging()){
            map.setTotalCount(count(c.get()));
            criteria.setFirstResult(map.getSkipResults());
            criteria.setMaxResults(map.getPagingSize());            
        }
        map.setResult(criteria.list());
    }
    
    /**
     * 추후 where조건으로 totalCount가 되도록 수정하기. 
     */
    public void findBy(SearchMap map,String hql) {
        Query query = super.getSession().createQuery(hql);
        if(map.isPaging()){
            //map.setTotalCount(findCount(crits));
            query.setFirstResult(map.getSkipResults());
            query.setMaxResults(map.getPagingSize());            
        }
        map.setResult(query.list());
    }
    
    
    // ===========================================================================================
    //                                        서브클래스에서 구현하시오.
    // ===========================================================================================

    /**
     * 오버라이드 해서 사용하시오. 
     * 안쓰는데도 있으니 abstract는 안달았음.
     */
    protected Order[] getDefaultOrder(){
        return new Order[0];
    }
    
    ////getSession().createSQLQuery("insert into t_code (id,name,sort,upper_id,is_use) values (999,'asd',1,'zzz',1)").executeUpdate();
    
    /* 버전 낮으면 안되는듯.
    public int updateByBid(Company company) {
        Query query = getSession().createQuery("update Custom a set a.companyId = :companyId where a.cuOwnerBid = :bid and a.companyId = :org ");
        query.setLong("companyId", company.getCompanyId());
        query.setLong("org", 0L);
        query.setString("bid", company.getBid());
        return query.executeUpdate();
    }*/    
    
    /*        String sql = "select * from e_board where 1=1 ";
    String where = "";        
        
    SQLQuery query = getSession().createSQLQuery(sql + where);
    
    
    query.setLong("companyId", company.getCompanyId());
    query.setLong("org", 0L);
    query.setString("bid", company.getBid());
    query.setResultTransformer(new AliasToEntityMapResultTransformer());
    
    //String sql2 = "select count(*) from e_board";
    //SQLQuery query2 = getSession().createSQLQuery(sql2);    
    //map.setTotalCount(Sets.getResultInt(query2.list())) ;
    
    if(map.isPaging()){
        map.setTotalCount(findCount(map.getCastedJunction(getPersistentClass())));
        query.setFirstResult(map.getSkipResults());
        query.setMaxResults(map.getPagingSize());
    }
    //crit.setResultTransformer(new AliasToEntityMapResultTransformer());
    map.setResult(query.list());*/    
    
    
    //Conjunction and = Restrictions.conjunction();
    
}
