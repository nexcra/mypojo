package erwins.util.tools;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.hibernate.criterion.*;

import erwins.util.lib.*;
import erwins.util.morph.Rr;


/**
 * iBatis & Hibernate용 페이징 처리기.
 * @author  erwins
 */

public class SearchMap extends Mapp {
    
    private static final long serialVersionUID = 1L;
    
    /** HTML에서 서버로 전달 받을 요정 페이지 번호의 이름  */
    public static final String HTML_PAGE_NO = "pageNo";
    /**
     * @uml.property  name="result"
     */
    protected List<?> result ;
    protected static int DEFAULT_PAGING_SIZE = 15;
    /**
     * @uml.property  name="pagingSize"
     */
    protected Integer pagingSize ;
    /**
     * @uml.property  name="totalCount"
     */
    protected Integer totalCount ;
    /**
     * @uml.property  name="pageNo"
     */
    protected Integer pageNo;
    /**
     * @uml.property  name="skipResults"
     */
    protected int skipResults; //역 인덱스.
    
    /**
     * @uml.property  name="optimize"
     */
    protected boolean optimize = false;
    protected Boolean optimized;
    
    /**
     * Hibernate용 and  Conjunction
     * @uml.property  name="junction"
     */
    private Conjunction  junction;
    
    /**
     * 1. JAVA에서  pageNo로 paging객체를 생성한다. 이는 쿼리시 사용된다.
     * 디폴트 null의 값인 0이 들어오면 1로 바꿔준다.
     */
    public SearchMap(HttpServletRequest req){
        String pageNo = req.getParameter(HTML_PAGE_NO);
        this.pageNo = pageNo==null || pageNo.equals("") ? null : Integer.parseInt(pageNo);
        this.putAll(new Rr<Object>(req).getMap());
    }
    public SearchMap(int pageNo){
        this.pageNo =   pageNo==0 ? 1 : pageNo;
    }
    
    /**
     * total count를 구해서 입력한다. 캐싱 하자.
     * @uml.property  name="totalCount"
     */    
    public void setTotalCount(Integer totalCount){
        if(pageNo==null) pageNo = 1 ;  //기존 플젝때문에 넣은 임시 메소드.. ㄷㄷ
        this.totalCount = totalCount;
        skipResults = getSkipResults() -1;
    }
    
    /**
     * @return
     * @uml.property  name="totalCount"
     */
    public Integer getTotalCount() {
        return totalCount;
    }
    
    /**
     * 페이징 처리를 하는지? 만드시 total count를 먼저 구한 후 객체 List를 구해야 한다?
     */
    public boolean isPaging(){
        if(pageNo==null) return false;
        else return true;
    }

    /**
     * @return
     * @uml.property  name="result"
     */
    public List<?> getResult(){
        return result;
    }
    
    @SuppressWarnings("unchecked")
    public List<Mapp> getResultMapp(){
        return (List<Mapp>)result;
    }
    
    @SuppressWarnings("unchecked")
    public List<Map<String,Object>> getResultMapStr(){
        return (List<Map<String,Object>>)result;
    }
    
    @SuppressWarnings("unchecked")
    public List<Map<Object,Object>> getResultMap(){
        return (List<Map<Object,Object>>)result;
    }
    
    /**
     * paging을 리턴 후 삭제한다.
     * @uml.property  name="result"
     */
    public void setResult(List<?> list){
        this.result = list;
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
    public void setIBatisIterator(Object ... keys){
        for(Object key : keys){
            Object obj = get(key);
            if(obj==null) continue;
            put(key, Sets.toStringList(obj));
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
    
    /**
     * 모든 타입??에 사용 가능. 성능은 의심.
     * 주의! like등의 조건에 해당하지만 실제 클래스와 매칭이 안되면 오류 발생시킴 
     */
    public Criterion getCastedJunction(Class<?> clazz){
        
        if(junction==null) junction = Restrictions.conjunction(); 
        for(Object key : keySet() ){
            String keyStr = key.toString();
            String value = getStr(key);
            if(value.equals("")) continue; 
            
            if(keyStr.startsWith("eq")){
                String fieldName = Strings.getCamelize2(keyStr,"eq");
                Object obj = Clazz.getCastedValue(clazz,fieldName,value);
                junction.add(Restrictions.eq(fieldName,obj));
            }else if(keyStr.startsWith("like")){
                String fieldName = Strings.getCamelize2(keyStr,"like");
                junction.add(Restrictions.like(fieldName, value,MatchMode.ANYWHERE));
            }else if(keyStr.startsWith("ge")){
                String fieldName = Strings.getCamelize2(keyStr,"ge");
                Object obj = Clazz.getCastedValue(clazz,fieldName,value);                
                junction.add(Restrictions.ge(fieldName, obj));
            }else if(keyStr.startsWith("le")){
                String fieldName = Strings.getCamelize2(keyStr,"le");
                Object obj = Clazz.getCastedValue(clazz,fieldName,value);
                junction.add(Restrictions.le(fieldName, obj));
            }
        }
        return junction;
    }  
    
    /**
     * 조건을 추가한다.
     */
    public void addAnd(Criterion ... res){
        if(junction==null) junction = Restrictions.conjunction();
        for(Criterion c:res) junction.add(c);
    }
    
    /**
     * 조건을 추가한다.
     */
    public void addOr(Criterion ... res){
        if(junction==null) junction = Restrictions.conjunction();
        Disjunction or = Restrictions.disjunction();
        for(Criterion c:res) or.add(c);
        junction.add(or);
    }
    
    private static final String[] searchKeyword = {"eq","like","ge","le"};
    
    /**
     * Jsp에서 search조건들을 초기화 한다.
     * 라디오도 되게 수정할것..
     */
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
    }
    
    // ===========================================================================================
    //                                치환      
    // ===========================================================================================
    
    /**
     * iBatis등의 기본 문자열.. 즉 모두 대문자라고 가정.
     */
    @SuppressWarnings("unchecked")
    public void enumerated(Class ... enums){
        for(Map<Object,Object> map : getResultMap()){
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
        else return pagingSize;
    }
    /**
     * @param pagingSize
     * @uml.property  name="pagingSize"
     */
    public void setPagingSize(Integer pagingSize) {
        this.pagingSize = pagingSize;
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