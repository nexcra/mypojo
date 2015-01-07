package erwins.util.groovy


import org.apache.commons.collections.map.ListOrderedMap

import com.google.common.collect.ArrayListMultimap
import com.google.common.collect.Multimap
import com.google.common.collect.Sets

import erwins.util.text.StringUtil


public class OracleSqlBean{

	final OracleSql db;
	public OracleSqlBean(OracleSql db){
		this.db = db;
	}

	/** my-batis같은거 쓸때 null예외를 회피하기위해 XML에 추가해주는 용도. => 변경을 해야함 */
	public static def ORACLE_TO_JDBC_TYPE = ['NUMBER':'NUMERIC','DATE':'TIMESTAMP ','VARCHAR2':'VARCHAR','CHAR':'CHAR']
	/** 더 많은 속성을 보고 판단해야 한다. 일단 임시 */
	public static def ORACLE_TO_JAVA_TYPE = ['NUMBER':'Long','DATE':'Date','VARCHAR2':'String','CHAR':'String']
	//public static def ORACLE_TO_JAVA_TYPE = ['NUMBER':'BigDecimal','DATE':'Date','VARCHAR2':'String','CHAR':'String']

	public static def 업데이트무시컬럼 = ['REG_TIME', 'REG_ID', 'REG_IP']

	/** 통계성 쿼리 등 간이 SQL을 bean으로 매핑하고싶을때.  */
	public printSqlToMyBatisBean(sql){
		//def list = paging(sql,1,1); //로우넘 생겨서 안씀 ㅋ
		def list = db.list(sql);
		if(list.size()==0) throw new IllegalStateException('1개 이상의 결과가 있어야 합니다.')

		def oneRow = list[0]
		def map = [:]
		def cn = oneRow.keySet()

		def writer = new StringWriter()
		def builder = new groovy.xml.MarkupBuilder(writer)
		def className = 'Sample';
		def invoices = builder.resultMap(id:className.capitalize()+'Map',type:className.capitalize()){
			cn.each { result(property:it.camelize(),column:it) }
		}
		map['RESULT_MAP'] = writer.toString()

		def javaResult = []
		cn.each{
			def value = oneRow[it]
			def type =  value==null ? 'Object' : value.class.simpleName
			javaResult << "private $type ${it.camelize()};"
		}
		map['JAVA'] = javaResult.join('\n')

		return map
	}

	def 참조쿼리 = """ SELECT bb.TABLE_NAME,aa.COLUMN_NAME,aa.POSITION,bb.CONSTRAINT_TYPE,cc.TABLE_NAME R_TABLE_NAME,cc.COLUMN_NAME R_COLUMN_NAME
		FROM USER_CONS_COLUMNS aa JOIN USER_CONSTRAINTS bb on aa.CONSTRAINT_NAME = bb.CONSTRAINT_NAME
		left JOIN USER_CONS_COLUMNS cc ON bb.R_CONSTRAINT_NAME = cc.CONSTRAINT_NAME
		WHERE  bb.TABLE_NAME = '%s'
		ORDER BY bb.CONSTRAINT_TYPE,aa.POSITION """

