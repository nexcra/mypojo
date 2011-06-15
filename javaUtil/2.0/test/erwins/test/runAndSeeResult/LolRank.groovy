package erwins.test.runAndSeeResult
import static org.junit.Assert.*

import java.math.RoundingMode

import org.junit.Test

import erwins.util.collections.MapForKeyList;
import erwins.util.collections.MapForList
import erwins.util.collections.MapType
import erwins.util.groovy.GroovyMetaUtil;
import erwins.util.tools.GroovyXml;
import erwins.util.vender.apache.Poi;
import erwins.util.vender.apache.PoiReaderFactory;

class LolRank {

	def dir = 'D:/LOL/'
	
	void test(){
		def batchSize = 100
		def step = 0
		while(true){
			def list = []
			[batchSize*step .. (step+1)*batchSize-1]*.each {  //[0 .. 4955]
				def gxml = new GroovyXml(url:"http://www.leagueoflegends.com/ladders/solo-5x5?page="+it).build()
				gxml.xml.BODY[0].DIV[2].DIV[0].DIV[2].TABLE[0].TBODY[0].children().each{
					def map = [:]
					map['Rank'] = it.TD[0].text()
					map['Player'] = it.TD[1].text()
					map['Wins'] = it.TD[2].text().toInteger()
					map['Losses'] = it.TD[3].text().toInteger()
					map['Rating'] = it.TD[4].text().toBigDecimal()
					map['Rate'] = map['Wins'] /  (map['Wins']+map['Losses']) * 100
					list << map
				}
				list.remove list.size()-1 //마지막 자료는 다음 첫 자료에 있음으로 삭제.  자료가 없으면 알아서 요류내면서 종료.. 귀찮 
				println "$it 번째 데이터 수집완료"
			}
			Poi p = new Poi()
			p.setListedMap 'data', list
			p.wrap().write dir+'LOL'+step
			step++
		}
	}
	@Test
	void test2(){
		def list = []
		new File(dir).listFiles().each {
			println it
			def p = PoiReaderFactory.instance it
			p[0].read{
				it['Wins'] = it['Wins'].toInteger()
				it['Losses'] = it['Losses'].toInteger()
				it['Rating'] = it['Rating'].toBigDecimal()
				it['Rate'] = it['Rate'].toBigDecimal().setScale( 2, RoundingMode.HALF_UP)
				list << it
			}
		}
		MapForKeyList mList = new MapForKeyList(MapType.ListOrderd)
		list.each {
			def key = it.Rating.toBigDecimal().setScale( -2, RoundingMode.FLOOR).setScale(0)
			mList.add key, it
		}
		def current = 0
		mList.each {
			def length = it.value.size()
			current += length
			def top = ( current /  list.size() * 100 ).setScale( 2, RoundingMode.HALF_UP)
			def rate = ( it.value.sum { it['Rate'] } / length ).setScale( 2, RoundingMode.HALF_UP)
			def wins = ( it.value.sum { it['Wins'] } / length ).setScale( 2, RoundingMode.HALF_UP)
			println  "${it.key}점대(${length}명) : 평균승율:$rate / 평균승수:$wins / 상위$top% "
		}
		def max1 = list.max { a,b -> if(a['Rating']<2000) return -1; else  a['Rate'].compareTo(b['Rate']) }
		println "2000이상 최고승율 : Rank ${max1.Rank}위 $max1.Player / Rating : $max1.Rating / 승율 : $max1.Rate / 승패 : $max1.Wins/$max1.Losses "
		def max = list.max { a,b -> if(a['Rating']<1800) return -1; else  a['Rate'].compareTo(b['Rate']) }
		println "1800이상 최고승율 : Rank ${max.Rank}위 $max.Player / Rating : $max.Rating / 승율 : $max.Rate / 승패 : $max.Wins/$max.Losses "
	}
}
