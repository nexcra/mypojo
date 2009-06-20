package erwins.util.dom2;

import java.io.Serializable;
import java.util.*;

import javax.persistence.*;

import erwins.util.lib.Encoders;
import erwins.util.lib.Sets;
import erwins.util.root.StringIdEntity;



/**
 * 메뉴 또는 코드성의 트리구조를 가지는 객체를 표현한다. 이전 이후의 연결 관계를 스스로 가진다.  ID는 유니크 키이다. 오라클 connect by 에 종속적이다. 이를 대비해 컴포지트 패턴?을 사용하자... 오바인가? 0. Document객체와 기본적으로 동일하다. 반드시 1개의 root객체를 가진다. 1. 주요 속성 3가지(키,부모키,이름)를 가진다. 필수입력이다. 2. 이를 토대로  level, nextDom, beforeDom, parent를 구한다.
 * @author  erwins(my.pojo@gmail.com)
 */
@MappedSuperclass
public abstract class  Dom<T extends Dom<T>> implements Cloneable,StringIdEntity,Comparable<T>,Serializable{  //Comparable??

    private static final long serialVersionUID = 1L;
    
    //private Class clazz;
    
    //기본정보
    /**
     * @uml.property  name="id"
     */
    protected String id;
    /**
     * @uml.property  name="upperId"
     */
    protected String upperId;
    /**
     * @uml.property  name="name"
     */
    protected String name;
    /**
     * @uml.property  name="level"
     */
    protected int level;
    /**
     * @uml.property  name="sort"
     */
    protected Integer sort;
    /**
     * @uml.property  name="data"
     */
    protected String data;
    
    //연결관계 정의
    /**
     * @uml.property  name="nextDom"
     */
    protected T nextDom;
    /**
     * @uml.property  name="beforeDom"
     */
    protected T beforeDom;
    /**
     * @uml.property  name="parent"
     */
    protected T parent;
    
    public static void qwe(){
        
    }

    @Transient
    public boolean isFirst(){
        return beforeDom == null ? true : false; 
    }

    @Transient
    public boolean isEnd(){
        return nextDom == null ? true : false; 
    }

    @Transient
    public boolean isRoot(){
        return parent == null ? true : false; 
    }
    
    /**
     *  마지막 Dom이거나 다음 Dom의 레벨이 현재레벨보다 더 적은 경우
     */
    @Transient public boolean isTop(){
        return (this.isEnd() || this.nextDom.getLevel() < this.getLevel()   ) ?  true : false; 
    }
    
    /**
     *  연결노드가 아닌 자식이 없는 노드
     *  일반적 connectBy로 보여줄 경우 마지막(leaf)인 노드를 보여주게 된다.
     */
    @Transient public boolean isNoChild(){
        return (this.isEnd() || this.nextDom.getLevel() <= this.getLevel()   ) ?  true : false; 
    }  
    
    /**
     * 연결된 Root Dom을 구한다.
     **/
    @SuppressWarnings("unchecked")
    @Transient public T getRoot(){
        T dom = (T)this;
        //Dom<T> dom = this;
        while(true){
            if(dom.isRoot()) break;
            dom = dom.getParent();
        }
        return dom;
    }
    
    /**
     * 연결된 노드의 Root직전의 객체를 리턴한다.
     **/
    @SuppressWarnings("unchecked")
    @Transient public T getFirstChild(){
        T dom = (T)this;
        //Dom<T> dom = this;
        while(true){
            if(dom.getParent().isRoot()) break;
            dom = dom.getParent();
        }
        return dom;
    }
    
    // ===========================================================================================
    //                                static
    // ===========================================================================================
    
    
    /**
     * id값으로 Dom객체를 검색한다.
     * null일경우 피상속 객체가 예외를 던지도록 수정하자.
     **/
    public static <T extends Dom<T>> T getElementById(List<T> baseDoms,String id){
        for(T dom:baseDoms) if(dom.getId().equals(id)) return dom;
        return null;
    }
    
    /**
     * id값으로 Dom객체를 검색한다.
     * 빠른 검색을 위해 HashMap을 사용한다. 1000개 이상일 경우 사용하자~
     * null일경우 피상속 객체가 예외를 던지도록 수정하자.
     **/
    public static <T extends Dom<T>> T getElementById(Map<String,T> baseDoms,String id){
        return baseDoms.get(id);
    }
    
    /**
     * 하위 Dom을 검색후 배열로 리턴한다.
     * 하위Dom 없으면 크기가 0인 배열을 리턴한다. 
     * Dom이 null이면  전체 dom들 중에서 root인 것들을 구한다.
     * 성능에 문제가 있음으로 크기가 작을때만 사용하자.
     * Connect By의 경우 성능상 경우 자식맵을 설정해서 사용한다.
     **/
    public static <T extends Dom<T>> List<T> getChild(List<T> baseDoms, T dom){
        if(dom==null) return getRoots(baseDoms);
        List<T> child = new ArrayList<T>();
        for(T childDom:baseDoms) {
            if(childDom.getParent()==null) continue;
            if(childDom.getParent().equals(dom)) child.add(childDom);
        }
        return child;
    }
    
