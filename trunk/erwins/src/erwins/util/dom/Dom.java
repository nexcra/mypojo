package erwins.util.dom;

import java.util.*;

import erwins.util.lib.Encoders;
import erwins.util.lib.Sets;
import erwins.util.root.StringIdEntity;


/**
 * 메뉴 또는 코드성의 트리구조를 가지는 객체를 표현한다. 이전 이후의 연결 관계를 스스로 가진다.  ID는 유니크 키이다. 오라클 connect by 에 종속적이다. 이를 대비해 컴포지트 패턴?을 사용하자... 오바인가? 0. Document객체와 기본적으로 동일하다. 반드시 1개의 root객체를 가진다. 1. 주요 속성 3가지(키,부모키,이름)를 가진다. 필수입력이다. 2. 이를 토대로  level, nextDom, beforeDom, parent를 구한다.
 * @author  erwins(my.pojo@gmail.com)
 */
public class Dom implements Cloneable,StringIdEntity{
    
    //기본정보
    /**
     * @uml.property  name="id"
     */
    protected String id;
    /**
     * @uml.property  name="idParent"
     */
    protected String idParent;
    /**
     * @uml.property  name="name"
     */
    protected String name;
    /**
     * @uml.property  name="description"
     */
    protected String description;
    /**
     * @uml.property  name="level"
     */
    protected int level;
    /**
     * @uml.property  name="href"
     */
    protected String href;
    /**
     * @uml.property  name="param1"
     */
    protected String param1;  //이거 data1 일케 바꾸기
    /**
     * @uml.property  name="param2"
     */
    protected String param2;
    /**
     * @uml.property  name="sort"
     */
    protected Integer sort;
    
    
    //연결관계 정의
    /**
     * @uml.property  name="nextDom"
     * @uml.associationEnd  
     */
    protected Dom nextDom;
    /**
     * @uml.property  name="beforeDom"
     * @uml.associationEnd  
     */
    protected Dom beforeDom;
    /**
     * @uml.property  name="parent"
     * @uml.associationEnd  
     */
    protected Dom parent;
    
    public boolean isFirst(){
        return beforeDom == null ? true : false; 
    }
    public boolean isEnd(){
        return nextDom == null ? true : false; 
    }
    public boolean isRoot(){
        return parent == null ? true : false; 
    }
    
    /**
     *  마지막 Dom이거나 다음 Dom의 레벨이 현재레벨보다 더 적은 경우
     */
    public boolean isTop(){
        return (this.isEnd() || this.nextDom.getLevel() < this.getLevel()   ) ?  true : false; 
    }
    
    /**
     *  연결노드가 아닌 자식이 없는 노드
     *  일반적 connectBy로 보여줄 경우 마지막(leaf)인 노드를 보여주게 된다.
     */
    public boolean isNoChild(){
        return (this.isEnd() || this.nextDom.getLevel() <= this.getLevel()   ) ?  true : false; 
    }    
    
    // ===========================================================================================
    //                                static
    // ===========================================================================================
    
    /**
     * id값으로 Dom객체를 검색한다.
     **/
    public static Dom getElementById(List<Dom> baseDoms,String id){
        for(Dom dom:baseDoms) if(dom.getId().equalsIgnoreCase(id)) return dom;
        return null;
    }
    
     /**
     * 상위 Dom을 검색한다.
     *  null을 리턴한다면 상위Dom이 없는 ROOT이다. 
     **/
    private static Dom getParent(List<Dom> baseDoms, Dom dom){
        for(Dom parentDom:baseDoms) if(parentDom.getId().equals(dom.getIdParent())) return parentDom;
        return null;
    }
    private static Dom getParent(Map<String,Dom> baseMap, Dom dom){
        return baseMap.get(dom.getIdParent());
    }
    
    /**
     * 하위 Dom을 검색후 배열로 리턴한다.
     *  하위Dom 없으면 크기가 0인 배열을 리턴한다. 
     *  Dom이 null이면  전체 dom들 중에서 root인 것들을 구한다.
     *  성능에 문제가 있음으로 크기가 작을때만 사용하자.
     **/
    public static List<Dom> getChild(List<Dom> baseDoms, Dom dom){
        if(dom==null) return getRoots(baseDoms);
        List<Dom> child = new ArrayList<Dom>();
        for(Dom childDom:baseDoms) {
            if(childDom.getParent()==null) continue;
            if(childDom.getParent().equals(dom)) child.add(childDom);
        }
        return child;
    }

    
    /**
     * 연결된 Root Dom을 구한다.
     **/
    public Dom getRoot(){
        Dom dom = this;
        while(true){
            if(dom.isRoot()) break;
            dom = dom.getParent();
        }
        return dom;
    }
    
