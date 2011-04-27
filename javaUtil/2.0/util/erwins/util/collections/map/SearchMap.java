package erwins.util.collections.map;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSON;

import org.hibernate.Hibernate;
import org.hibernate.annotations.CollectionOfElements;

import erwins.util.lib.CollectionUtil;
import erwins.util.lib.ReflectionUtil;
import erwins.util.morph.BeanToJson;
import erwins.util.root.EntityHibernatePaging;


/**
 * iBatis & Hibernate용 페이징 처리기.
 * 총 목록개수 Long으로 바꿀껏!
 * @author  erwins
 */

@SuppressWarnings("serial")
public class SearchMap extends RequestMap{
    
    protected static final int DEFAULT_PAGING_SIZE = 15;
    
    /** HTML에서 서버로 전달 받을 요정 페이지 번호의 이름  */
    public static final String HTML_PAGE_NO = "pageNo";
    
    /**
     * 결과값의 reference
     */
    protected List<?> result ;
    
    /**
     * 한번에 가져올 목록의 수.
     */
    protected Integer pagingSize ;
    
    /**
     * 총 목록의 개수
     */
    protected Integer totalCount ;
    
    /**
     * 현제 페이지 번호. 1부터 시작한다.
     */
    protected Integer pageNo;
    
    /**
     * 역 인덱스.
     */
    protected int skipResults;
    
    /**
     * totla count의 캐싱을 할것인지?
     */
    protected boolean optimize = false;
    
    /**
     * total count의 조회가 필요한지?
     */
    protected boolean count = true;
    

    protected Boolean optimized;
    
    /**
     * 1. JAVA에서  pageNo로 paging객체를 생성한다. 이는 쿼리시 사용된다.
     * 디폴트 null의 값인 0이 들어오면 1로 바꿔준다.
     */
    public SearchMap(HttpServletRequest req){
        super(req);
        this.pageNo = getIntegerId(HTML_PAGE_NO);
        //this.putAll(new Rr(req).getMap());
    }
    public SearchMap(int pageNo){
        this.pageNo =   pageNo==0 ? 1 : pageNo;
    }
    public SearchMap(){
    }
    
    /**
     * total count를 구해서 입력한다. 캐싱 하자.
     */    
    public void setTotalCount(Integer totalCount){
        if(pageNo==null) pageNo = 1 ;  //기존 플젝때문에 넣은 임시 메소드.. ㄷㄷ
        this.totalCount = totalCount;
        skipResults = getSkipResults() -1;
    }
    
    public Integer getTotalCount() {
        return totalCount;
    }
    
    /**
     * 페이징 처리를 하는지? pageN0가 있다면 페이징이다.
     */
    public boolean isPaging(){
        if(pageNo==null) return false;
        return true;
    }

    /**
     * 
     * 결과를 캐스팅 하지 않고 반환한다. 
     * 내부 객체를 몰라도 될때 사용하자.
     */
    public List<?> getResult(){
        return result;
    }
    /**
     * 해당 타입으로 캐스팅 해서 리턴한다. 코드 단축용.
     */
    @SuppressWarnings("unchecked")
    public <T> List<T> getResult(Class<T> clazz){
        return (List<T>)result;
    }
    
    /**
     * 하이버네이트의 경우 List형태로 받아오기 때문에 페이징 처리를 따로 해준다.
     * 가장 처음 보는 건의 location이 totalSize가 되도록 넘버링 처리 한다.
     * T는 ListRownumAble를 구현하고 있어야 한다.
     */
    @SuppressWarnings("unchecked")
    public void pagingForHibernate(){
        if(!isPaging()) throw new RuntimeException("this result is not paging!");
        if(totalCount==null) throw new RuntimeException("totalCount must not be null!");
        List<EntityHibernatePaging> temp = (List<EntityHibernatePaging>)result;
        int start = (this.pageNo-1) * this.pagingSize; 
        for(int i=0;i<temp.size();i++){
            EntityHibernatePaging each = temp.get(i);
            int location =  this.totalCount - start - i;
            each.setRownum(location);
        }
    }
    
    /** 캐싱되는 로그 등을 페이징처리하기위한 메소드. */
    @SuppressWarnings("unchecked")
	public <T> void pagingForList(List<T> list){
    	if(!isPaging()) throw new RuntimeException("this result is not paging!");
    	result = new ArrayList<T>();
    	this.totalCount = list.size();
    	List<T> temp = (List<T>)result; 
    	int index = getSkipResults();
    	Iterator<T> i = list.listIterator(index);
    	while(i.hasNext()){
    		temp.add(i.next());
    		if(temp.size() >= this.getPagingSize() ) break;
    	}
    }

    /** 정확히는 ADD가 맞다. ㅋ */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void setResult(List<?> list){
        if(this.result==null) this.result = list;
        else this.result.addAll((Collection)list);
    }
    
    /** 타입이 Map인 경우에만 사용 가능하다. */
    @SuppressWarnings("unchecked")
    public void setRowNum(){
        List<Map<String,Object>> obj = (List<Map<String,Object>>)getResult();
        for(Map<String,Object> map : obj) map.put("ROWNUM", nextIndex());
    }
    