    /** connect By하지 않은 원시데이타에 사용. */
    public static <T extends Dom<T>> List<T> getChildNoConnect(List<T> baseDoms, T dom){
        //if(dom==null) return getRoots(baseDoms);
        List<T> child = new ArrayList<T>();
        for(T childDom:baseDoms) {
            if(childDom.getUpperId().equals(dom.getId())) child.add(childDom);
        }
        return child;
    }
    
    /**
     * 전체 dom들 중에서 root인 것들을 구한다.
     * 원래 Root가 1개만 존재해야 함으로 특수한 경우에 사용된다.
     **/
    public static <T extends Dom<T>> List<T> getRoots(List<T> baseDoms){
        List<T> roots = new ArrayList<T>();
        for(T childDom:baseDoms) if(childDom.isRoot()) roots.add(childDom);
        return roots;
    }
    
    // ===========================================================================================
    //                                      Sort
    // ===========================================================================================
    
    /**
     * 1이 100 보다 위에 온다.
     */
    public int compareTo(T o) {
        Integer d1 = getSort();
        Integer d2 = o.getSort();
        if(d1==null || d2==null) return 0;
        return d1 > d2 ? 1 : (d1 == d2 ? 0 : -1);
    }    
    
    /**
     * sort 값으로 소팅합니다. 
     */
    public static <T extends Dom<T>> void sort(List<T> doms){
        Collections.sort(doms, new ComparatorBySort<T>());
    }
    /**
     * id 값으로 소팅합니다. (느림)
     */
    public static <T extends Dom<T>> void sortById(List<T> doms){
        Collections.sort(doms, new ComparatorById<T>());
    }
    
    /**
     *  Sort를 위한 DomComparator
     *  1이 100 보다 위에 온다.
     */
    protected static class ComparatorBySort<T extends Dom<T>> implements Comparator<Dom<T>> { 
        public int compare(Dom<T> dom1, Dom<T> dom2) {
            Integer d1 = dom1.getSort();
            Integer d2 = dom2.getSort();
            if(d1==null || d2==null) return 0;            
            return d1 > d2 ? 1 : (d1 == d2 ? 0 : -1);
        }
    }
    /**
     *  Sort를 위한 DomComparator
     *  1이 100 보다 위에 온다.
     */
    protected  static class ComparatorById<T extends Dom<T>> implements Comparator<Dom<T>> { 
        public int compare(Dom<T> dom1, Dom<T> dom2) {
            Integer d1 = Integer.parseInt(dom1.getId());
            Integer d2 = Integer.parseInt(dom2.getId());
            if(d1==null || d2==null) return 0;
            return d1 > d2 ? 1 : (d1 == d2 ? 0 : -1);
        }
    }
    
    // ===========================================================================================
    //                                    Connect By
    // ===========================================================================================

    /**
     * 새로운 배열을 만들어 자식 순으로 정렬한다.
     * (connect by 의 order siblings)
     */
    public static <T extends Dom<T>> List<T> connectBy(List<T> baseDoms){
        return new Connector<T>(baseDoms).connectBy();
    }
    
    /**
     * 새로운 배열을 만들어 자식 순으로 정렬한다.
     * (connect by 의 order siblings)
     */
    public static <T extends Dom<T>> List<T> connectByForNoRoot(List<T> baseDoms){
        return new Connector<T>(baseDoms).connectByForNoRoot();
    }
    
    /**
     * 1. 부모를 할당한다.
     * 2. 레벨을 정의한다.
     * 3. 새로운 배열을 만들어 루프를 돌면서 자식 노드를 추가한다.
     * 4. 전 후 객체를 지정한다. 
     */
    private static class Connector<T extends Dom<T>>{
        
        private List<T> baseDoms;
        private List<T> newDoms;
        private Map<String,T> baseMap;
        private Map<String,List<T>> parentMap = new HashMap<String,List<T>>(); //부모ID가 키값이 된다.
        
        public Connector(List<T> baseDoms){
            this.baseDoms = baseDoms;
            init();
        }
        
        /**
         * 쩌는 성능을 위해 2개의 보조 Map을 추가했다. 
         */        
        private void init(){
            baseMap = Sets.getMap(baseDoms);
            for(T dom : baseDoms){
                List<T> list = parentMap.get(dom.getUpperId());
                if(list==null) list = new ArrayList<T>();
                list.add(dom);
                parentMap.put(dom.getUpperId(), list);
            }            
        }
        
        /**
         * 새로운 배열을 만들어 자식 순으로 정렬한다.
         * (connect by 의 order siblings)
         */
        public List<T> connectBy(){
            setParentDom(); //1
            T root = setLevelByParent(); //2
            newDoms = new ArrayList<T>();
            orderSiblings(root);
            setSideDomByOrder();
            return newDoms;
        }
        
