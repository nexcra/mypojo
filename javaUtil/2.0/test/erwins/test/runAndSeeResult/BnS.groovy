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
		def 철무방 = []
		def 만금당 = []
		def 태상문 = []
		def 성군당 = []
		def 도기방 = []
		def 약왕원 = []
		def 일미문 = []
		태상문 << [name:'느티나무',count:10,min:33,재료:[]]
		태상문 << [name:'대사막 장인의 치명 합성패',count:1,min:14,재료:[
				[name:'느티나무',count:10],
				[name:'붉은빛 합성원소',count:1],
				[name:'내열토 정제재',count:1],
		]]
		태상문 << [name:'대사막 명공의 치명 합성패 상자',count:1,min:14,재료:[
			[name:'대사막 장인의 치명 합성패',count:1],
			[name:'냉혈귀의 가시',count:1],
			[name:'고급 내열토 정제재',count:1],
			[name:'영석',count:6],
			[name:'꿩 뼈',count:3],
	    ]]
		성군당 << [name:'굴 자개',count:5,min:14,재료:[
				[name:'굴 껍데기',count:20],
				[name:'암반수',count:1],
				[name:'영석',count:6],
				[name:'냉혈귀의 가시',count:1],
				[name:'목련',count:1],
			]]
		성군당 << [name:'십련정강 봉인해제 부적',count:10,min:33,재료:[
			[name:'굴 자개',count:5],
			[name:'느티나무 괴황지',count:5],
		]]
		약왕원 << [name:'인삼',count:10,min:33,재료:[]]
		약왕원 << [name:'인삼 회복약',count:5,min:33,재료:[
			[name:'인삼',count:20],
		]]
		약왕원 <<  [name:'인삼 진액',count:5,min:33,재료:[
			[name:'인삼',count:20],
			[name:'암반수',count:1],
			[name:'영석',count:6],
			[name:'사막의 짐승 내단',count:1],
			[name:'메기 기름',count:1],
		]]
		약왕원 <<  [name:'인삼 즉시회복약',count:5,min:33,재료:[
			[name:'인삼 진액',count:5],
			[name:'내열토 약병',count:10],
		]]
		도기방 <<  [name:'내열토 그릇',count:5,min:33,재료:[
			[name:'내열토',count:10],
			[name:'대지의 성물',count:1],
		]]
		도기방 << [name:'내열토 약병',count:5,min:33,재료:[
			[name:'내열토',count:10],
			[name:'암반수',count:1],
		]]
		도기방 << [name:'느티나무 괴황지',count:5,min:33,재료:[
			[name:'내열토',count:10],
			[name:'대지의 성물',count:1],
		]]
		도기방 << [name:'내열토 정제재',count:5,min:33,재료:[
			[name:'내열토',count:10],
			[name:'암반수',count:1],
		]]
		도기방 << [name:'고급 내열토 정제재',count:5,min:33,재료:[
			[name:'내열토',count:20],
			[name:'느티나무 수액',count:1],
			[name:'영석',count:6],
			[name:'냉혈귀의 가시',count:1],
		]]
		println '====태상문====' ;  이윤계산출력(태상문)
		println '====성군당====' ;  이윤계산출력(성군당)
		println '====약왕원====' ;  이윤계산출력(약왕원)
		println '====도기방====' ;  이윤계산출력(도기방)
		//println 도기방.last()['재료']
		
	}
	
	private 이윤계산출력(items) {
		items.each {
			이윤계산(it)
			출력(it)
		}
	}
	
	private 출력(item) {
		println "$item.name : 이윤:$item.이윤 / 개당판매가:$item.avg 재료비:$item.재료비 상점수량:$item.total";
	}
	
	private 이윤계산(item) {
		def 수수료 = 20 + 80
		def 경매장판매수수료 = 0.95
		calculate(item)
		item.재료.each { calculate(it) }
		item['재료비'] = item.재료.sum(0){ it['avg'] * it['count'] }
		
		item['이윤'] = Math.round((item['avg'] * item['count']) * 경매장판매수수료) - item['재료비'] - 수수료
	}
	
	private calculate(item) {
		def result = getItemAvg(item['name'])
		item['avg'] = result['avg']
		item['total'] = result['total']
	}
	
	private getItemAvg(아이템명,페이지=2) {
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
		
		if(페이지 == 2 && result['total'] < 14) return getItemAvg(아이템명,1)
		
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
		if(result['avg']<=0) println '경고 금액0 : ' + 아이템명
		return result
	}
	
	private long toMoney(prefix, mt,요율) {
		def 금 =  StringUtil.getFirst(mt, prefix);
		if(금 != mt) return StringUtil.getDecimal(금).longValue() * 요율
		return 0
	}
}
