
package erwins.util.morph;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import net.sf.json.JSONArray;

import org.apache.ecs.Element;
import org.apache.ecs.html.Option;

import erwins.util.dom2.Code;
import erwins.util.lib.*;
import erwins.util.root.Singleton;

/**
 * ECS2를 개량한 버전이다.
 * 1개씩만 지원한다.
 * 실패작~
 * @author     erwins(my.pojo@gmail.com)
 */
@Singleton
@Deprecated
@SuppressWarnings(value={"unused","unchecked"})
public class HtmlBuilder {

    private List<HtmlCommand> list = new CopyOnWriteArrayList<HtmlCommand>();
    
    public HtmlBuilder add(HtmlCommand command){
        list.add(command);
        return this;
    }
    
    /**
     * preFix가 일치하는 것을 골라서 build한다.
     */
    public static abstract class HtmlCommand{
        protected String preFix = null;
        public HtmlCommand(String preFix){
            this.preFix = preFix;
        }
        public void run(List<Element> json,String key){
            if(Strings.startsWith(key,preFix)) build(json, key);
        }
        abstract public void  build(List<Element> root,String key);
    }

    public String get(String parameter){
        List<Element> root = new ArrayList<Element>();
        for(HtmlCommand type : list){
            type.run(root,parameter);
        }
        return Strings.join(root,"");
    }
    
    public HtmlBuilder(HtmlTemplit ... templits){
        for(HtmlTemplit each:templits) add(each.getC());
    };
    
    /**
     * 기본세팅
     */
    public enum HtmlTemplit{
        /** Enum을 매핑한다. ex) Enum(SomeType) */
        Enum(new HtmlCommand("Enum"){
            @Override
            public void build(List<Element> root, String key) {
                
                
                
                String enumName = Strings.substringsBetween(key,"(",")")[0];
                String className = "erwins.myPage.enums." + enumName;
                Enum[] num = Clazz.getEnums(className);
                
                JSONArray array = JSONs.get(Clazz.getEnums(className),true);
                
            }
        }),
        /** Dom2의 Code를 매핑한다. ex) Code(0,1,345)*/
        CODE(new HtmlCommand("Code"){
            @Override
            public void build(List<Element> json,String key) {
                String upperCodeStr = Strings.substringsBetween(key,"(",")")[0];
                String[] upperCodes = upperCodeStr.split(",");
                for(String upperCode : upperCodes){
                    List<Code> list = Code.getCodes().get(upperCode);      
                    //json.put(preFix+"("+upperCode+")",new DomToJson<Code>(list).getForNoRoot());    
                }
            }
        });
        
        private HtmlCommand c;
        HtmlTemplit(HtmlCommand c){
            this.c = c;
        }
        public HtmlCommand getC() {
            return c;
        }
        
    }
    
    
    /**
     * Enum 타입의 class를 제작한다.
     * toString의 값이 이름이 되며
     * name 값이 DB에 저장되는 value값이 된다.
     * ordinal의 값을 사용할려면 다른 방법을 찾아보자. name방법이 추가/삭제/가시성에 좀더 자유롭다.
     */
    public static String getOption(List<Element> root,Enum<?>[] en,boolean isAll, Enum<?> ... ingnor) {
        List<Code> codes = new ArrayList<Code>();
        //if(isAll) codes.add(emptyDom());
        for (Enum<?> mode : en) {
            if(Sets.isEquals(ingnor, mode)) continue;
            Option o = new Option(mode.name());
            o.setTagText(mode.toString());
            //if(dom.getLevel()==999)  o.setSelected(true);
            root.add(o);

        }
        return "";
    }    

    


}