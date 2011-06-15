
package erwins.util.web;

import java.io.File;
import java.util.Map.Entry;

import erwins.util.lib.FileUtil;
import erwins.util.lib.security.MD5;

/** 대용량 엑셀 생성 시 메일이나 메신저로 링크하기 위한 래퍼 */
public class RequestFileMap extends RequestMap<File>{
	
	/** 요건 Rest방식일경우 */
	public RequestFileMap(String url){
		super(url);
	}
	
	public RequestFileMap(String url,String key){
		super(url,key);
	}
	
	/** link를 린턴한다. */
	public String toLink(File value){
		String key = MD5.getHashHexString(url+value.getAbsolutePath());
		map.put(key, value);
		if(parameterKey==null) return  url + key;
		else return  url + "?" + parameterKey + ":" + key;
	}
	
	/** 파일 다 지운다. 디렉토리는 남겠지? */
	public void clear(){
		for(Entry<String,File> e : map.entrySet()){
			File file = e.getValue();
			FileUtil.delete(file);
		}
		map.clear();
	}
}
