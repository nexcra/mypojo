package erwins.util.vender.apache


import groovy.sql.GroovyRowResult;

/** SQLUtil은 싱글톤만 사용하니까 따로 뺐다.  */
public class GroovyMetaUtil{
	
	/** 이게 더 깔끔한듯 */
	public static void hashMap(){
		HashMap.metaClass."plus" = { key,value ->
			def org = delegate.get(key)
			if(org==null) org = 0;
			delegate.put key, org+value
		}
		HashMap.metaClass."plus" = { delegate.plus it , 1 }
	}
	
	public static void file(){
		/** 해당 확장자의 파일을 모두 가져온다 */
		File.metaClass."listAll" = {endsWith ->
			def files = [];
			delegate.eachFileRecurse{if(it.file) files << it};
			files.findAll { it.name.toString().endsWith(endsWith) }
		}
	}
	
	/**
	 * List<Map> 데이터들의 특정값 중복 체크. 
	 * 이후 map.findAll { it.value==1 } 등을 사용하자. -> 테스트 필요
	 *  keyList로 :를 사용해 연결하자. */
	public static Map duplicated(list,key){
		def map = [:]
		list.each{ map.plus it[key]}
		return map
	}
	
	/** 미칠듯이 편하다. 이거 굿! */
	public static void groovyRowResult(nullString){
		GroovyRowResult.metaClass."getNullSafe"  = { key ->
			if(delegate.containsKey(key)) return delegate[key]
			else return nullString
		}
	}
	
	
	
	
}

