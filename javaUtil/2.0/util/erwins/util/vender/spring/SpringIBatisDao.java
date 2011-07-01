package erwins.util.vender.spring;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.orm.ibatis.support.SqlMapClientDaoSupport;

import erwins.util.collections.map.SearchMap;
import erwins.util.counter.Accumulator;
import erwins.util.counter.Counter;

/**
 * iBatis용 dao
 * 전체 재작성 할것!
 **/
@Deprecated
public class SpringIBatisDao extends SqlMapClientDaoSupport{
    
    private static Map<String,Integer> THRESHOLD_CACHE = new HashMap<String,Integer>();
    private static Map<String,Integer> NORMAL_CACHE = new HashMap<String,Integer>();
    
    private static Counter cu = new Accumulator(500);
    
    /** 캐시 임계치 10만건 */
    private static final int THRESHOLD = 100000;
    
    /**
     * Optimize라면 10만건 이상의 자료는 의미 없음으로 페이징 카운트를 캐싱한다.
     * 1. 대량 캐시는 일자별로 바뀌게 만들자.
     * 2. 소량 캐시는.. 흠. 고민일세.
     */
    private void getPagingCount(SearchMap searchMap,String sqlName){
        Integer count = null;
        if(searchMap.isOptimize()){
            count =  THRESHOLD_CACHE.get( searchMap.hashCode(sqlName));
            if(count==null || !cu.next()){
                count = (Integer)getSqlMapClientTemplate().queryForObject(sqlName+".Count",searchMap);
                if(count>THRESHOLD)  THRESHOLD_CACHE.put(searchMap.hashCode(sqlName), count);
            }else searchMap.setOptimized(true);
        }else{
            count = (Integer)getSqlMapClientTemplate().queryForObject(sqlName+".Count",searchMap);
        }
        searchMap.setTotalCount(count);
    }
    
    /**
     * 페이지넘버가 1이라면 디폴트로 캐싱하며 Optimize라면 1이라도 캐싱하지 않는다.
     * 최초 페이지 로드의 페이지 넘버는 1이라고 가정한다.
     * 추후 시간/일자 등을 이용해 캐싱을 초기화 하자.
     */
    @SuppressWarnings("unused")
    private void getPagingCount2(SearchMap searchMap,String sqlName){
        Integer count = null;
        count =  NORMAL_CACHE.get( searchMap.hashCode(sqlName));
        if(count==null || (searchMap.getPageNo()==1 && !searchMap.isOptimize()) ){
            count = (Integer)getSqlMapClientTemplate().queryForObject(sqlName+".Count",searchMap);
            NORMAL_CACHE.put(searchMap.hashCode(sqlName), count);
        }else{
            searchMap.setOptimized(true);
        }        
        searchMap.setTotalCount(count);
    }
    
    /**
     * 가장 강한 캐싱정책
     * 페이지넘버가 1이 아니라면 무조건 캐싱하고
     * 페이지넘버가 1이라면 Optimize라면 캐싱한다.
     * 추후 시간/일자 등을 이용해 캐싱을 초기화 하자.
     * 아직 미구현~~
     */
    @SuppressWarnings("unused")
    private void getPagingCount3(SearchMap searchMap,String sqlName){
        Integer count = null;
        count =  NORMAL_CACHE.get( searchMap.hashCode(sqlName));
        if(count==null || (searchMap.getPageNo()==1 && !searchMap.isOptimize()) ){
            count = (Integer)getSqlMapClientTemplate().queryForObject(sqlName+".Count",searchMap);
            NORMAL_CACHE.put(searchMap.hashCode(sqlName), count);
        }else{
            searchMap.setOptimized(true);
        }        
        searchMap.setTotalCount(count);
    }

    /**
     * 경고! iBatis는 페이징을 지원하지 않는다. 페이징 처럼 보여지는것 뿐이다.rownum을 직접 사용할것! 
     * return type이  Integer인 SQL을 중복 정의해야 한다.
     * sqlName에는 .Count를 붙이도록 하자.
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void queryForList(SearchMap searchMap,String sqlName){
        if(searchMap.isPaging()){
            Integer count = (Integer)getSqlMapClientTemplate().queryForObject(sqlName+".Count",searchMap);
            searchMap.setTotalCount(count);        
            List<Map> list =  getSqlMapClientTemplate()
            .queryForList(sqlName, searchMap, searchMap.getSkipResults(), searchMap.getPagingSize());
            searchMap.setResult(list);
        }else{
            List<Map> list =  getSqlMapClientTemplate().queryForList(sqlName, searchMap);
            searchMap.setResult(list);
        }
    }
    
    /**
     * rownum을 이용한 오라클용 페이징 처리에 사용한다.
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void queryForOraclePaging(SearchMap searchMap,String sqlName){
        if(!searchMap.isPaging()) throw new RuntimeException("this method required paging");
        searchMap.put("MIN",searchMap.getSkipResults()+1);
        searchMap.put("MAX",searchMap.getSkipResults() + searchMap.getPagingSize());
        //Integer count = (Integer)getSqlMapClientTemplate().queryForObject(sqlName+".Count",searchMap);
        //searchMap.setTotalCount(count);
        getPagingCount(searchMap,sqlName);
        List<Map> list =  getSqlMapClientTemplate().queryForList(sqlName, searchMap);
        searchMap.setResult(list);
    }
    
    /**
     * id와 Map을 이용해 SQL을 호출한다.  map이 없으면 null을 입력.
     * SP호출일 경우 : <procedure id="Dbrain.sendPayment" parameterMap="payment-params">
     */
    public Object queryForObject(Map<Object,Object> map,String sqlName){
        return getSqlMapClientTemplate().queryForObject(sqlName, map);
    }
    public Integer queryForInteger(Map<Object,Object> map,String sqlName){
        return (Integer)getSqlMapClientTemplate().queryForObject(sqlName, map);
    }
    
    /** update */
    public int update(Map<Object,Object> map,String sqlName){
        return getSqlMapClientTemplate().update(sqlName, map);
    }
    
    /** update. required와 일치하지 않으면 RuntimeException */
    public void update(Map<Object,Object> map,String sqlName,int required){
        int result = update(map,sqlName);
        if(result!=required) throw new RuntimeException(required+" update required. But!! "+result+" updated.");
    }
    
    /** delete */
    public int delete(Map<Object,Object> map,String sqlName){
        return getSqlMapClientTemplate().delete(sqlName, map);
    }
    
    /** delete. required와 일치하지 않으면 RuntimeException */
    public void delete(Map<Object,Object> map,String sqlName,int required){
        int result = delete(map,sqlName);
        if(result!=required) throw new RuntimeException(required+" delete required. But!! "+result+" deleted.");
    }
    
}
