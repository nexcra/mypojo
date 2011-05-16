package erwins.test.runAndSeeResult


import org.cyberneko.html.parsers.SAXParser 
import org.junit.Test;



import static org.junit.Assert.*;

class SC2Rank {
	
	@Test
	void test(){
		HashMap.metaClass."plus" = { key,value ->
			def org = delegate.get(key)
			if(org==null) org = 0;
			delegate.put key, org+value
		}
		HashMap.metaClass."plus" = { delegate.plus it , 1 }
		eachData '한국','kr', 1
		eachData '북미','us', 1
		eachData '남미','us', 2
		eachData '유럽','eu', 1
		eachData '러시아','eu', 2
		eachData '대만','tw', 2
		eachData '동남아시아','sea', 1
		eachData '중국',null,null
	}
	
	private eachData(name,nation,no) {
		println "==== $name ===="
		def url = "http://${nation}.battle.net/sc2/ko/ladder/${no}/grandmaster"
		if(no==null) url = 'http://www.battlenet.com.cn/sc2/zh/ladder/1/grandmaster'
		def text = new URL(url).openStream().getText('UTF-8')
		def html = new XmlSlurper(new SAXParser()).parseText(text)
		def list = html.BODY.DIV.DIV[1].DIV.DIV.DIV.DIV[2].TABLE.TBODY.TR;
		
		def countMap = new HashMap()
		list.collect {
			boolean isBlank = it.TD[7]==''
			isBlank ? it.TD[6] : it.TD[7]
		}.each { countMap.plus it.toString() }
		println "종족수  : $countMap"
		
		def valueMap = new HashMap()
		list.collect {
			boolean isBlank = it.TD[7]==''
			isBlank ? [race:it.TD[6],value: it.TD[3]] : [race:it.TD[7],value: it.TD[4]]
		}.each { valueMap.plus it.race.toString() , it.value.toString().toInteger() }
		println "점수합계 : $valueMap"
	}
}
