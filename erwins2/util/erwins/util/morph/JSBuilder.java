
package erwins.util.morph;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import erwins.util.lib.ReflectionUtil;
import erwins.util.lib.CollectionUtil;
import erwins.util.lib.StringUtil;
import erwins.util.root.Pair;
import erwins.util.root.Singleton;
import erwins.util.vender.etc.Flex;

/**
 * Flex등에서 요청받은 String문자열로 Json을 생성해내는 빌더이다.
 * 간단한 콤보박스 등의 제작, 디폴트 코드 세팅에 사용된다.
 * 입력(String) : 다수의 요청 문자열은 기본 |로 잘라낸다.
 * 리턴 : json에 요청 문자열을 key로 한 hash에 배열로 담긴다. 
 * 추가 사항은 클로저를 등록해서 사용하자. (동적으로 추가가 가능하다.) 
 * @author erwins(my.pojo@gmail.com)
 */
@Singleton
public class JSBuilder{
    
    private static final String seperator = "\\|";
    
    private List<JsonCommand> list = new CopyOnWriteArrayList<JsonCommand>();
    
    public JSBuilder add(JsonCommand command){
        list.add(command);
        return this;
    }
    
    public JSBuilder(JsonTemplit ... templits){
        for(JsonTemplit each:templits) add(each.getC());
    };
    
    public JSONObject build(String parameter){
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
            if(StringUtil.startsWith(key,preFix)) build(json, key);
        }
        abstract public void  build(JSONObject root,String key);
    }
    
    /**
     * 기본세팅
     */
    public enum JsonTemplit{
        /** Enum을 매핑한다. ex) Enum(SomeType,false)
         * 2번째 인자는 All(전체)를 넣을지 말지를 결정한다. 디폴트는 true이다. 
         * */
        Enum(new JsonCommand("Enum"){
            @Override
            public void build(JSONObject root, String key) {
                String argsString = StringUtil.substringsBetween(key,"(",")")[0];
                String[] args = argsString.split(",");
                boolean all = args.length >  1 && args[1].equals("false") ? false : true;
                JSONArray array = get((Pair[])ReflectionUtil.getEnums(args[0]),all);
                root.put(args[0],array);
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
    
    // ===========================================================================================
    //                                    enum
    // ===========================================================================================
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static JSONArray get(Class<?> clazz,boolean all) {
        Enum<?>[] qwe = ((Class<Enum>)clazz).getEnumConstants();
        return get((Pair[])qwe,all);
    }
    
    /**
     * Enum 타입의 class를 제작한다.
     */
    public static JSONArray get(Pair[] pairs, boolean all, Pair... ingnor) {
        JSONArray jsonArray = new JSONArray();
        if(all) jsonArray.add(emptyJson());
        for (Pair pair : pairs) {
            if(CollectionUtil.isEqualsAny(ingnor, pair)) continue;
            JSONObject json = new JSONObject();
            json.put(Flex.LABEL, pair.getName());
            json.put(Flex.VALUE, pair.getValue());
            jsonArray.add(json);
        }
        return jsonArray;
    }

    // ===========================================================================================
    //                                기타 기본 메소드    
    // ===========================================================================================

    /**
     * JSON에 자신을 가르키는 this를 obj로 포함시킨다. (obj:this)
     **/
    public static String addJsonThis(JSONObject e) {
        if(e==null) return null;
        if(e.size()==0) return e.toString();
        return "{obj:this,"+e.toString().substring(1);
    }    
    
    private static JSONObject emptyJson(){
        JSONObject json = new JSONObject();
        json.put(Flex.LABEL, "전체");
        json.put(Flex.VALUE,"");
        return json;
    }
    
}
