package erwins.webapp.myApp.mtgo



import static org.junit.Assert.*

import java.util.Iterator;

import erwins.util.lib.FormatUtil;
import erwins.util.lib.StringEscapeUtil
import erwins.util.lib.StringUtil;
import erwins.util.tools.GroovyXml
import erwins.util.vender.apache.PoiSheetReader;

class MTGO implements Iterable{

	def static final ROOT_URL = 'http://findmagiccards.com'
	def cards = []
	public Iterator iterator() {
		return cards.iterator()
	}

	public loadCard(cardList){
		cardList.each { cardName ->
			try{
				println "=== $cardName ==="
				def name = StringEscapeUtil.escapeUrl cardName
				def gxml = new GroovyXml(url:ROOT_URL+"/Search?T="+name).build()
				def list = gxml.xml.BODY.CENTER.TABLE.TR.collect{ [name:it.TD[1].A.text(),url:it.TD[1].A.'@href'] }
				list = list.findAll { it.name.endsWith(cardName)  }
				list = list.collect { findDetail(it) }
				def avgHalf = list.sum{it.money} / list.size() / 2  //평균가의 절반 이하는 이벤트 카드로 보고 제외한다.
				def card = list.findAll { it.money > avgHalf  }.  min { it.money }
				card.cardName = cardName;
				card.matchSize = list.size() 
				cards <<card
			}catch(e){
				println e
				cards << new Card(cardName:cardName)
			}
		}
		return this
	}

	private findDetail(map){
		def inner = new GroovyXml(url:ROOT_URL+map.url).build()
		def table = inner.xml.BODY.TABLE[0].TR[0].TD[1].CENTER[1].TABLE[0]

		Card card = new Card();
		def priceText = StringUtil.getNumericStr( table.TR[12].TD[1].text() )
		card.cost = table.TR[1].TD[1].text()
		card.type = table.TR[2].TD[1].text()
		card.edition = table.TR[5].TD[1].text()
		card.rarity = table.TR[6].TD[1].text()
		card.price = priceText
		card.url = ROOT_URL+map.url
		if(priceText.trim()=='') card.money =  new BigDecimal('0.01') //가격표시가 되어있지 않으면 0.01로 간주한다.(이벤트일듯)
		else card.money = new BigDecimal( priceText )
		return card
	}
}
