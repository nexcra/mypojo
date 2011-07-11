package erwins.util.groovy



import static org.junit.Assert.*

import org.apache.poi.hssf.usermodel.HSSFClientAnchor;
import org.hibernate.hql.ast.SqlGenerator
import org.junit.Test

import erwins.gsample.dsl.Select;
import erwins.gsample.dsl.SqlBuilder;
import erwins.util.lib.CollectionUtil;
import erwins.util.lib.ConsoleUtil;
import erwins.util.vender.apache.Poi;
import erwins.util.vender.apache.PoiReaderFactory;

/** 데이터 사전 등으로 논리명 <--> 물리명 으로 교체한다 */
class WordReplacer {
	
	private final def wordList
	private final def replaceKey //한글 -> 영문 시 한글
	private final def replaceValue //한글 -> 영문 시 영문
	private String separator = ''
	
	/** wordList는 정렬되어 있어야 한다 */
	public WordReplacer(wordList,replaceKey,replaceValue){
		this.wordList = wordList
		this.replaceKey = replaceKey
		this.replaceValue = replaceValue
	}
	
	public replace(word){
		def (temp,replaced) = [word,word]
		def matchedList = wordList.findAll { temp.contains(it[replaceKey]) }
		matchedList.each {
			if(!temp.contains(it[replaceKey])) return
			replaced = replaced.replaceAll(it[replaceKey],separator+it[replaceValue])
			temp = temp.replaceAll(it[replaceKey],'')
			if(replaced.startsWith(separator)) replaced =  replaced.substring separator.length(),replaced.length()
		}
		return [matchedList,replaced,temp]
	}

}
