package erwins.test.runAndSeeResult


import static org.junit.Assert.*

import java.math.RoundingMode;

import org.apache.commons.httpclient.NameValuePair
import org.cyberneko.html.parsers.SAXParser
import org.junit.Test

import erwins.util.groovy.GroovyMetaUtil
import erwins.util.lib.StringUtil
import erwins.util.web.HttpData

class BnS {
	
	@Test
	void test(){
		def 태상문 = [name:'대사막 장인의 치명 합성패',count:1,min:14,재료:[
				[name:'느티나무',count:10],
				[name:'붉은빛 합성원소',count:1],
				[name:'내열토 정제재',count:1],
			]]
		def 약왕원1 = [name:'인삼 회복약',count:5,min:33,재료:[
			[name:'인삼',count:20],
		]]
		def 약왕원2 = [name:'인삼 즉시회복약',count:5,min:33,재료:[
			[name:'인삼',count:20],
		]]
		def 도기방1 = [name:'내열토 그릇',count:5,min:33,재료:[
			[name:'내열토',count:10],
			[name:'대지의 성물',count:1],
		]]
		def 도기방2 = [name:'내열토 약병',count:5,min:33,재료:[
			[name:'내열토',count:10],
			[name:'암반수',count:1],
		]]
		def 도기방3 = [name:'느티나무 괴황지',count:5,min:33,재료:[
			[name:'내열토',count:10],
			[name:'대지의 성물',count:1],
		]]
		이윤계산출력([태상문,약왕원1,약왕원2,도기방1,도기방2,도기방3])
	}
	
	private 이윤계산출력(items) {
		items.each {
			이윤계산(it)
			출력(it)
		}
	}
	
	private 출력(item) {
		println "$item.name : 이윤:$item.이윤 / 판매가:$item.avg 재료비:$item.재료비 상점수량:$item.total";
	}
	
	private 이윤계산(item) {
		def 수수료 = 20 + 80
		calculate(item)
		item.재료.each { calculate(it) }
		item['재료비'] = item.재료.sum{ it['avg'] * it['count'] }
		item['이윤'] = item['avg'] * item['count'] - item['재료비'] - 수수료
	}
	
	private calculate(item) {
		def result = getItemAvg(item['name'])
		item['avg'] = result['avg']
		item['total'] = result['total']
	}
	
	private getItemAvg(아이템명,페이지=5) {
		HttpData d = HttpData.getSimpleClient()
		def url = 'http://bns.plaync.com/bs/market/search'
		def query = [new  NameValuePair('ct','0'),new  NameValuePair('level','0-0')
			,new  NameValuePair('stepper','forward-' + 페이지),new  NameValuePair('exact','1')
			,new  NameValuePair('sort','default-asc'),new  NameValuePair('type','SALE')
			,new  NameValuePair('grade','0'),,new  NameValuePair('q',아이템명)]
		def text = d.url(url).query(query.toArray(new NameValuePair[query.size()])).send()
		def html = new XmlSlurper(new SAXParser()).parseText(text)
		def body = html.BODY[0].DIV[1].DIV.SECTION.DIV
		def total = body.SECTION.P.text()
		def list = body.DIV[1].DIV.DIV.TABLE.TBODY.TR
		
		def result = [:]
		result['total'] = StringUtil.getDecimal(total).longValue()
		
		if(페이지 == 5 && result['total'] < 35) return getItemAvg(아이템명,1)
		
		def itemList = []
		list.each {
			def item = [:]
			item['img'] = it.TH.SPAN[0].IMG[0].attributes()['src'] //.attributes['src']
			item['level'] = it.TD[0].text()
			item['moneyStr'] = it.TD[2].DIV[0].SPAN.text()
			def mt = item['moneyStr']
			long sum = 0
			sum += toMoney('금',mt,100 * 100)
			mt = StringUtil.getLast(mt, '금');
			sum += toMoney('은',mt,100)
			mt = StringUtil.getLast(mt, '은');
			sum += toMoney('동',mt,1)
			item['money'] = sum
			itemList << item
		}
		if(itemList.size()==0) result['avg'] = 0
		else{
			long sum = 0;
			itemList.each { sum +=it['money'] }
			result['avg'] = new BigDecimal(sum / list.size()).setScale(0, RoundingMode.HALF_UP);
		}
		return result
	}
	
	private long toMoney(prefix, mt,요율) {
		def 금 =  StringUtil.getFirst(mt, prefix);
		if(금 != mt) return StringUtil.getDecimal(금).longValue() * 요율
		return 0
	}
}
