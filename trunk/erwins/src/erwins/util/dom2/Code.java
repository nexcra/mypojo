package erwins.util.dom2;

import java.util.*;

import javax.persistence.*;

import org.apache.commons.lang.StringUtils;
import org.apache.ecs.html.Option;

import erwins.util.lib.Days;
import erwins.util.vender.apache.ECS2;


/**
 * 캐싱 되는 Code이다. root만 1단계이며 나며지는 계층 구조를 가진다.
 * @author  erwins(quantum.object@gmail.com)
 */
@javax.persistence.Entity
@javax.persistence.Table(name="CODE")
@AttributeOverrides({
    @AttributeOverride(name="id"  , column = @Column(name="ID",length=20)),
    @AttributeOverride(name="upperId", column = @Column(name="UPPER_ID",length=20,nullable=false)),
    @AttributeOverride(name="name", column = @Column(name="NAME",length=500,nullable=false)),
    @AttributeOverride(name="sort", column = @Column(name="SORT",length=10))
})
public class Code extends Dom<Code>{
    
    private static final long serialVersionUID = 1L;
    
    /**
     * @uml.property  name="param1"
     */
    protected String param1;  //이거 data1 일케 바꾸기
    /**
     * @uml.property  name="param2"
     */
    protected String param2;
    /**
     * @uml.property  name="use"
     */
    private boolean use = true;
    
    /**
     * 성능에 문제가 있을 수 있다.. ㅠㅠ
     * 부모를 제외한 자식들을 계층형으로 Map에 입력한다.
     */
    public static void init(List<Code> list){
        list = Code.connectByForNoRoot(list);

        List<Code> codes = null;
        for(Code code : list){            
            if(code.isRoot() || code.isEnd()){
                if(codes!=null){
                    codes = Code.connectByForNoRoot(codes);
                    Code.addCode(codes.get(0).getUpperId(), codes);
                }
                codes = new ArrayList<Code>();
            }else{
                codes.add(code);
            }
        }
    }
    
    
    // ===========================================================================================
    //                                    cache
    // ===========================================================================================
    
    /**
     * @uml.property  name="codes"
     */
    private static HashMap<String,List<Code>> codes = new HashMap<String,List<Code>>();  
    
    public static void addCode(String id, List<Code> doms){
        codes.put(id, doms);
    }
    
    /**
     * 방어복사 하지 않는다. 변경 금지!
     * @uml.property  name="codes"
     */
    public static HashMap<String,List<Code>> getCodes(){
        return codes;
    }
    
    // ===========================================================================================
    //                                    static
    // ===========================================================================================
    
    /** 현재 년도 및 5년 간의 년도를 셀렉트 박스로 나타낸다. */
    public static final String OPTION_YEAR = "year";
    /** 12개의 월을 셀렉트 박스로 나타낸다. */
    public static final String OPTION_MONTH = "month";
    
    /**
     * 모든 코드에서 코드ID를 이용해 코드를 검색
     * id로 부모를 찾을 수 있다면 더 빠르게 검색 가능.
     **/
    @Transient public static Code getElementById(String id){
        Code dom = null;
        if(id.length() > 2 && codes.containsKey(id.substring(0,2))){
            dom = Code.getElementById(codes.get(id.substring(0,2)), id);
        }
        if(dom==null){
            for(String key:codes.keySet()){
                dom = Code.getElementById(codes.get(key), id);
                if(dom != null) return dom;
            }            
        }
        if(dom == null) System.out.println("No Code By Id");
        return dom;
    }

    
    /**
     * 기본 검색시 다수의 code를 ","로 연결해서 사용한다. 
     * ex) getOption("12,D84",true); or getOption(Code.OPTION_YEAR,true);
     * @param 기본값으로 "전체"를 사용할지  default = false
     * @param2 현재 월을 기본으로 지정할 것인지? default = false
     */
    @Transient public static String getOption(String parentCd,boolean... condition) {
        List<Code> doms = new ArrayList<Code>();
        if(condition.length != 0 && condition[0])  doms.add(ECS2.emptyDom());
        
        if(StringUtils.contains(parentCd, OPTION_YEAR)){
            int year = Integer.parseInt(Days.YEAR.get());
            for(int i=0;i<5;i++){
                Code dom = new Code();
                dom.setId(String.valueOf(year-i));
                dom.setName(year-i+"년");
                doms.add(dom);
            }
        }else if(StringUtils.contains(parentCd, OPTION_MONTH)){
            int month = 0;
            if(condition.length>1 && condition[1]) month = Integer.parseInt(Days.MONTH.get());
            for(int i=0;i<12;i++){
                Code dom = new Code();
                dom.setId(String.valueOf(i+1));
                dom.setName(i+1+"월");
                if(month!=0 && month==i+1) dom.setLevel(999);
                doms.add(dom);
            }
        }else{
            String[] parentCds = parentCd.split(",");
            for(String code : parentCds){
                doms.addAll(codes.get(code));            
            }
        }
        
        return ECS2.OPTION.get(doms);
    }
    
    /**
     * from 년도에서 현재 년도까지
     * @param1 제조(true:전년도 기본)인지 수입(당해년도 기본)인지?
     * 나중에 표준에 맞게 변경????
     */
    @Transient public static String getSelectYearFrom (int yy,boolean... condition){
        StringBuffer str = new StringBuffer();
        int i=0;
        for(int year = Integer.parseInt(Days.YEAR.get());year >=yy;year--){
            Option o = new Option("");
            o.setValue(year);
            o.setTagText(year+"년");
            if(!condition[0] && i==0) o.setSelected(true);
            if(condition[0] && i==1) o.setSelected(true);
            str.append(o);
            i++;
        }
        return str.toString();
    }    

    /**
     * code에 해당하는 checkBox를 리턴한다.
     */
    @Transient public static String getCheckBox (String parentCd,String name){        
        return ECS2.RADIO.get(codes.get(parentCd), name);
    }

    /**
     * code에 해당하는 radio를 리턴한다.
     */
    @Transient public static String getRadio(String parentCd,String name){
        return ECS2.RADIO.get(codes.get(parentCd), name);
    }
    @Transient public static String getRadio(String parentCd){
        return getRadio(parentCd,parentCd);
    }

    
    // ===========================================================================================
    //                                    getter / setter
    // ===========================================================================================
    /**
     * @return
     * @uml.property  name="param1"
     */
    @Column(name="DATA1",length=2000)
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
    @Column(name="DATA2",length=2000)
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
     * @uml.property  name="use"
     */
    @Column(name="IS_USE",length=1)
    public boolean isUse() {
        return use;
    }
    /**
     * @param isUse
     * @uml.property  name="use"
     */
    public void setUse(boolean isUse) {
        this.use = isUse;
    }
    
}