    /**
     * 안타깝게도 HTML에서 배열로 받은 map의 Array는 iBatis가 Array로 인식하지 못한다.
     * 따라서 Array를 Collection의 List로 변형해주는 로직을 가진다.
     * ex) <isNotEmpty property="comType" >        
            <iterate prepend="AND" property="comType" open="(" close=")" conjunction="OR">
                a.role like '%'||#comType[]#||'%'
             </iterate>
           </isNotEmpty>   
     */
    public void setIBatisIterator(String ... keys){
        for(String key : keys){
        	Object obj = theMap.get(key);
            if(obj==null) continue;
            theMap.put(key, CollectionUtil.toStringList(obj));
        }
    }
    
    /**
     * String type만 적용 가능
     * "and"가 붙은것을 "="조건으로 묶어준다. <br>
     * "like"가 붙은것을 "like"조건으로 묶어준다. <br>
     * "ge"가 붙은것을 "크거나 같은"조건으로 묶어준다. ex) 시작일 <br>
     * "le"가 붙은것을 "작거나 같은"조건으로 묶어준다. ex) 종료일 <br>
     * 주의! like등의 조건에 해당하지만 실제 클래스와 매칭이 안되면 오류 발생시킴
     */    
    /*
    public Conjunction getStringJunction(){ //////////////// setParameter를 사용하면 하이버가 알아서 판단!
        if(junction==null) junction = Restrictions.conjunction(); 
        for(Object key : keySet() ){
            String keyStr = key.toString();
            if(keyStr.startsWith("eq")){
                String value = getStr(key);
                if(!value.equals(""))
                    junction.add(Restrictions.eq(Strings.getCamelize2(keyStr,"eq"), value));
                    //junction.add(Restrictions.eq(Strings.getCamelize2(keyStr,"eq"),SomeType.valueOf(value)));
            }else if(keyStr.startsWith("like")){
                String value = getStr(key);
                if(!value.equals("")) 
                    junction.add(Restrictions.like(Strings.getCamelize2(keyStr,"like"), value,MatchMode.ANYWHERE));
            }else if(keyStr.startsWith("ge")){
                String value = getStr(key);
                junction.add(Restrictions.ge(Strings.getCamelize2(keyStr,"ge"), value));
            }else if(keyStr.startsWith("le")){
                String value = getStr(key);
                junction.add(Restrictions.le(Strings.getCamelize2(keyStr,"le"), value));
            }
        }
        return junction;
    }
    */
    
    /*
    private static final String[] searchKeyword = {"eq","like","ge","le"};
    
    /**
     * Jsp에서 search조건들을 초기화 한다.
     * 라디오도 되게 수정할것..
     */
    /*
    public String getJson(){
        JSONObject json = new JSONObject();
        for(Object key : keySet() ){
            String keyStr = key.toString();
            for(String searchKey : searchKeyword){
                if(keyStr.startsWith(searchKey)){
                    String value = getStr(key);
                    if(!StringUtils.isEmpty(value)){                    
                        json.put(keyStr, value);   
                    }
                    break;
                }
            }

        }
        return json.toString();
    }*/
    
    /** 급조했음. 주의!  다 귀찮을때 사용하자. */
    @SuppressWarnings("unchecked")
	public void initializeByHibernate(){
    	if(result==null || result.size()==0) return;
    	Class<?> clazz = result.get(0).getClass();
        Method[] methods = clazz.getMethods();
        
        for(Object each : result){
        	for(Method method : methods){
                if(!ReflectionUtil.isGetter(method)) continue;
                if(CollectionUtil.isAnnotationPresent(method, CollectionOfElements.class,OneToMany.class,ManyToOne.class)){
                    if(method.getParameterTypes().length!=0) continue;
                    Object proxy;
                    try {
                        proxy = method.invoke(each);
                    }
                    catch (Exception e) {
                        throw new RuntimeException(e.getMessage(),e);
                    }
                    Hibernate.initialize(proxy);
                }
            }
        }
    }
    
    public JSON toJSON() {
    	return BeanToJson.create().build(this.getResult());
    }
    
    // ===========================================================================================
    //                                치환      
    // ===========================================================================================
    
    /**
     * result값의 Enum id에 해당하는 값을 name으로 바꾼다.
     * iBatis등의 기본 문자열로 매칭한다. 즉 모두 대문자라고 가정.
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void enumerated(Class ... enums){
        for(Map<Object,Object> map : getResult(Map.class)){
            for(Class clazz : enums){
                if(!clazz.isEnum()) throw new RuntimeException("only enum plz..");
                String key = clazz.getSimpleName().toUpperCase();
                String value =  (String)map.get(key);
                if(value==null) continue;
                String name = Enum.valueOf(clazz, value).toString();
                map.put(key, name);
            }
        }
    }    
    
    
    // ===========================================================================================
    //                                   method
    // ===========================================================================================
    
    /**
     * @param pageNo
     * @uml.property  name="pageNo"
     */
    public void setPageNo(Integer pageNo){
        this.pageNo = pageNo;
    }
    /**
     * @return
     * @uml.property  name="pageNo"
     */
    public Integer getPageNo(){
        return this.pageNo;
    }    
    