	/** 테스트용 견본이며, 실제 사이트마다 복사/붙여넣기 후 수정해서 사용
	 * 조인절의 경우 뒷 테이블의 FK가 앞테이블의 PK에 반드시 존재해야 한다.
	 * domainTable에는 약어를 붙이지 않는다.
	 * ex) String sql = bean.printMybatis(Lists.newArrayList("ACCOUNT","BOARD","LOGIN_HIS","BOARD_FILE"),"BOARD");  */
	public String printMybatis(tableList,domainTable = null){

		def ABBR중복체크 =  Sets.newHashSet()
		def FROM = [];
		def 테이블이름맵 = [:]

		//첫번째 / 두번때 조인 테이블은 위치가 변경되어도 상관없어야 한다.  .. 아직 미구현

		def 테이블정보들 = tableList.collect { TABLE_NAME ->
			def 테이블정보 = [TABLE_NAME:TABLE_NAME]
			def 제약조건_딕셔너리 = db.list String.format(참조쿼리, TABLE_NAME)
			테이블정보.COLUMN_NAME = 제약조건_딕셔너리.collect{ it.COLUMN_NAME }
			테이블정보.NOT_NULL = 제약조건_딕셔너리.findAll { ['C', 'P'].contains(it.CONSTRAINT_TYPE) }.collect{ it.COLUMN_NAME }
			테이블정보.PK = 제약조건_딕셔너리.findAll { ['P'].contains(it.CONSTRAINT_TYPE) }.collect{ it.COLUMN_NAME }
			테이블정보.ABBR = StringUtil.getAbbr(TABLE_NAME)
			while(!ABBR중복체크.add(테이블정보.ABBR)){
				테이블정보.ABBR = StringUtil.plusAsLastNumber(테이블정보.ABBR,1)
			}
			테이블이름맵.put(TABLE_NAME,테이블정보.ABBR)
			//ex) [[TABLE_NAME:BOARD, COLUMN_NAME:USER_ID, POSITION:1, CONSTRAINT_TYPE:R, R_TABLE_NAME:ACCOUNT, R_COLUMN_NAME:USER_ID]]
			//테이블정보.FK = 제약조건_딕셔너리.findAll { ['R'].contains(it.CONSTRAINT_TYPE) && 테이블이름맵.containsKey(it.R_TABLE_NAME) }
			테이블정보.FK = 제약조건_딕셔너리.find { ['R'].contains(it.CONSTRAINT_TYPE) && 테이블이름맵.containsKey(it.R_TABLE_NAME) }
			//println "TABLE_NAME "+테이블정보.TABLE_NAME
			//println "PK "+테이블정보.PK
			//println "FK "+테이블정보.FK
			테이블정보.도메인테이블 = TABLE_NAME == domainTable

			if(테이블이름맵.size()==1){
				FROM << "$TABLE_NAME $테이블정보.ABBR"
			}else{
				if(테이블정보.FK == null) FROM << "$TABLE_NAME $테이블정보.ABBR ON --"
				else{
					def FK테이블약어 = 테이블이름맵[테이블정보.FK.R_TABLE_NAME]
					FROM << "JOIN $TABLE_NAME $테이블정보.ABBR ON ${FK테이블약어}.$테이블정보.FK.R_COLUMN_NAME = ${테이블정보.ABBR}.$테이블정보.FK.COLUMN_NAME"
				}
			}
			return 테이블정보
		}

		def SQL = "SELECT ";
		SQL += 테이블정보들.collect{ 테이블정보 ->
			테이블정보.COLUMN_NAME.collect {
				테이블정보.도메인테이블 ? "${테이블정보.ABBR}.$it" :  "${테이블정보.ABBR}.$it ${테이블정보.ABBR}_$it"
			}.join(" , ") }.join(", \n")
		SQL += "\nFROM " +  FROM.join("\n")

		return SQL.toString()
	}


	static final List 예약어 = ['AS']

	public String 약어(str,depth){
		def 약어 = StringUtil.getAbbr(str,depth)
		if(예약어.contains(약어)) 약어 = StringUtil.getAbbr(str,depth+1) //약어 제외
		return 약어
	}

	public Map<String,String> 약어만들기(List<String> tableList){
		Map<String,String> map = new ListOrderedMap()
		Multimap<String,String> 약어맵 =  ArrayListMultimap.create();
		tableList.each { 약어맵.put(StringUtil.getAbbr(it), it) }

		int depth = 1;
		while(true){
			if(약어맵.asMap().entrySet().findAll { it.value.size() > 1 }.size()==0) break;
			def new약어맵 = ArrayListMultimap.create();
			약어맵.asMap().entrySet().each {
				if(it.value.size() == 1 ){
					new약어맵.put(it.key,it.value[0])
				}else{
					it.value.each {
						new약어맵.put(약어(it,depth), it)
					}
				}
			}
			depth++
			약어맵 = new약어맵
		}

		약어맵.asMap().entrySet().each{ map.put(it.value[0],it.key) }

		return map
	}

