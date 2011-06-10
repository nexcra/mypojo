package erwins.util.vender.apache


import erwins.util.lib.StringUtil
import groovy.sql.GroovyRowResult

/** SQLUtil은 싱글톤만 사용하니까 따로 뺐다.  */
public class GroovyMetaUtil{
	
	public static void addMeta(){
		hashMap()
		file()
		stringArray()
		list()
		groovyRowResult 'N/A'
	}
	
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
	
	/** 이외 유용한것들
	 * min / max / split / sort / sum / unique
	 * assert [0:[2,4,6], 1:[1,3,5]] == [1,2,3,4,5,6].groupBy { it % 2 }
	 *   */
	public static void list(){
		/** List<Map> 을 Map<List<Map>> 으로 변경 */
		ArrayList.metaClass."toMap" = { key ->
			def map = [:];
			delegate.each { map.put it[key],it  }
			return map
		}
		/** 그냥 split은 true / false 구조로 무조건 2개로 나눈다. 이는 그것을 개량한것이다.
		* separator이 true로 나올때마다 하나의 리스트를 추가한다.
		* List<List>의 구조를 가진다. 첫번째 separator는 무조건 true가 나와야 한다. */
	   ArrayList.metaClass."splitByFirst" = { separator ->
		   def result = []
		   def nowList
		   delegate.each {
			   if(separator(it)){
				   nowList = []
				   result << nowList
			   }
			   nowList << it
		   }
		   return result
	   }
		ArrayList.metaClass."containsAny" = { value ->
			delegate.findAll { it==value }.size() > 0
		}
		ArrayList.metaClass."containsAny" = { key, value ->
		delegate.findAll { it[key]==value }.size() > 0
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
	/** 추가하진 않는다. 나중에 하든지 하자. */
	public static void string(){
		String.metaClass.swapCase = {
			def sb = new StringBuffer()
			delegate.each {
				sb << (Character.isUpperCase(it as char) ? Character.toLowerCase(it as char) : 
						Character.toUpperCase(it as char))
			}
			sb.toString()
		}
	}
	
	public static void stringArray(){
		String[].metaClass."insertSql"  = {tableName ->
			def parameter = StringUtil.iterateStr( '?', ',', delegate.length)
			def INSERT = "INSERT INTO $tableName ( ${delegate.join(',')} ) VALUES ( ${parameter} )"
			return INSERT
		}
	}
	
	
	
	
}

