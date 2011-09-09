package erwins.webapp.myApp.mtgo



import static org.junit.Assert.*

import org.junit.Test

import erwins.util.groovy.GroovyMetaUtil
import erwins.util.lib.StringUtil
import erwins.util.vender.apache.Poi
import erwins.util.vender.apache.PoiReader;

class MTGOTest {

	static{
		GroovyMetaUtil.addMeta()
	}

	@Test
	void test2(){
		def dir = 'C:/DATA/'
		def file = 'mtgo'
		def list = new File(dir,file+'.csv').readLines()
		list.remove(0)
		list = list.collect { StringUtil.getFirstAfter(it, ',').replaceAll('"','') }
		MTGO o = new MTGO().loadCard(list)
		//MTGO o = new MTGO().loadCard(['Reassembling Skeleton'])
		Poi p = new Poi();
		p.addSheet('카드리스트', ['이름','타입','희귀도','발비','가격($)','수량','에디션','판본수','URL'])
		o.each { p.addValues(it.cardName,it.type,it.rarity ,it.cost ,it.price ,it.quantity ,it.edition ,it.matchSize ,it.url) }
		p.wrap().write(dir+file+'.xls')
	}

	
}