	/** printMybatis2 수정본
	 *  처음게 도메인 테이블이고, 나머지가 조인 테이블들이다. */
	public String printMybatis2(tableList){

		def 테이블이름_약어맵 = 약어만들기(tableList)
		def FROM = [];
		def 테이블정보들 = []
		tableList.eachWithIndex { TABLE_NAME,index ->
			def 테이블정보 = [TABLE_NAME:TABLE_NAME]
			def 제약조건_딕셔너리 = db.list String.format(참조쿼리, TABLE_NAME)
			테이블정보.COLUMN_NAME = db.list("SELECT COLUMN_NAME FROM user_tab_columns a WHERE a.TABLE_NAME = '$TABLE_NAME' ORDER BY a.TABLE_NAME, COLUMN_ID").collect{ it.COLUMN_NAME }
			테이블정보.NOT_NULL = 제약조건_딕셔너리.findAll { ['C', 'P'].contains(it.CONSTRAINT_TYPE) }.collect{ it.COLUMN_NAME }
			//[AD_QUEUE_ID]
			테이블정보.PK = 제약조건_딕셔너리.findAll { ['P'].contains(it.CONSTRAINT_TYPE) }.collect{ it.COLUMN_NAME }
			테이블정보.ABBR = 테이블이름_약어맵[TABLE_NAME]

			//ex) [[TABLE_NAME:BOARD, COLUMN_NAME:USER_ID, POSITION:1, CONSTRAINT_TYPE:R, R_TABLE_NAME:ACCOUNT, R_COLUMN_NAME:USER_ID]]
			테이블정보.FK = 제약조건_딕셔너리.findAll { ['R'].contains(it.CONSTRAINT_TYPE) && 테이블이름_약어맵.containsKey(it.R_TABLE_NAME) }

			if(index == 0){
				FROM << "$TABLE_NAME $테이블정보.ABBR"
			}else{
				//먼저 자신의 PK가 앞에나온 테이블들의 FK에 있는지 검색한다. (일단은 전부 싱글키로 간주한다.)
				def 연결된FK =  테이블정보들.collect{ it.FK }.flatten().find{ it.R_TABLE_NAME == TABLE_NAME && it.R_COLUMN_NAME == 테이블정보.PK[0] }
				if(연결된FK == null) FROM << "$TABLE_NAME $테이블정보.ABBR ON --"
				else{
					def FK테이블약어 = 테이블이름_약어맵[연결된FK.TABLE_NAME]
					FROM << "JOIN $TABLE_NAME $테이블정보.ABBR ON ${FK테이블약어}.$연결된FK.R_COLUMN_NAME = ${테이블정보.ABBR}.${테이블정보.PK[0]}"
				}
			}
			테이블정보들 <<  테이블정보
		}

		def SQL = "SELECT ";
		SQL += 테이블정보들.collect{ 테이블정보 ->
			테이블정보.COLUMN_NAME.collect {
				테이블정보.TABLE_NAME == tableList[0] ? "${테이블정보.ABBR}.$it" :  "${테이블정보.ABBR}.$it ${테이블정보.ABBR}_$it"
			}.join(" , ") }.join(", \n")
		SQL += "\nFROM " +  FROM.join("\n")

		return SQL.toString()
	}

	/** <association columnPrefix="C1_"  property="channel"  resultMap="ChannelDao.ChannelMap" />
	 *   */
	public String printMybatisXml(tableList){
		def 테이블이름_약어맵 = 약어만들기(tableList)
		def SQL = []
		tableList.eachWithIndex { e,index ->
			if(index==0) return
				def 프로퍼티명 = StringUtil.getCamelize(e)
			def 캐피탈 = StringUtil.capitalize(프로퍼티명)
			SQL << "<association columnPrefix=\"${테이블이름_약어맵[e]}_\"  property=\"$프로퍼티명\"  resultMap=\"${캐피탈}Dao.${캐피탈}Map\" />"
		}
		return SQL.join('\n')
	}

