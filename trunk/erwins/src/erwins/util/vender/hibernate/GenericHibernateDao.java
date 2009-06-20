package erwins.util.vender.hibernate;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.LockMode;
import org.hibernate.criterion.*;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import erwins.util.lib.Sets;
import erwins.util.root.DefaultEntity;
import erwins.util.root.UpdateAbleEntity;
import erwins.util.tools.SearchMap;

/**
 * getOrder을 재정의 할것. <br> return 이 2개 이상일 경우 Object[]로 넘어온다. 주의!
 */
public abstract class GenericHibernateDao<Entity, ID extends Serializable> extends HibernateDaoSupport implements GenericDao<Entity, ID>{

    /**
     * @uml.property  name="persistentClass"
     */
    private Class<Entity> persistentClass;

    @SuppressWarnings("unchecked")
    public GenericHibernateDao() {
        this.persistentClass = (Class<Entity>)((ParameterizedType)getClass().getGenericSuperclass()).getActualTypeArguments()[0];
    }

    /**
     * @return
     * @uml.property  name="persistentClass"
     */
    public Class<Entity> getPersistentClass() {
        return persistentClass;
    }
    
    /**
     * 데이터 입력 후 목록을 iBatis등을 사용하여 다시 쿼리할때 세션이 유지된다면 늦은 insert가 실행된다.
     * 이러한 경우 flush를 이용하여 쿼리를 강제로 실행 시켜야 한다. 커밋되지는 않는다.
     **/
    public void flush(){
        getSession().flush();
        //getSession().clear(); //메모리에서 삭제한다.
    }    
    
    /**
     * 주어진 조건에 해당하는 자료가 1건 이상 있는지?
     **/
    public boolean isExist(Criterion... criterion) {
        int count = findCount(criterion);
        if(count > 0) return true;
        else return false;
    }  
    
    /**
     * DefaultEntity의 하위노드라면 초기값을 세팅해 준다.
     * ID가 null이 아니면 update라고 판단하고 수정 가능 여부를 판별한다.
     */
    @SuppressWarnings("unchecked")
    public Entity makePersistent(Entity client) {
        if(client instanceof DefaultEntity){
            DefaultEntity<ID> de = (DefaultEntity<ID>)client;
            if(client instanceof UpdateAbleEntity && de.getId()!=null){
                UpdateAbleEntity<Entity,ID> server = (UpdateAbleEntity<Entity,ID>)findById(de.getId());
                server.validate();
                server.makeDefaultValue();
                server.update(client);
                getSession().saveOrUpdate(server);
                return (Entity)server;
            }
            de.makeDefaultValue();
        }
        getSession().saveOrUpdate(client);
        return client;
    }
    

    public void makeTransient(Entity entity) {
        getSession().delete(entity);
    }
    /**
     * 소유주의 벨리데이션 체크를 거치는 삭제메소드. 
     */
    @SuppressWarnings("unchecked")
    public void makeTransient(ID id) {
        Entity entity = findById(id,false);
        if(entity instanceof UpdateAbleEntity){
            UpdateAbleEntity<Entity,ID> server = (UpdateAbleEntity<Entity,ID>)entity;
            server.validate();
        }
        getSession().delete(entity);
    }  
    
    // ===========================================================================================
    //                                      find
    // ===========================================================================================

    /**
     * 수정을 원할경우 lazy로 얻어오자.??
     * 일반 select일 경우 lock을 얻을 필요가 없다.
     */
    @SuppressWarnings("unchecked")
    public Entity findById(ID id, boolean lock) {
        Entity entity;
        if (lock) entity = (Entity) getSession().load(getPersistentClass(), id, LockMode.UPGRADE);
        else entity = (Entity) getSession().get(getPersistentClass(), id);
        return entity;
    }
    
    /**
     * 일반 select일 경우 lock을 얻을 필요가 없다.
     */
    public Entity findById(ID id) {
        return findById(id,false);
    }

    public List<Entity> findAll(){
        return findBy();
    }
    
    /**
     * 페이징 등에서 사용될 전체 카운트 수
     * 복잡한 검색은 iBatis를 사용하고 아니라면 이것을 사용한다.
     **/
    @SuppressWarnings("unchecked")
    public int findCount(Criterion... criterion) {
        Criteria crit = getSession().createCriteria(getPersistentClass()).setProjection(
                Projections.projectionList().add(Projections.rowCount())
                );
        if(criterion != null) for (Criterion c : criterion) crit.add(c); //null이면 무시
        return Sets.getResultInt(crit.list());
    }
    
    /**
     * iBatis에서 id들을 읽어와서 하이버네이트로 객체 검색
     **/
    @SuppressWarnings("unchecked")
    public List<Entity> findByIds(List<Integer> idList) {
        if(idList.size()==0) return new ArrayList();
        return getSession().createCriteria(getPersistentClass()).setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
        .addOrder(Order.desc("id")).add(Restrictions.in("id", idList)).list();
    }   
        
    /**
     * getOrder을 재정의 할것.
     */
    @SuppressWarnings("unchecked")
    public List<Entity> findBy(Criterion... criterion) {
        Criteria crit = getSession().createCriteria(getPersistentClass());
        for (Criterion c : criterion)  crit.add(c);
        for (Order c : getDefaultOrder()) crit.addOrder(c);
        return crit.list();
   }
    
    /**
     * getOrder을 재정의 할것.
     */
    public Entity findUnique(Criterion... criterion) {
        return Sets.getResultUnique(findBy(criterion));
    }
    
    /**
     * 일반 select일 경우 lock을 얻을 필요가 없다.
     * id에 해당하는 객체가 없을경우 예외를 던진다.
     */
    public Entity findUnique(ID id){
        Entity T =  findById(id,false);
        if(T==null) throw new RuntimeException(
                persistentClass.getSimpleName()+ " is not found (pk is "+id+"). you need debugging");
        return T;
    }

    /**
     * SearchMap을 통해 paging과 select를 동시에 작업한다.
     * 추후 필요하면 count를 캐싱하자. iBatis놈이랑 같이 사용할것~
     */
    public void findBy(SearchMap map) {
        Criteria crit = getSession().createCriteria(getPersistentClass());
        Criterion crits = map.getCastedJunction(getPersistentClass());        
        crit.add(crits);
        for (Order c : getDefaultOrder()) crit.addOrder(c);
        if(map.isPaging()){
            map.setTotalCount(findCount(crits));
            crit.setFirstResult(map.getSkipResults());
            crit.setMaxResults(map.getPagingSize());            
        }
        //crit.setResultTransformer(new AliasToEntityMapResultTransformer());
        map.setResult(crit.list());
    }
    
    @SuppressWarnings("unchecked")
    public List<Entity> findByExample(Entity exampleInstance,String[] excludeProperty) {
        Criteria crit = getSession().createCriteria(getPersistentClass());
        Example example = Example.create(exampleInstance).excludeZeroes(); //excludeZeroes 요거 추가
        
        if(excludeProperty != null) for(String exclude:excludeProperty) example.excludeProperty(exclude); //0을 null로 간주 추가
        
        crit.add(example);
        crit.addOrder(Order.desc("id"));
        return crit.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list();
    }
    
    /**
     * 제외조건에 null을 입력함 , 필수 구현 항목임.
     **/
    public List<Entity> findByExample(Entity exampleInstance) {        
        return findByExample(exampleInstance,null);
    }
    
    /**
     * 프로시저 호출
     **/
    @Deprecated
    public void callSP() {        
        //"{ call PG_DBR_CONNECT.SP_RECEIPT () }"
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
