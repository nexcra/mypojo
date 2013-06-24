
package erwins.util.template;

import java.io.File;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import erwins.util.exception.ExceptionUtil;
import erwins.util.vender.etc.Flex;

/** 해당 디렉토리를 Flex tree로 보여줄때 */
@Deprecated
public abstract class FileJsonTemplateForFlex{
	
	public final File repository;
	
	public FileJsonTemplateForFlex(File repository){
		ExceptionUtil.throwIfNotExist(repository);
		this.repository = repository;
	}
    
	/** file일경우에만 호출된다. obj에 들어갈 내용을 기술한다 */
    protected abstract void build(File file,JSONObject obj);

    private JSONArray cachedResult;
    
    public void refresh() {
    	cachedResult = null;
    }
    
    public JSONArray visit() {
    	if(cachedResult==null){
    		cachedResult = new JSONArray();
    		reflexiveVisit(repository,cachedResult);
    	}
    	return cachedResult;
    }
    
    public JSONArray visitIgnoreRoot() {
    	if(cachedResult==null){
    		cachedResult = new JSONArray();
    		for (File each : repository.listFiles()) {
                reflexiveVisit(each,cachedResult);
            }	
    	}
        return cachedResult;
    }
    
    protected void reflexiveVisit(File file,JSONArray array) {
        if (file.isHidden()) return;
        JSONObject obj = new JSONObject();
        obj.put(Flex.LABEL, file.getName());

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