        public List<T> connectByForNoRoot(){
            setParentDom(); //1
            setLevelByParent(); //2
            newDoms = new ArrayList<T>();
            
            List<T> children = getRoots(baseDoms);
            sort(children);
            for(T child : children) orderSiblings(child);
            
            setSideDomByOrder();
            return newDoms;
        }   
       
        
        /** parent를 구한다. */        
        private void setParentDom(){ //이거 public으로 쓸수 있게 변경
            for(T dom : baseDoms){
                if(dom.getName()==null) dom.setName(dom.getId());  //name이 없으면 id로 대체??????????????????????
                dom.setParent(getElementById(baseMap, dom.getUpperId()));
            }
        }
        
        /**
         * setParentDom이 설정되어 있어야 사용 가능하다.
         * 1. list를 순회하면서 level을 설정한다.
         * 2. root Dom을 찾아서 덤으로 리턴한다.  
         */
        private T setLevelByParent() {
            T root = null;
            for(T dom : baseDoms){
                if(dom.getParent()==null) root = dom;
                int deep = 1;    //1이 root이다.            
                T temp = dom;
                while(true){
                    if(temp.getParent()==null) break;
                    deep++;
                    temp = temp.getParent();
                }
                dom.setLevel(deep);
            }
            return root;
        }     
        
        /**
         * nextDom 및 beforeDom을 정의한다. 
         * 이는 dom의 isNoChild등을 사용할때 필요하다.
         * 없으면 null을 입력해 준다.
         */
        private void setSideDomByOrder(){
            for(int index=0;index<newDoms.size();index++){ //parent
                T dom = newDoms.get(index);
                if(index == 0) dom.setBeforeDom(null);
                else dom.setBeforeDom(newDoms.get(index-1));
                if(index == newDoms.size()-1) dom.setNextDom(null);
                else dom.setNextDom(newDoms.get(index+1));
            }
        }
        
        /**
         * Oracle orderSiblings을 흉내낸다. 재귀호출로 tree를 위한 order를 만들어 낸다.
         */
        private void orderSiblings(T parent){
            newDoms.add(parent);            
            List<T> children = parentMap.get(parent.getId());
            if(children==null) return;
            sort(children);
            for(T child : children) orderSiblings(child);
        }
    }   

    
    
    // ===========================================================================================
    //                               getter / setter     
    // ===========================================================================================
    


    /**
     * @return
     * @uml.property  name="id"
     */
    @Id
    public String getId() {
        return id;
    }

    /**
     * @param id
     * @uml.property  name="id"
     */
    public void setId(String id) {
        this.id = id;
    }
    
    @Transient public void setIdByInt(Integer id) {
        this.id = id.toString();
    }

    /**
     * @return
     * @uml.property  name="name"
     */
    public String getName() {
        return name;
    }

    /**
     * @param name
     * @uml.property  name="name"
     */
    public void setName(String name) {
        this.name = name;
    }    

    /**
     * @return
     * @uml.property  name="upperId"
     */
    public String getUpperId() {
        return upperId;
    }

    /**
     * @param idParent
     * @uml.property  name="upperId"
     */
    public void setUpperId(String idParent) {
        this.upperId = idParent;
    }

    /**
     * @return
     * @uml.property  name="sort"
     */
    public Integer getSort() {
        return sort;
    }

    /**
     * @param order
     * @uml.property  name="sort"
     */
    public void setSort(Integer order) {
        this.sort = order;
    }    

    /**
     * @return
     * @uml.property  name="parent"
     */
    @Transient public T getParent() {
        return parent;
    }

    /**
     * @param parent
     * @uml.property  name="parent"
     */
    public void setParent(T parent) {
        this.parent = parent;
    }

    /**
     * @return
     * @uml.property  name="level"
     */
    @Transient public int getLevel() {
        return level;
    }

    /**
     * @param level
     * @uml.property  name="level"
     */
    public void setLevel(int level) {
        this.level = level;
    }

    /**
     * @return
     * @uml.property  name="nextDom"
     */
    @Transient public T getNextDom() {
        return nextDom;
    }

    /**
     * @param nextDom
     * @uml.property  name="nextDom"
     */
    public void setNextDom(T nextDom) {
        this.nextDom = nextDom;
    }

    /**
     * @return
     * @uml.property  name="beforeDom"
     */
    @Transient public T getBeforeDom() {
        return beforeDom;
    }

    /**
     * @param beforeDom
     * @uml.property  name="beforeDom"
     */
    public void setBeforeDom(T beforeDom) {
        this.beforeDom = beforeDom;
    }

    /**
     * @return
     * @uml.property  name="data"
     */
    @Transient public String getData() {
        return data;
    }

    /**
     * @param href
     * @uml.property  name="data"
     */
    public void setData(String href) {
        this.data = href;
    }    
    

    @Override
    public String toString(){
        return getId();
    }
    

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        return result;
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        T other = (T) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        return true;
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public T clone(){
        try {
            return (T)super.clone();
        } catch (CloneNotSupportedException e) {
            Encoders.stackTrace(e);
            return null;
        }
    }
    
}