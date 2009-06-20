
package erwins.util.morph;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import erwins.util.dom2.*;
import erwins.util.lib.Clazz;
import erwins.util.lib.Strings;
import erwins.util.root.Singleton;

/**
 * Flex등에서 요청받은 String문자열로 Json을 생성해내는 빌더이다.
 * 간단한 콤보박스 등의 제작, 디폴트 코드 세팅에 사용된다.
 * 입력(String) : 다수의 요청 문자열은 기본 |로 잘라낸다.
 * 리턴 : json에 요청 문자열을 key로 한 hash에 배열로 담긴다. 
 * 추가 사항은 클로저를 등록해서 사용하자. (동적으로 추가가 가능하다.) 
 * @author erwins(my.pojo@gmail.com)
 */
@Singleton
public class JsonBuilder{
    
    private static final String seperator = "\\|";
    
    private List<JsonCommand> list = new CopyOnWriteArrayList<JsonCommand>();
    
    public JsonBuilder add(JsonCommand command){
        list.add(command);
        return this;
    }
    
    public JsonBuilder(JsonTemplit ... templits){
        for(JsonTemplit each:templits) add(each.getC());
    };
    
    public JSONObject get(String parameter){
        JSONObject root = new JSONObject();
        String[] keys = parameter.split(seperator);
        for(String key : keys){
            for(JsonCommand type : list){
                type.run(root,key);
            }
        }
        return root;
    }
    
    /**
     * preFix가 일치하는 것을 골라서 build한다.
     */
    public static abstract class JsonCommand{
        protected String preFix = null;
        public JsonCommand(String preFix){
            this.preFix = preFix;
        }
        public void run(JSONObject json,String key){
            if(Strings.startsWith(key,preFix)) build(json, key);
        }
        abstract public void  build(JSONObject root,String key);
    }
    
    /**
     * 기본세팅
     */
    public enum JsonTemplit{
        /** Enum을 매핑한다. ex) Enum(SomeType) */
        Enum(new JsonCommand("Enum"){
            @Override
            public void build(JSONObject root, String key) {
                String enumName = Strings.substringsBetween(key,"(",")")[0];
                //String className = "erwins.myPage.enums." + enumName;
                String className = enumName;
                JSONArray array = JSONs.get(Clazz.getEnums(className),true);
                root.put(enumName,array);
            }
        }),
        /** Dom2의 Code를 매핑한다. ex) Code(0,1,345)*/
        CODE(new JsonCommand("Code"){
            @Override
            public void build(JSONObject json,String key) {
                String upperCodeStr = Strings.substringsBetween(key,"(",")")[0];
                String[] upperCodes = upperCodeStr.split(",");
                for(String upperCode : upperCodes){
                    List<Code> list = Code.getCodes().get(upperCode);      
                    json.put(preFix+"("+upperCode+")",new DomToJson<Code>(list).getForNoRoot());    
                }
            }
        }),
        /** Dom2의 Menu를 매핑한다. */
        MENU(new JsonCommand("Menu"){
            @Override
            public void build(JSONObject json,String key) {
                List<Menu> list = Menu.getMenu();
                json.put(key,new DomToJson<Menu>(list).get());
            }
        });
        
        private JsonCommand c;
        JsonTemplit(JsonCommand c){
            this.c = c;
        }
        public JsonCommand getC() {
            return c;
        }
        
    }

}
