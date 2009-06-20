
package erwins.util.vender.apache;

import java.util.ArrayList;
import java.util.List;

import org.apache.ecs.Element;
import org.apache.ecs.html.*;

import erwins.util.dom2.Code;
import erwins.util.lib.Sets;
import erwins.util.lib.Strings;

/**
 * 아파치 ECS를 래핑한다. 왜 래핑했냐문.. 사용법을 자꾸 까먹어서 ㅋㅋ enum은 HTML INPUT만 지원한다.
 * @author     erwins(my.pojo@gmail.com)
 */
public enum ECS2 {

    /**
     * @uml.property  name="hIDDEN"
     * @uml.associationEnd  
     */
    HIDDEN("hidden"), 
    /**
     * @uml.property  name="bUTTON"
     * @uml.associationEnd  
     */
    BUTTON("button"), 
    /**
     * @uml.property  name="rADIO"
     * @uml.associationEnd  
     */
    RADIO("radio"), 
    /**
     * @uml.property  name="cHECK_BOX"
     * @uml.associationEnd  
     */
    CHECK_BOX("checkBox"),
    /**
     * @uml.property  name="oPTION"
     * @uml.associationEnd  
     */
    OPTION("option"),
    /**
     * @uml.property  name="gROUP_OPTION"
     * @uml.associationEnd  
     */
    GROUP_OPTION("");

    private ECS2(String name) {
        this.name = name;
    }

    private String name;

    public Input get(String name, String value) {
        Input input = new Input(this.name, name, value);
        return input;
    }
    
    public Input get(String value) {
        Input input = new Input(this.name);
        input.setValue(value);        
        return input;
    }
    
    /**
     * 해당하는 doms으로 완성된 문자열을 리턴한다.
     * 완성된 doms를 직접 제작해서 사용할 때 사용된다. 
     * OPTION 이외에는 entityName가 필수이다.
     */
    public String get(List<Code> doms,String ... entityName) {
        List<Element> options = new ArrayList<Element>();
        switch (this) {
            case OPTION: Translator.option(doms, options); break;
            case GROUP_OPTION: Translator.groupOption(doms, options); break;
            case RADIO: Translator.radio(doms, options,entityName[0]); break;
            case CHECK_BOX: Translator.checkBox(doms, options,entityName[0]); break;
        }
        return Strings.joinTemp(options);
    }
    
    
    // ===========================================================================================
    //                                    STATIC
    // ===========================================================================================

    /**
     * cursor:hand 는 IE만 된다. 바꾸자.
     */
    public static Label getLabel(String forTargetId) {
        Label label = new Label(forTargetId);
        label.setStyle("cursor:hand"); 
        return label;
    }

    /**
     * input에 라벨을 씌워 리턴한다.
     */
    public static Label getLabel(Input in) {
        Label label = new Label(in.getAttribute("id"));
        label.setStyle("cursor:hand");
        label.addElement(in);
        return label;
    }

    public static Option getOption(String value, String tagText) {
        Option o = new Option(value);
        o.setTagText(tagText);
        return o;
    }
    
    /**
     * 콤보의 "전체" 등으로 사용되는 value가 ""인 Dom을 생성한다.
     * @param Dom의 이름 없을경우"전체"
     */
    public static  Code emptyDom(String... strings){
        Code dom = new Code();
        dom.setId("");
        dom.setName(strings.length == 1 ? strings[0] : "전체");
        return dom;
    }
    
    /**
     * Enum 타입의 class를 제작한다.
     * toString의 값이 이름이 되며
     * name 값이 DB에 저장되는 value값이 된다.
     * ordinal의 값을 사용할려면 다른 방법을 찾아보자. name방법이 추가/삭제/가시성에 좀더 자유롭다.
     */
    public static String getOption(Enum<?>[] en,boolean isAll, Enum<?> ... ingnor) {
        List<Code> codes = new ArrayList<Code>();
        if(isAll) codes.add(emptyDom());
        for (Enum<?> mode : en) {
            if(Sets.isEquals(ingnor, mode)) continue;
            Code code = new Code();
            //code.setIdByInt(mode.ordinal());
            code.setId(mode.name());
            code.setName(mode.toString());
            codes.add(code);
        }
        return ECS2.OPTION.get(codes);
    }

    /**
     * doms를 ECS의 options 등으로 변환해준다.
     */
    private static class Translator{

        public static void option(List<Code> doms, List<Element> options) {
            for (Code dom : doms) {
                options.add(option(dom));
            }
        }
        
        /**
         * level이 999이면 선택된 상태.
         */        
        private static Option option(Code dom){
            Option o = new Option(dom.getId());
            o.setTagText(dom.getName());
            if(dom.getLevel()==999)  o.setSelected(true);
            return o;
        }
        
        /**
         * 그룹 옵션을 정의한다.
         * 그룹옵션은 계층형이 아님으로 2레벨만 지원한다.
         */
        public static void groupOption(List<Code> doms, List<Element> options) {            
            for (Code dom : doms) {
                if(dom.isNoChild()) continue;
                for (Code child : Code.getChild(doms, dom)) {
                    OptGroup optGroup = new OptGroup();
                    optGroup.addElement(option(child));
                    options.add(optGroup);
                }
            }
        }
        
        /**
         * 제일 처음것이 선택된 상태.
         */
        public static void radio(List<Code> doms, List<Element> options,String entityName) {
            int i=0;
            for (Code dom : doms) {              
                Input input = new Input("radio",entityName,dom.getId());
                input.setID(entityName + "_"+i);
                input.setTagText(dom.getName());
                if(i==0) input.setChecked(true);
                Label label = ECS2.getLabel(input);
                options.add(label);
                i++;
            }
        }
        public static void checkBox(List<Code> doms, List<Element> options,String entityName) {
            int i=0;
            for (Code dom : doms) {
                Input input = new Input("checkBox",entityName,dom.getId());
                input.setID(dom.getName() + "_"+i);
                input.setTagText(dom.getName());
                Label label = ECS2.getLabel(input);
                options.add(label);                
                i++;
            }
        }
    }


}