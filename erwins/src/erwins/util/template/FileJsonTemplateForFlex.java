
package erwins.util.template;

import java.io.File;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import erwins.util.vender.etc.Flex;

public abstract class FileJsonTemplateForFlex{
    
    protected JSONArray result = new JSONArray();
    
    public JSONArray visit(File root) {
        reflexiveVisit(root,result);
        return result;
    }
    
    public JSONArray visitIgnoreRoot(File root) {
        for (File each : root.listFiles()) {
            reflexiveVisit(each,result);
        }
        return result;
    }
    
    /** file일경우 obj에 들어갈 내용을 기술한다 */
    protected abstract void build(File file,JSONObject obj);
    
    protected void reflexiveVisit(File file,JSONArray array) {
        if (file.isHidden()) return;
        JSONObject obj = new JSONObject();
        obj.put("label", file.getName());

        if (file.isFile()) {
            obj.put(Flex.IS_BRANCH, false);
            build(file,obj);
        } else {
            obj.put(Flex.IS_BRANCH, true);
            JSONArray children = new JSONArray();
            for (File each : file.listFiles()) {
                reflexiveVisit(each,children);
            }
            obj.put(Flex.CHILDREN, children);
        }
        array.add(obj);
    }

}
