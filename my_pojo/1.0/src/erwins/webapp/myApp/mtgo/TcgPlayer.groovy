package erwins.webapp.myApp.mtgo

import static org.junit.Assert.*

import org.cyberneko.html.parsers.SAXParser

import erwins.util.lib.StringEscapeUtil
import erwins.util.lib.StringUtil

class TcgPlayer{

	def static final ROOT_URL = 'http://magic.tcgplayer.com'
	def static final PATH = '/db/magic_single_card.asp?cn='
	def static final encode = 'UTF-8'

	public static loadCard(List cardList){
		cardList.each {Card card ->
			try{
				println "=== $card.cardName ==="
				def name = StringEscapeUtil.escapeUrl card.cardName
				card.url = ROOT_URL+PATH+name;
				def text = new URL(card.url).openStream().getText(encode)
				//Form 태그가 잘못 들어가있어서 ㅅㅂ 파싱할때 깨진다. 따라서 삭제해주자.
				text = text.replaceAll('<FORM method="POST" action="/db/search_result.asp" name="inputForm" id="inputForm">','');
				text = text.replaceAll('</FORM>','');
				def xml = new XmlSlurper(new SAXParser()).parseText(text)
				def main = xml.BODY.TABLE.TR.TD.TABLE[2].TR.TD[1].DIV.TABLE[1]
				def priceText =StringUtil.getNumericStr(  main.TR[0].TD.DIV.TABLE.TR.TD[2] )
				card.price = new BigDecimal(priceText);
				def infoTable =  main.TR[1].TD[0].TABLE
				card.edition = infoTable.TR[0].TD[1].A[0].FONT
				card.cost =  infoTable.TR[1].TD[1].FONT.toString().trim()
				card.type = infoTable.TR[3].TD[1].FONT.text().replaceAll(' ','').replaceAll('\n','').trim()
				//레어도의 위치가 바뀐다.
				card.rarity =  infoTable.TR[5].TD[1].FONT.toString().trim()
				if(card.rarity=='') card.rarity = infoTable.TR[4].TD[1].FONT.toString().trim()
				
				card.imageUrl = ROOT_URL+main.TR[1].TD[2].TABLE.TR[1].TD[1].IMG.'@src'  //width/height : 200/285
			}catch(e){
				println e
			}
		}
	}

}
