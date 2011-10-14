package erwins.webapp.myApp.mtgo



import static org.junit.Assert.*



import org.cyberneko.html.parsers.SAXParser

import erwins.util.lib.StringEscapeUtil
import erwins.util.lib.StringUtil
import erwins.util.temp.UrlConnection
import erwins.util.tools.GroovyXml

/** 카드 업데이트가 느려서 이제 안씀 */
class MTGO{

	def static final ROOT_URL = 'http://findmagiccards.com'

	public loadCard(cardList){
		cardList.each { card ->
			def cardName = card.cardName;
			try{
				println "=== $cardName ==="
				def name = StringEscapeUtil.escapeUrl cardName
				
				//def gxml = new GroovyXml(url:ROOT_URL+"/Search?T="+name).build()
				def xmlString =  new UrlConnection().doGet(ROOT_URL+"/Search?T="+name)
				def xml = new XmlSlurper(new SAXParser()).parseText(xmlString)
				
				def htmlList = xml.BODY.CENTER.TABLE.TR.collect{ [name:it.TD[1].A.text(),url:it.TD[1].A.'@href'] }
				def list = htmlList.findAll { it.name.endsWith(cardName)  }
				if(list.size()==0) list = htmlList.findAll { it.name.contains(cardName)  } //뒷문자 매칭이 안되면 풀매칭. ex) Life/Death
				
				list = list.collect { findDetail(it) }
				//def avgHalf = list.sum{it.money} / list.size() / 2  //평균가의 절반 이하는 이벤트 카드로 보고 제외한다.
				def oneCard = list.findAll { it.price != 0.01 }.  min { it.price } //0.01이 아닌걸로 수정
				card.type = oneCard.type;
				card.cost = oneCard.cost;
				card.edition = oneCard.edition;
				card.rarity = oneCard.rarity;
				card.price = oneCard.price;
				card.url = oneCard.url;
			}catch(e){
				println e
			}
		}
		return this
	}

	private findDetail(map){
		def inner = new GroovyXml(url:ROOT_URL+map.url).build()
		def table = inner.xml.BODY.TABLE[0].TR[0].TD[1].CENTER[1].TABLE[0]

		Card card = new Card();
		def priceText = StringUtil.getNumericStr( table.TR[12].TD[1].text() )
		card.cost = table.TR[1].TD[1].text().replaceAll(" ", "")  //공백문자가 아닌 특수문자이다. ㅅㅂ
		card.type = table.TR[2].TD[1].text().replaceAll(" ", "")
		card.edition = table.TR[5].TD[1].text().replaceAll(" ", "")
		card.rarity = table.TR[6].TD[1].text().replaceAll(" ", "")
		card.url = ROOT_URL+map.url
		if(priceText.trim()=='') card.price =  new BigDecimal('0.01') //가격표시가 되어있지 않으면 0.01로 간주한다.(이벤트일듯)
		else card.price = new BigDecimal( priceText )
		return card
	}
}
