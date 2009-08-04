
package erwins.util.visitor;

import java.io.File;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import erwins.util.vender.etc.Flex;

public abstract class FileJsonVisitorForFlex implements Visitor<File>{
    protected JSONArray result = new JSONArray();
    
    /** file일경우 obj에 들어갈 내용을 기술한다 */
    protected abstract void build(File file,JSONObject obj);
    
    public void reflexiveVisit(File file,JSONArray array) {
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
    public JSONArray getResult() {
        return result;
    }
}
