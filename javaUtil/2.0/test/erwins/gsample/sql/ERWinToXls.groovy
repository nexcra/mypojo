package erwins.gsample.sql



import static org.junit.Assert.*

import org.apache.commons.collections.map.ListOrderedMap

import erwins.util.collections.MapForList
import erwins.util.collections.MapType
import erwins.util.text.StringUtil
import erwins.util.vender.apache.Poi
import erwins.util.vender.apache.PoiReaderFactory


/** ER-WIN에서 엔티티정의서 뽑기
 * 먼저 ER-WIN의 report기능으로 도메인이 나와야 한다. 이는 csv로 나오는데 이를  xls로 저장하자.
 * ER-WIN에서 도메인 입력시 물리가 논리명 입력한계보다 작다. 따라서 물리명은 짤리는수가 있음으로 논리명을 사용한다.
 * Column Null Option : NOT NULL or NULL 
 * Column Is PK : Yes or No
 * Domain Name : _default_  
 * 
 * 오래되서 뭔지 모르겠다. 이걸 다시 쓸날이 오진 않을듯
 * */
@Deprecated
public class ERWinToXls implements Iterable {
	
	def tables = new MapForList(MapType.ListOrderd)
	
	public Iterator iterator(){
		return tables.iterator()
	}
	/** ERD의 서브젝트 에어리어 정보  Table/View Name / Subject Area Name */
	public ERWinToXls addSbject(fileName){
		def sbject = PoiReaderFactory.instance(fileName)[0].read().findAll { it['Subject Area Name'] != '<Main Subject Area>' }
		tables.each {
			def entity = it.value[0] //첫번째 놈한테만 담는다.
			if(sbject!=null) entity['subject'] = sbject.findAll { it['Table/View Name'] == entity['Table Name']  }
				.collect { it['Subject Area Name'] }.join(',')
		}
		return this
	}
	public getAt(String tableName) {
		return tables.find { it['Table Name'] == tableName }
	}
	
	def toKr =  ['Table Name':'테이블명','Column Name':'컬럼명','Attribute Name':'컬럼논리명','Column Datatype':'데이터타입'
		,'Column Null Option':'필수입력여부','Column Is PK':'주키여부','Logical Domain Name':'도메인식별','Column Comment':'컬럼코멘트']
	
	def nullDomain =  ['_default_','<default>']
	
	/** 한글명으로 변신.. 컬럼만.   리턴은 리스트로 */
	public convert(){
		def result = []
		tables.each{  it.value.each { 
			def map = it.mapping(toKr)
			if(map['도메인식별']==null) map['도메인식별'] = StringUtil.getLast(it['Domain Name'], '?') //ER-WIN의 버그 수정 
			map['필수입력여부'] = map['필수입력여부'] =='NOT NULL' ? 'Y' : 'N'
			map['주키여부'] = map['주키여부']=='Yes' ? 'Y' : 'N'
			result << map
		}}
		return result
	}
	
	/** ex)ERWinToXls erwin = new ERWinToXls(ROOT+'기후자료',{ it['Entity Name'].startsWith('DB:') }) */
	public ERWinToXls(fileName,filter=null){
		def list = PoiReaderFactory.instance(fileName)[0].read()
		if(filter!=null) list = list.findAll { filter(it)  }
		list.each { tables.add it['Table Name'] , it  }
	}

	void toXls(fileName){
		def 엔티티정의서 = []
		def 애트리뷰트정의서 = []
		Poi pp = new Poi()
		tables.each {
			def entity = it.value[0]
			엔티티정의서 <<  new ListOrderedMap().merge(entity,['Table Name','Entity Name','Table Comment','subject'])
			it.value.each {
				애트리뷰트정의서 << new ListOrderedMap().merge(it,
					['Table Name','Entity Name','Column Name','Attribute Name','Column Comment','Domain Name'
						,'Column Datatype','Column Null Option','Column Is PK'])
			}
		}
		pp.setListedMap '엔티티정의서', 엔티티정의서
		pp.setListedMap '애트리뷰트정의서', 애트리뷰트정의서
		pp.getMerge(1).setAbleRow(0).setAbleCol(0,1).merge();
		pp.wrap().write fileName
	}
	
	void toXlsKr(fileName){
		def 엔티티정의서 = []
		def 애트리뷰트정의서 = []
		Poi pp = new Poi()
		tables.each {
			def entity = it.value[0]
			엔티티정의서 << new ListOrderedMap().merge(entity,['Table Name','Entity Name','Table Comment'])
			it.value.each {
				def e = new ListOrderedMap()
				e['테이블명'] = it['Table Name']
				e['엔티티명'] = it['Entity Name']
				e['컬럼명'] = it['Attribute Name']
				e['어트리뷰트명'] = it['Attribute Name']
				e['설명'] = it['Column Comment']
				//it['Column Datatype'].eachMatch( /\(.*\)/, { length =  it-'('-')' } )
				e['도메인식별'] = nullDomain.any { it==it['Logical Domain Name'] } == '_default_' ? '' : it['Logical Domain Name']
				e['데이터타입'] = it['Column Datatype']
				e['필수입력여부'] = it['Column Null Option'] =='NOT NULL' ? 'Y' : 'N'
				e['식별자정보'] = it['Column Is PK']=='Yes' ? 'Y' : 'N'
				애트리뷰트정의서 << e 
			}
		}
		pp.setListedMap '엔티티정의서', 엔티티정의서
		pp.setListedMap '애트리뷰트정의서', 애트리뷰트정의서
		pp.getMerge(1).setAbleRow(0).setAbleCol(0,1).merge();
		pp.wrap().write fileName
	}
}
