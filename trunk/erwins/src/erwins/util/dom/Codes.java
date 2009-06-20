package erwins.util.dom;

import java.util.*;

import javax.servlet.jsp.JspException;

import org.apache.commons.lang.StringUtils;
import org.apache.ecs.html.Input;
import org.apache.ecs.html.Span;

import erwins.util.lib.Days;
import erwins.util.vender.apache.ECS;

/**
 * @author  erwins(quantum.object@gmail.com)
 */
public abstract class Codes{
    
    /** 현재 년도 및 5년 간의 년도를 셀렉트 박스로 나타낸다. */
    public static final String OPTION_YEAR = "year";
    /** 12개의 월을 셀렉트 박스로 나타낸다. */
    public static final String OPTION_MONTH = "month";
    
    //cache
    /**
     * @uml.property  name="codes"
     */
    private static HashMap<String,List<Dom>> codes = new HashMap<String,List<Dom>>();  
    
    public static void addCode(String id, List<Dom> doms){
        codes.put(id, doms);
    }
    
    /**
     * 방어복사 하지 않는다.
     * @uml.property  name="codes"
     */
    public static HashMap<String,List<Dom>> getCodes(){
        return codes;
    }
    
    /**
     * 모든 코드에서 코드ID를 이용해 코드를 검색
     **/
    public static Dom getCodeById(String id){
        for(String key:codes.keySet()){
            Dom dom = Dom.getElementById(codes.get(key), id); 
            if(dom != null) return dom;
        }
        System.out.println("No Code By Id");
        return null;
    }
    
    /**
     * 기본 검색시 다수의 code를 ","로 연결해서 사용한다. 
     * ex) getOption("12,D84",true); or getOption(Code.OPTION_YEAR,true);
     * @param 기본값으로 "전체"를 사용할지  default = false
     * @param2 현재 월을 기본으로 지정할 것인지? default = false
     */
    public static String getOption(String parentCd,boolean... condition) {
        List<Dom> doms = new ArrayList<Dom>();
        if(condition.length != 0 && condition[0])  doms.add(ECS.emptyDom());
        
        if(StringUtils.contains(parentCd, OPTION_YEAR)){
            int year = Integer.parseInt(Days.YEAR.get());
            for(int i=0;i<5;i++){
                Dom dom = new Dom();
                dom.setId(String.valueOf(year-i));
                dom.setName(year-i+"년");
                doms.add(dom);
            }
        }else if(StringUtils.contains(parentCd, OPTION_MONTH)){
            int month = 0;
            if(condition.length>1 && condition[1]) month = Integer.parseInt(Days.MONTH.get());
            for(int i=0;i<12;i++){
                Dom dom = new Dom();
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
        
        return ECS.OPTION.get(doms);
    }
    
    /**
     * IBSheet용 콤보를 리턴한다. 
     * EX) sheet.InitDataCombo(0,"itemTypeCd",<%=Code.~~%>); 
     */
    public static String getComboBoxForIBSheet(String parentCd){
        StringBuffer optionName = new StringBuffer();
        StringBuffer optionValue = new StringBuffer();
        
        boolean isFirst = true;
        
        optionName.append("\"");
        optionValue.append("\"");
        for(Dom dom : codes.get(parentCd)){            
            if(isFirst) isFirst = false;
            else {
                optionName.append("|");
                optionValue.append("|");
            }
            optionName.append(dom.getName());
            optionValue.append(dom.getId());
        }
        optionName.append("\"");
        optionValue.append("\"");
        
        return optionName.toString() +","+ optionValue.toString();
    }

    /**
     * code에 해당하는 checkBox를 리턴한다.
     */
    public static String getCheckBox (String parentCd,String name){        
        return ECS.RADIO.get(codes.get(parentCd), name);
    }

    /**
     * code에 해당하는 radio를 리턴한다.
     */
    public static String getRadio(String parentCd,String name){
        return ECS.RADIO.get(codes.get(parentCd), name);
    }
    public static String getRadio(String parentCd){
        return getRadio(parentCd,parentCd);
    }

    /**
     * code에 해당하는 다중 radio를 리턴한다.
     * 1레벨 뒤에 하위 2레벨이 순차적으로 들어와야 한다.
     */
    public static String getMultiRadio (String parentCd,String name) throws JspException{
        List<Dom> domList = codes.get(parentCd);
        StringBuffer parent = new StringBuffer();
        StringBuffer child = new StringBuffer();
        Dom dom;
        Span span = null;
        for(int i=0;i<domList.size();++i){
            dom = domList.get(i);
            if(dom.getLevel()==1){
                Input input = ECS.RADIO.get(name, dom.getId());
                //input.setOnClick("onMultiRadio('"+dom.getId()+"');");
                input.setID(name + "_"+i);        
                input.addAttribute("ev", "radioRoot");
                //input.setOnClick("onMultiRadio(this);");
                input.setTagText(dom.getName());
                span = new Span();
                span.setID(dom.getId());
                
                if(i==0) input.setChecked(true);         
                else span.setStyle("display:none");   
                
                parent.append(ECS.getLabel(input));
                
            }else if(dom.getLevel()==2){
                Input input = new Input("radio",name,dom.getId());
                input.setID(dom.getId());
                if(i==0) input.setChecked(true);
                input.setTagText(dom.getName());
                span.addElement(ECS.getLabel(input));
                if(dom.isTop()) child.append(span);
            }else{
                throw new JspException("sorry! we only help 2nd Level");
            }
        }
        return parent.toString() +"<br>"+ child.toString();
    }
    
   
    
}