    /**
     * 전체 dom들 중에서 root인 것들을 구한다.
     **/
    public static List<Dom> getRoots(List<Dom> baseDoms){
        List<Dom> roots = new ArrayList<Dom>();
        for(Dom childDom:baseDoms) if(childDom.isRoot()) roots.add(childDom);
        return roots;
    }
    
    /**
     * parent를 구한다.
     */
    @Deprecated
    public static void setParentDom(List<Dom> baseDoms){
        for(Dom dom : baseDoms){
            if(dom.getName()==null) dom.setName(dom.getId());  //name이 없으면 id로 대체
            dom.setParent(getParent(baseDoms,dom));
        }
        setLevelByParent(baseDoms);
        setSideDomByOrder(baseDoms);
    }
    
    /**
     * 1. list를 순회하면서 level을 설정한다.
     * 2. root Dom을 찾아서 덤으로 리턴한다.  
     */
    @Deprecated
    private static Dom setLevelByParent(List<Dom> baseDoms) {
        Dom root = null;
        for(Dom dom : baseDoms){
            if(dom.getParent()==null) root = dom;
            int deep = 1;    //1이 root이다.            
            Dom temp = dom;
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
     */
    @Deprecated
    private static void setSideDomByOrder(List<Dom> baseDoms){
        for(int index=0;index<baseDoms.size();index++){ //parent
            Dom dom = baseDoms.get(index);
            if(index != 0) dom.setBeforeDom(baseDoms.get(index-1));
            if(index != baseDoms.size()-1) dom.setNextDom(baseDoms.get(index+1));
        }
    }
    
    /**
     * Oracle orderSiblings을 흉내낸다. 재귀호출로 tree를 위한 order를 만들어 낸다.
     * @param baseDoms : 원본값
     * @param parent : 부모 객체
     * @param doms : 변환값
     */
    @Deprecated
    private static void setConnectByLoop(List<Dom> baseDoms, Dom parent,List<Dom> doms){
        doms.add(parent);
        List<Dom> children = new ArrayList<Dom>();
        for(Dom childDom:baseDoms) {
            if(childDom.getIdParent()==null) continue;
            if(childDom.getIdParent().equals(parent.getId())) children.add(childDom);
        }
        sort(children);
        for(Dom child : children){
            setConnectByLoop(baseDoms,child,doms);
        }
    }    
    
    public static List<Dom> setConnectBy(List<Dom> baseDoms){
        return new Temp(baseDoms).setConnectBy();
    }    
    

    private static class Temp{
        
        private List<Dom> baseDoms;
        private List<Dom> newDoms;
        private Map<String,Dom> baseMap;
        private Map<String,List<Dom>> parentMap = new HashMap<String,List<Dom>>(); //부모ID가 키값이 된다.
        
        public Temp(List<Dom> baseDoms){
            this.baseDoms = baseDoms;
            init();
        }
        
        /**
         * 쩌는 성능을 위해 2개의 보조 Map을 추가했다. 
         */        
        private void init(){
            baseMap = Sets.getMap(baseDoms);
            for(Dom dom : baseDoms){
                List<Dom> list = parentMap.get(dom.getIdParent());
                if(list==null) list = new ArrayList<Dom>();
                list.add(dom);
                parentMap.put(dom.getIdParent(), list);
            }            
        }
        
        /**
         * 새로운 배열을 만들어 자식 순으로 정렬한다.
         * (connect by 의 order siblings)
         */
        public List<Dom> setConnectBy(){
            setParentDom(); //1
            Dom root = setLevelByParent(); //2
            newDoms = new ArrayList<Dom>();
            setConnectByLoop(root);
            setSideDomByOrder(newDoms);
            return newDoms;
        }        
        
        /** parent를 구한다. */        
        private void setParentDom(){ //이거 public으로 쓸수 있게 변경
            for(Dom dom : baseDoms){
                if(dom.getName()==null) dom.setName(dom.getId());  //name이 없으면 id로 대체??????????????????????
                dom.setParent(getParent(baseMap,dom)); 
            }
        }
        
        /**
         * setParentDom이 설정되어 있어야 사용 가능하다.
         * 1. list를 순회하면서 level을 설정한다.
         * 2. root Dom을 찾아서 덤으로 리턴한다.  
         */
        private Dom setLevelByParent() {
            Dom root = null;
            for(Dom dom : baseDoms){
                if(dom.getParent()==null) root = dom;
                int deep = 1;    //1이 root이다.            
                Dom temp = dom;
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
         */
        private void setSideDomByOrder(List<Dom> baseDoms){
            for(int index=0;index<baseDoms.size();index++){ //parent
                Dom dom = baseDoms.get(index);
                if(index != 0) dom.setBeforeDom(baseDoms.get(index-1));
                if(index != baseDoms.size()-1) dom.setNextDom(baseDoms.get(index+1));
            }
        }   
        
        /**
         * Oracle orderSiblings을 흉내낸다. 재귀호출로 tree를 위한 order를 만들어 낸다.
         * @param baseDoms : 원본값
         * @param parent : 부모 객체
         * @param doms : 변환값
         */
        private void setConnectByLoop(Dom parent){
            newDoms.add(parent);
            //List<Dom> children = new ArrayList<Dom>();
            
            List<Dom> children = parentMap.get(parent.getId());
            if(children==null) return;
            /*
            for(Dom childDom:baseDoms) {
                if(childDom.getIdParent()==null) continue;
                if(childDom.getIdParent().equals(parent.getId())) children.add(childDom);
            }
            */
            sort(children);
            for(Dom child : children){
                setConnectByLoop(child);
            }
        }
    }
    
    
    /**
     * sort 값으로 소팅합니다. 
     */
    public static void sort(List<Dom> doms){
        Collections.sort(doms, new ComparatorBySort());
    }
    /**
     * id 값으로 소팅합니다. (느림)
     */
    public static void sortById(List<Dom> doms){
        Collections.sort(doms, new ComparatorById());
    }
    
    /**
     *  Sort를 위한 DomComparator
     *  1이 100 보다 위에 온다.
     */
    private static class ComparatorBySort implements Comparator<Dom> { 
        public int compare(Dom dom1, Dom dom2) {
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
    private static class ComparatorById implements Comparator<Dom> { 
        public int compare(Dom dom1, Dom dom2) {
            Integer d1 = Integer.parseInt(dom1.getId());
            Integer d2 = Integer.parseInt(dom2.getId());
            if(d1==null || d2==null) return 0;
            return d1 > d2 ? 1 : (d1 == d2 ? 0 : -1);
        }
    }
    
    // ===========================================================================================
    //                               getter / setter     
    // ===========================================================================================
    

    /**
     * @return
     * @uml.property  name="href"
     */
    public String getHref() {
        return href;
    }
    /**
     * @return
     * @uml.property  name="parent"
     */
    public Dom getParent() {
        return parent;
    }
    /**
     * @param parent
     * @uml.property  name="parent"
     */
    public void setParent(Dom parent) {
        this.parent = parent;
    }

    /**
     * @param href
     * @uml.property  name="href"
     */
    public void setHref(String href) {
        this.href = href;
    }
    /**
     * @return
     * @uml.property  name="id"
     */
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
    /**
     * @return
     * @uml.property  name="idParent"
     */
    public String getIdParent() {
        return idParent;
    }
    /**
     * @param idParent
     * @uml.property  name="idParent"
     */
    public void setIdParent(String idParent) {
        this.idParent = idParent;
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
     * @uml.property  name="description"
     */
    public String getDescription() {
        return description;
    }
    /**
     * @param description
     * @uml.property  name="description"
     */
    public void setDescription(String description) {
        this.description = description;
    }
    /**
     * @return
     * @uml.property  name="level"
     */
    public int getLevel() {
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
     * @uml.property  name="param1"
     */
    public String getParam1() {
        return param1;
    }
    /**
     * @param param1
     * @uml.property  name="param1"
     */
    public void setParam1(String param1) {
        this.param1 = param1;
    }
    /**
     * @return
     * @uml.property  name="param2"
     */
    public String getParam2() {
        return param2;
    }
    /**
     * @param param2
     * @uml.property  name="param2"
     */
    public void setParam2(String param2) {
        this.param2 = param2;
    }
    /**
     * @return
     * @uml.property  name="nextDom"
     */
    public Dom getNextDom() {
        return nextDom;
    }
    /**
     * @param nextDom
     * @uml.property  name="nextDom"
     */
    public void setNextDom(Dom nextDom) {
        this.nextDom = nextDom;
    }
    /**
     * @return
     * @uml.property  name="beforeDom"
     */
    public Dom getBeforeDom() {
        return beforeDom;
    }
    /**
     * @param beforeDom
     * @uml.property  name="beforeDom"
     */
    public void setBeforeDom(Dom beforeDom) {
        this.beforeDom = beforeDom;
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
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Dom other = (Dom) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        return true;
    }
    
    @Override
    public Dom clone(){
        try {
            return (Dom)super.clone();
        } catch (CloneNotSupportedException e) {
            Encoders.stackTrace(e);
            return null;
        }
    }
 
	
    
}