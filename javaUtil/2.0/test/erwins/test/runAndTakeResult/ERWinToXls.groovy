package erwins.test.runAndTakeResult



import static org.junit.Assert.*

import org.apache.commons.collections.map.ListOrderedMap;
import org.junit.Test

import erwins.gsample.dsl.SqlBuilder
import erwins.util.collections.MapForList;
import erwins.util.vender.apache.Poi
import erwins.util.vender.apache.PoiReaderFactory

/** ER-WIN에서 엔티티정의서 뽑기
 * 먼저 ER-WIN의 report기능으로 도메인이 나와야 한다. */
class ERWinToXls {

	private def map = new MapForList()
	private Poi pp = new Poi()
	
	void add(file,c=null){
		def p = PoiReaderFactory.instance(file)
		p[0].read {
			def name = it['Table Name']
			if(c!=null) if(!c(it)) return
			map.add name,it
		}
	}
	
	void write(file){
		def tables = []
		def comumns = []
		map.each {
			def name = it.key
			def t = new ListOrderedMap()
			t['테이블명'] = it.value[0]['Table Name']
			t['엔티티명'] = it.value[0]['Entity Name']
			t['테이블 코멘트'] = it.value[0]['Table Comment']
			tables << t
			def first = true
			it.value.each {
				def e = new ListOrderedMap()
				if(first){
					e['엔티티명'] = it['Entity Name']
					first = false
				}else e['엔티티명'] = ''
				e['어트리뷰트명'] = it['Attribute Name']
				e['도메인명'] = it['Domain Name'] == '_default_' ? '' : it['Domain Name']
				def type = it['Column Datatype']
				e['데이터타입'] = it['Column Datatype']
				def length = ''
				it['Column Datatype'].eachMatch( /\(.*\)/, { length =  it-'('-')' } )
				e['길이'] = length
				e['필수입력여부'] = it['Column Null Option'] =='NOT NULL' ? 'Y' : ''
				e['식별자정보'] = it['Column Is PK']=='Yes' ? 'PK' : ''
				e['어트리뷰트유형'] = ''
				e['설명'] = it['Column Comment']
				comumns << e
			}
		}
		pp.setListedMap '엔티티정의서', tables
		pp.setListedMap '애트리뷰트정의서', comumns
		pp.wrap().write file
	}
}
