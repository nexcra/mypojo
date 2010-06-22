
package erwins.util.template;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import net.sf.json.JSONArray;
import erwins.util.exception.ExceptionFactory;

/**
 * 단순 json과 해시값을 가지는 컨테이너이다.
 **/
public abstract class MD5FileManagerTemplate{
	
	protected JSONArray array;
	protected final Map<String,File> eachCached = Collections.synchronizedMap(new HashMap<String,File>()) ;	
	
    public final File repository;
    
    public MD5FileManagerTemplate(File repository){
    	this.repository = repository;
    	ExceptionFactory.throwExeptionIfNotExist(repository);
    }
    
    public File getFile(String hash){
        return eachCached.get(hash);
    }
    
    public abstract JSONArray getJson();
    
    public JSONArray refresh(){
    	array = null;
    	return getJson();
    }

}