    /**
     * 현재 체이지 번호에 따라 목록의 중단 시점을 구한다. iBatis or Hibernate등에서 사용?? +1해야하는거 아님?
     * @uml.property  name="skipResults"
     */
    public int getSkipResults(){
        return pageNo*getPagingSize()-getPagingSize();
    }
    
    /**
     * 최대 페이지 no
     **/
    public int getMaxPageNo() {
        return totalCount /getPagingSize() +1;
    }
    
    /**
     * JSP에서 역순으로 index를 생성할때 사용된다.
     **/
    public int nextIndex() {
        skipResults++;
        return totalCount-skipResults;
    }
     
    /**
     * 이전 목록이 있는지? 
     */
    public boolean isBeforeAble(){
        return pageNo > 1 ? true : false;
    }
    
    /**
     * pagingCount를 캐싱해서 대용량 SQL조회 속도를 올린다.
     * @uml.property  name="optimize"
     */
    public void setOptimize(boolean optimization) {
        this.optimize = optimization;
    }
    /**
     * pagingCount를 캐싱하는지?
     * @uml.property  name="optimize"
     */
    public boolean isOptimize() {
        return optimize;
    }
    
    /**
     * 이번 SQL이 optimized되었응가?
     */
    public Boolean isOptimized() {
        return optimized;
    }
    /**
     * optimized되었을때 기록
     * @uml.property  name="optimized"
     */
    public void setOptimized(Boolean optimized) {
        this.optimized = optimized;
    }
    /**
     * 다음 목록이 있는지?
     */
    public boolean isNextAble(){
        return (pageNo)*10 < totalCount ? true : false;
    }
    /**
     * @return
     * @uml.property  name="pagingSize"
     */
    public Integer getPagingSize() {
        if(pagingSize==null) return DEFAULT_PAGING_SIZE;
        return pagingSize;
    }
    /**
     * @param pagingSize
     * @uml.property  name="pagingSize"
     */
    public void setPagingSize(Integer pagingSize) {
        this.pagingSize = pagingSize;
    }
    
    /** totalCount를 사용하는가? 가본값은 true */
    public void setCount(boolean count) {
        this.count = count;
    }
    /** totalCount를 사용하는가? 가본값은 true */
    public boolean isCount() {
        return count;
    }    
    
    // ===========================================================================================
    //                                   간이 네이게이션 바.
    // ===========================================================================================    
    


    /** 간이 네비게이션 바를 생성한다. */
    public String getSimpleNavigationBar(String url){
        return new SimpleNavigationBar(url).get();
    }
    
    /**
     * 간단한 / Ajax에서는 사용 불가능한 네이게이션바.
     */
    private class SimpleNavigationBar {

        private String searchParams;
        private String uri;

        public SimpleNavigationBar(String uri) {
            this.searchParams = "#";
            this.uri = uri;
        }

        public String get() {
            StringBuffer str = new StringBuffer("");
            int pCount = 0;
            int pStart = 0;

            pCount = (totalCount - 1) / getPagingSize() + 1;
            pStart = ((pageNo - 1) / 10) * 10 + 1;
            
            if (pageNo > 10) {
                str.append("<a href=\"");
                str.append(getPageUri(1));
                str.append("\">[Before]</a>\n");
                str.append("<a href=\"");
                str.append(getPageUri(pStart - 10));
                str.append("\">[First]</a>\n");
            }

            for (int i = pStart; i <= pCount && i - pStart < 10; i++)
                if (i == pageNo) {
                    str.append("| <strong>");
                    str.append(i);
                    str.append("</strong>&nbsp;");
                } else {
                    str.append("| <a href=\"");
                    str.append(getPageUri(i));
                    str.append("\">");
                    str.append(i);
                    str.append("</a>&nbsp;\n");
                }

            str.append("|");

            if (pCount - pStart >= 10) {
                str.append(" <a href=\"");
                str.append(getPageUri(pStart + 10));
                str.append("\">[Next]</a>\n");
                str.append(" <a href=\"");
                str.append(getPageUri(((pCount - 1) / 10) * 10 + 1));
                str.append("\">[Last]</a>\n");
            }
            return str.toString();
        }

        private String getBaseUri() {
            return this.uri + "?" + this.searchParams;
        }

        private String getPageUri(int page) {
            return getBaseUri() + getPageParams(page);
        }

        private String getPageParams(int page) {
            return "&" + HTML_PAGE_NO + "=" + page ;
        }

    }
    @Override
    public boolean equals(Object o) {
		return theMap.equals(o);
	}
    @Override
    public int hashCode() {
        return super.hashCode();
    }
    /**
     * HashMap등에 사용되는 코드
     */
    public String hashCode(String str) {
        return str + super.hashCode();
    }
    
}