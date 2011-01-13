
package erwins.util.template;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import net.sf.json.JSONObject;
import erwins.util.lib.FileUtil;
import erwins.util.lib.security.MD5;

public class MD5FileManagerTemplate extends FileJsonTemplateForFlex{
	
	public static final String HASH_CODE = "hashCode";
	public static final String LENGTH = "length";
	
	protected final Map<String,File> eachCached = Collections.synchronizedMap(new HashMap<String,File>()) ;	
    
    public MD5FileManagerTemplate(File repository){
    	super(repository);
    }
    
    public File getFile(String hash){
        return eachCached.get(hash);
    }

	@Override
	protected void build(File file, JSONObject obj) {
		String hashCode = MD5.getHashHexString(file.getAbsolutePath());
    	obj.put(HASH_CODE, hashCode);
    	eachCached.put(hashCode, file);
    	obj.put(LENGTH, FileUtil.getMb(file));
    	build2(file,obj, hashCode);
	}
	
	/** 사용자 정의시 이것을 오버라이딩 할것. */
	protected void build2(File file,JSONObject obj,String hashCode){
		//아무것도 하지 않는다.
	}


}
