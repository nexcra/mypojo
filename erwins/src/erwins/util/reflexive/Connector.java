package erwins.util.reflexive;

import java.io.Serializable;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import erwins.util.exception.HeapNotFoundException;
import erwins.util.morph.HtmlOptionBuilder;
import erwins.util.root.Pair;


/**
 * 트리 구조를 형성하는 Connector이다.
 * 이 인스턴스는 시스템 기동시 단 한번만 초기화 된다.  자료 변경시 지우고 새로 만들것!
 * Connectable은 불변객체라 가정하면 스래드 안전하다.
 * map을 리턴할때는 unmidifiable하게 던져주자.
 * 1. 부모를 먼저 지정해 주자.
 * 2. 자식을 지정해 주자.
 * 3. 자식을 소팅해 주자.
 * @author erwins(my.pojo@gmail.com)
 */
public class Connector<ID extends Serializable,T extends Connectable<ID,T>> {
    
    /** 디비에서 막 가져와 랜덤하게 저장된 map */
    private final Map<ID,T> map = new ConcurrentHashMap<ID, T>();
    
    public void put(T obj){
        map.put(obj.getId(),obj);
    }
    
    /** check일 경우 값이 없으면 예외를 던진다. */
    public T $(ID id,boolean check){
        T obj = map.get(id);
        if(check && obj==null) throw new HeapNotFoundException(id + " is not found by heap");
        return obj;    
    }
    
    public T $(ID id){
        return $(id,false);
    }
    
    public List<T> $$(ID id){
        return $(id).getChildren();
    }
    
    public T getRoot(T me) {
        T code = me;
        T parent = code.getParent(); 
        while(parent!=null){
            code = parent;
            parent = code.getParent();
        }
        return code;
    }
    
    // ===========================================================================================
    //                                    connect 관련
    // ===========================================================================================
    
    private List<T> roots;
    
    /** 
     * DB로드시 최초 1회만 해준다. 캐싱때문에 특이하게 된 경우.
     * DB에서 읽을때 sort는 필요 없다. 
     *  */ 
    public void setParentsForFirstLoad(List<T> list){
        for(T each : list){
            put(each);
        }
        for(T each : map.values()){
            T proxy = each.getParent();
            if(proxy==null) continue;
            ID parentId = proxy.getId();
            T parent = map.get(parentId);
            each.setParent(parent);
        }
    }
    
    /**
     * 이 메소드만이 map이 설정되어 있지 않아도 사용 가능하다. (for Enum)
     */
    public void setChildren(T ... values){
        for(T each : values) put(each);
        setChildren();
        Collections.sort(roots); //map이라 순서가 없음으로 다시 소팅 해준다.
    }
    
    /** parent가 설정되어 있을때 child를 추가해준다. */
    public void setChildren(){
        roots = new ArrayList<T>();
        for(T each : map.values()){
            T parent = each.getParent();
            if(parent==null) roots.add(each);
            else  parent.addChildren(each);
        }
    }
    
    public void orderSiblings(){
        for(T each :roots ) orderSiblings(each);
    }
    
    /**
     * Oracle orderSiblings을 흉내낸다. 재귀호출로 tree를 위한 order를 만들어 낸다.
     */
    private void orderSiblings(T parent){
        List<T> children = parent.getChildren();
        Collections.sort(children);
        for(T each : children) orderSiblings(each);
    }

    public List<T> getRoots() {
        return Collections.unmodifiableList(roots);
    }
    
    // ===========================================================================================
    //                                   html생성 관련
    // ===========================================================================================
    
    @SuppressWarnings("unchecked")
    public String option(ID parentId,boolean all){
        return HtmlOptionBuilder.option((Collection<Pair>)$$(parentId), all);
    }
    
    /**
     * 그룹 옵션을 정의한다. 그룹옵션은 계층형 임으로 여기에서만 정의된다.
     */
    public String groupOption(ID ... parentId) {
        Collection<T> all = new ArrayList<T>();
        for(int i=0;i<parentId.length;i++){
            ID id = parentId[i];
            T parent = $(id);
            all.add(parent);
        }
        return HtmlOptionBuilder.groupOption(all);
    }
    
    @SuppressWarnings("unchecked")
    public String radio(ID parentId,String entityName) {
        try {
            return HtmlOptionBuilder.radio((List<Pair>)$$(parentId), entityName);
        }
        catch (Exception e) {
            return e.getMessage();
        }
    }
    
    @SuppressWarnings("unchecked")
    public String checkBox(ID parentId,String entityName) {
        return HtmlOptionBuilder.checkBox((List<Pair>)$$(parentId), entityName);
    }
    
    
}

