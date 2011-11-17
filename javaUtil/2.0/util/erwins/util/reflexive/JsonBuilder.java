
package erwins.util.reflexive;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;


public abstract class JsonBuilder<ID extends Serializable,T extends Connectable<ID,T>> extends Connector<ID,T>{
	
    protected JSONArray root = new JSONArray();
    
    public JsonBuilder() {}
    
    public String visit(Collection<T> children) {
        for(T each : children) reflexiveVisit(each,root);
        return root.toString();
    }
    
    protected void reflexiveVisit(T target,JSONArray parent) {
    	JSONObject json = new JSONObject();
    	buildVo(target,json);
        
        List<T> children = target.getChildren();
        if(children!=null && children.size()!=0){
        	JSONArray childrenArray = new JSONArray();
            for(T each : children){
                reflexiveVisit(each,childrenArray);
            }
            json.put(childrenKey(), childrenArray);	
        }
        parent.add(json);
    }
    
    protected abstract void buildVo(T target,JSONObject json);
    
    /** 이 값은 jsTree에 사용된다. 필요하면 오버라이드 하자 */
    protected String childrenKey(){
    	return "children";
    }
}
