package erwins.util.dom2;

import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;


/**
 * @author erwins(my.pojo@gmail.com)
 */
public class DomToJson<T extends Dom<T>>{
    
    private List<T> baseDoms;
    
    public DomToJson(List<T> doms){
        baseDoms = doms;
    }
    
    /**
     * DOM을 해석하여 tree구조의 Apache UL을 리턴한다.
     **/
    public String get(){
        T root = T.getChild(baseDoms, null).get(0);
        JSONArray ul = getUl(root,null);
        if(ul==null) return "no file";
        return ul.toString();
    }
    
    /**
     * DOM을 해석하여 tree구조의 Apache UL을 리턴한다.
     * Root는 무시하며 2레벨 유닛부터 표현한다.
     **/
    public String getForNoRoot(){
        JSONArray ul = getUl(null,null);
        if(ul==null) return "no file";
        return ul.toString();
    }

    /**
     * 자식 객체를 구해서 UL로 담고 각 자식마다. 재귀호출 한다.
     **/
    private JSONArray getUl(T thisDom,JSONObject thisLi){
        List<T> thisDoms = T.getChild(baseDoms, thisDom);
        if(thisDoms.size()==0) return null;
        JSONArray array = new JSONArray();        
        for(T dom : thisDoms){
            JSONObject obj = new JSONObject();
            obj.put("label", dom.getName());
            obj.put("value", dom.getId());
            obj.put("isBranch",!dom.isNoChild());
            
            JSONArray childUl = getUl(dom,obj); //자식을 구해서 모두 담는다.
            if(childUl!=null) obj.put("children",childUl);             
            array.add(obj);
        }
        return array;
    }
    

    
}