	/** iBatiis or MyBatis용 */
	public Map<String,Object> printBatis(TABLE_NAME){
		db.loadInfo("and a.TABLE_NAME = '$TABLE_NAME'").loadColumn().loadColumnKey()
		def columns = db[TABLE_NAME].COLUMNS
		def cn = columns.collect { it.COLUMN_NAME }

		/* 알아서 바꿔주자 */
		def $$ = {'#{'+it.camelize()+'}'  }; //mybatis용
		def $$2 = { cName ->
			def type = ''
			def col =  columns.find { it['COLUMN_NAME'] == cName }
			if(col['P']==null && col['C']==null){   //null가능하다면 타입을 명시해준다. (mybatis의경우 타입이 없으면 null파라메터를 허용하지 않는다.)
				def jdbcType = ORACLE_TO_JDBC_TYPE[col['DATA_TYPE']]
				if(jdbcType!=null) type += ',jdbcType='+jdbcType
			}
			'#{'+cName.camelize()+type+'}'
		}
		/*
		 nonPks.each { cName ->
		 def col =  columns.find { it['COLUMN_NAME'] == cName }
		 def jdbcType = ORACLE_TO_JDBC_TYPE[col['DATA_TYPE']]
		 if(jdbcType==null) result(property:cName.camelize(),column:cName)
		 else result(property:cName.camelize(),column:cName,jdbcType:jdbcType)
		 }
		 */

		//def $$ = {'#'+it.camelize()+'#'  }; //ibatis용
		def sqls = [:];
		def abbr = StringUtil.getAbbr(TABLE_NAME)
		sqls['SELECT'] = 'SELECT ' + cn.collect{"${abbr}.$it"}.join(', ') + " \nFROM $TABLE_NAME $abbr "

		sqls['SELECT_PREFIX'] = ', ' + cn.collect{"${abbr}.$it ${abbr}_$it"}.join(', ')

		sqls['INSERT'] = "INSERT INTO $TABLE_NAME (" + cn.join(', ') + ') \nVALUES ('+ cn.collect { $$2(it) }.join(',')  +')'

		def pks = columns.findAll { it['P'] == 'P' }.collect { it.COLUMN_NAME }
		def nonPks = columns.findAll { it['P'] != 'P' }.collect { it.COLUMN_NAME }

		sqls['UPDATE'] = "UPDATE $TABLE_NAME SET " + cn.findAll{ !업데이트무시컬럼.contains(it) }.collect { it + ' = '+$$2(it)}.join(', ') + ' \nWHERE ' + pks.collect { it + ' = '+$$(it) }.join(' AND ')

		def mergeSql  = "MERGE INTO $TABLE_NAME USING dual ON ( " + pks.collect { it + ' = '+$$(it) }.join(' AND ')  + " )\n"
		mergeSql += "WHEN matched THEN UPDATE SET " + nonPks.collect { it + ' = '+$$2(it) }.join(', ') + '\n'
		mergeSql += "WHEN NOT matched THEN INSERT (" + cn.join(', ') + ') VALUES ('+ cn.collect { $$2(it)}.join(', ')  +')'
		sqls['MERGE'] = mergeSql

		//참고
		//http://www.jarvana.com/jarvana/view/org/mybatis/mybatis/3.0.2/mybatis-3.0.2-javadoc.jar!/org/apache/ibatis/type/JdbcType.html
		def writer = new StringWriter()
		def builder = new groovy.xml.MarkupBuilder(writer)
		def className = TABLE_NAME.camelize();
		def invoices = builder.resultMap(id:className.capitalize()+'Map',type:className.capitalize()){
			pks.each { id(property:it.camelize(),column:it) }
			nonPks.each { result(property:it.camelize(),column:it)  }
		}
		sqls['RESULT_MAP'] = writer.toString()

		def voName = className.capitalize()+'Vo'
		def staticConstructor = []

		def pkSet = columns.findAll { it['P'] == 'P' }
		def pkJava = pkSet.collect { ORACLE_TO_JAVA_TYPE[it['DATA_TYPE']] + " " + it['COLUMN_NAME'].camelize() }.join(",")

		staticConstructor << "public static $voName createByPk($pkJava){"
		staticConstructor << "  $voName vo = new $voName();"
		pkSet.each {
			def name = it['COLUMN_NAME'].camelize()
			staticConstructor << "  vo.set${name.capitalize()}(${name});"
		}
		staticConstructor << "  return vo;"
		staticConstructor << "}"
		sqls['STATIC_CONS'] = staticConstructor.join('\n')

		return sqls
	}

	/** java 도메인 객체 기본생성 */
	public printJava(TABLE_NAME){
		db.loadInfo("and a.TABLE_NAME = '$TABLE_NAME'").loadColumn().loadColumnKey()
		def cols = db[TABLE_NAME].COLUMNS
		def cn = cols.collect { it.COLUMN_NAME }

		def result = [
			StringUtil.getCamelize(TABLE_NAME).capitalize()
		]
		cols.each {
			def type = ORACLE_TO_JAVA_TYPE[it['DATA_TYPE']]
			if(type=='BigDecimal' && it['DATA_SCALE']==0) type ='Long'
			def name = StringUtil.getCamelize(it['COLUMN_NAME'])
			if(it.COMMENTS!=null) result << "/** $it.COMMENTS */"
			result << "private $type $name;"
		}
		return result
	}

}
