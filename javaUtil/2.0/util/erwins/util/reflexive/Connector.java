package erwins.util.reflexive;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.google.common.collect.Lists;

import erwins.util.root.Pair;
import erwins.util.root.TreeObject;
import erwins.util.vender.apache.HtmlOptionBuilder;


/**
 * 트리 구조를 형성하는 Connector이다.
 * 이 인스턴스는 시스템 기동시 단 한번만 초기화 된다.  자료 변경시 지우고 새로 만들것!
 * Connectable가 불변객체라면 스래드 안전하다.
 * map을 리턴할때는 unmidifiable하게 던져주자.
 * 1. 부모를 먼저 지정해 주자.  DB등에 당연히 부모키가 존재할것. Enum이라면 connector.setChildren(Menu.values()) 등
 * 2. 자식을 지정해 주자. connector.setChildren();
 * 3. 자식을 소팅해 주자. connector.orderSiblings();
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
        if(check && obj==null) throw new RuntimeException(id + " is not found from heap");
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
    
    /** 특정 레벨의 item을 모두 가져온다. 1이 ROOT이다. 당연히 2 이상 입력해야지? 
     * 사용시 장담 못함. 소형 데이터에만 사용할것. */
    public List<T> getListAsLevel(int level) {
        List<T> result = new ArrayList<T>();
        grapAsLevel(result,roots,level,1);
        return result;
    }
    
    private void grapAsLevel(List<T> result,List<T> childs,int max,int now) {
    	if(max<=now) for(T each : childs) result.add(each);
    	else{
    		now++;
    		for(T each : childs) grapAsLevel(result,each.getChildren(),max,now);
    	}
    }
    
    // ===========================================================================================
    //                                    connect 관련
    // ===========================================================================================
    
    private List<T> roots;
    
    /** 최초 로드시 초기화 한다. 사용은 보통
     * 1.DB캐싱 (Hibernate : setParentsForFirstLoad)
     * 2.DB캐싱 (iBatis : setParentsForTreeObject)
     * 3.enum (setChildren)
     *  */
	private void initData(List<T> list) {
		for(T each : list) put(each);
	}
    
    /** 
     * Hibernate 전용 메소드. parent에 프록시가 들어감으로 진짜 객체와 교체해 준다.
     * 쓸일 없을듯.. service 내에서 호출해주면 알아서 캐싱해 줄 뿐만 아니라
     * EHCach등을 사용하면 아에 필요 없다.
     *  */ 
    public void setParentsForFirstLoad(List<T> list){
        initData(list);
        for(T each : map.values()){
            T proxy = each.getParent();
            if(proxy==null) continue;
            ID parentId = proxy.getId();
            T parent = map.get(parentId);
            each.setParent(parent);
        }
    }
    
    /** iBatis등에서 parent가 직접 매핑되지 않을때 강제로 Parent를 map에서 찾아서 등록해 준다. */
    @SuppressWarnings({ "unchecked", "rawtypes" })
	public void setParentsForTreeObject(List<T> list){
        initData(list);
        for(T each : map.values()){
        	TreeObject<ID> tree = (TreeObject) each;
        	ID parentId = tree.getParentId();
        	if(parentId==null) continue;
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
    }
    
    /** parent가 설정되어 있을때 child를 추가해준다. */
    public void setChildren(){
        roots = new ArrayList<T>();
        for(T each : map.values()){
            T parent = each.getParent();
            if(parent==null) roots.add(each);
            else  parent.addChildren(each);
        }
        Collections.sort(roots); //map이라 순서가 없음으로 다시 소팅 해준다.
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
    
    /** 최상위 부모가 앞에 오도록 리스트를 반환한다. 무한루프 주의 */
    public List<T> getAllParent(T obj) {
    	List<T> list = Lists.newLinkedList();
    	addAllParent(list,obj);
        return list;
    }
    
    private void addAllParent(List<T> list,T obj) {
    	T parent = obj.getParent();
    	if(parent==null) return ;
    	list.add(0, parent);
    	addAllParent(list,parent);
    }
    
    // ===========================================================================================
    //                                   별도기능
    // ===========================================================================================
    /** current는 최종노드이다. current의 상위로 올라가면서 target과 돵일한지 비교한다.  */
    public boolean isContain(T current,T target) {
		boolean on = false;
    	T t = current;
    	while(t!=null){
    		if(target == t){
    			on = true;
    			break;
    		}
    		t = t.getParent();
    	}
		return on;
	}

    public int getLevel(T target) {
		int i=1;
    	T t = target;
    	while(t.getParent()!=null){
    		t = t.getParent();
    		i++;
    	}
		return i;
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

