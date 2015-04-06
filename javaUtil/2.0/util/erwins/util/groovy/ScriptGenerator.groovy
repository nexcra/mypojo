package erwins.util.groovy

import com.google.common.collect.Sets

import erwins.util.text.StringUtil
import groovy.sql.GroovyRowResult

/** 
 * SQL의 메타데이터를 넣어주면 간단한 스크립트를 생성해준다.
 *  */
public class ScriptGenerator {
	
	public static def ORACLE_TO_JDBC_TYPE = ['NUMBER':'NUMERIC','DATE':'TIMESTAMP','TIMESTAMP':'TIMESTAMP','VARCHAR2':'VARCHAR','CHAR':'CHAR','CLOB':'CLOB']
	public static def ORACLE_TO_JAVA_TYPE = ['NUMBER':'Long','DATE':'Date','TIMESTAMP':'Date','VARCHAR2':'String','CHAR':'String','CLOB':'String']
	
	public static def CUBRID_TO_JDBC_TYPE = ['NUMERIC':'NUMERIC','TIMESTAMP':'TIMESTAMP','DATETIME':'DATETIME','VARCHAR':'VARCHAR','CHAR':'CHAR','CLOB':'CLOB']
	public static def CUBRID_TO_JAVA_TYPE = ['NUMERIC':'Long','TIMESTAMP':'Date ','DATETIME':'Date','VARCHAR':'String','CLOB':'String']
	
	private final def 메타데이터;
	public def 업데이트무시컬럼 = []
	public def 프리픽스컬럼 = [:]
	public def 버저닝컬럼
	
	public def 매핑무시컬럼 = []
	public def 도메인패키지 = ''
	
	public def DB2JDBC타입 = ORACLE_TO_JDBC_TYPE //mybatis 변환용
	public def DB2JAVA타입 = ORACLE_TO_JAVA_TYPE
	
	ScriptGenerator(메타데이터){
		this.메타데이터 = 메타데이터
		GroovyMetaUtil.addMeta()
	}
	
	private String DB2JDBC(DB타입){
		def 타입 = DB2JDBC타입.find{ k,v -> DB타입.startsWith(k) } 
		if(타입==null) println "DB2JDBC 타입이 존재하지 않습니다. $DB타입"
		타입.value
	}
	
	private String DB2JAVA(DB타입){
		def 타입 = DB2JAVA타입.find{ k,v -> DB타입.startsWith(k) }
		if(타입==null) println "DB2JAVA 타입이 존재하지 않습니다. $DB타입"
		타입.value
	}
	
	def 마이바티스변환 = { 컬럼 -> 
		def JAVA필드 = 컬럼['컬럼명'].camelize()
		def 변환프리픽스 =  프리픽스컬럼[JAVA필드]
		if(변환프리픽스 != null){
			JAVA필드 = 변환프리픽스 + '.' + JAVA필드
		}
		
		def 컬럼낫널 = 컬럼['NOT_NULL']
		def JDBC타입추가 = ''
		if(!컬럼낫널){
			def JDBC타입 = DB2JDBC 컬럼['타입']
			JDBC타입추가 += ',jdbcType='+JDBC타입
		}
		'#{'+JAVA필드+JDBC타입추가+'}'
	};
	
	public void 스크립트생성(){
		
		메타데이터.each { 테이블 -> 
			def 테이블명 = 테이블['테이블명']
			def 컬럼들 = 테이블['컬럼들'];
			def PK컬럼들 = 컬럼들.findAll { it['PK'] }
			def 일반컬럼들 = 컬럼들.findAll { !it['PK'] }
			def 컬럼명셑 = 컬럼들.collect{ it['컬럼명'] }
			def 테이블약어 = StringUtil.getAbbr(테이블명)
			
			테이블['SELECT'] = 'SELECT ' + 컬럼명셑.collect{ "${테이블약어}.$it"}.join(', ') + " \nFROM $테이블명 $테이블약어 "
			테이블['INSERT'] = "INSERT INTO $테이블명 (" + 컬럼명셑.join(', ') + ') \nVALUES ('+ 컬럼들.collect { 마이바티스변환(it) }.join(',')  +')'
			테이블['INSERT'] = 테이블['INSERT'].toString()
			테이블['UPDATE'] = "UPDATE $테이블명 SET " + 일반컬럼들.findAll{ !업데이트무시컬럼.contains(it['컬럼명']) }.collect { it['컬럼명'] + ' = '+마이바티스변환(it) }.join(', ') + ' \nWHERE ' + PK컬럼들.collect { it['컬럼명'] + ' = '+마이바티스변환(it) }.join(' AND ')
			
			def 낙관적잠금 = ''
			if(버저닝컬럼!=null && 컬럼명셑.contains(버저닝컬럼)) 낙관적잠금 = ' AND ' + 버저닝컬럼 + ' = #{'+버저닝컬럼.camelize()+'}' 
			
			테이블['UPDATE'] = 테이블['UPDATE'].toString() + 낙관적잠금
				
			def mergeSql  = "MERGE INTO $테이블명 USING dual ON ( " + PK컬럼들.collect { it['컬럼명'] + ' = '+마이바티스변환(it) }.join(' AND ')  + " )\n"
			mergeSql += "WHEN matched THEN UPDATE SET " + 일반컬럼들.collect { it['컬럼명'] + ' = '+마이바티스변환(it) }.join(', ') + '\n'
			mergeSql += "WHEN NOT matched THEN INSERT (" + 컬럼명셑.join(', ') + ') VALUES ('+ 컬럼들.collect { 마이바티스변환(it)}.join(', ')  +')'
			테이블['MERGE'] = mergeSql
			테이블['MERGE'] = 테이블['MERGE'].toString()
			
			//참고
			//http://www.jarvana.com/jarvana/view/org/mybatis/mybatis/3.0.2/mybatis-3.0.2-javadoc.jar!/org/apache/ibatis/type/JdbcType.html
			def writer = new StringWriter()
			def builder = new groovy.xml.MarkupBuilder(writer)
			def 클래스명 = 테이블명.camelize();
			def invoices = builder.resultMap(id:클래스명.capitalize()+'Map',type:도메인패키지+'.'+클래스명+'.'+클래스명.capitalize()+'Vo'){
				PK컬럼들.collect{ it['컬럼명'] }.each { id(property:it.camelize(),column:it) }
				일반컬럼들.collect{ it['컬럼명'] }.findAll{ !매핑무시컬럼.contains(it.camelize()) }.each { result(property:it.camelize(),column:it)  }
			}
			테이블['RESULT_MAP'] = writer.toString()
			
			
			def VO명 = 클래스명.capitalize()+'Vo'
			def 생성자명 = 'of'
			def 생성자파라메터 = PK컬럼들.collect { DB2JAVA(it['타입']) + " " + it['컬럼명'].camelize() }.join(",")

			def staticConstructor = []
			staticConstructor << "public static $VO명 ${생성자명}($생성자파라메터){"
			staticConstructor << "  $VO명 vo = new ${VO명}();"
			PK컬럼들.collect{ it['컬럼명'].camelize() }.each { staticConstructor << "  vo.set${it.capitalize()}(${it});" }
			staticConstructor << "  return vo;"
			staticConstructor << "}"
			테이블['STATIC_CONS'] = staticConstructor.join('\n')

			def 자바빈 = []			
			컬럼들.collect {
				def type = DB2JAVA(it['타입'])
				def name = StringUtil.getCamelize(it['컬럼명'])
				if(매핑무시컬럼.contains(name)) return
				if(it['COMMENTS']!=null) 자바빈 << "/** $it.COMMENTS */"
				자바빈 << "private $type $name;"
				//자바빈 << ""
			}
			
			테이블['JAVA_BEAN'] = 자바빈.join("\n")
		}
		
	}
	
	/**
	 * 테스트용 견본이며, 실제 사이트마다 복사/붙여넣기 후 수정해서 사용
	 * 조인절의 경우 뒷 테이블의 FK가 앞테이블의 PK에 반드시 존재해야 한다.
	 * domainTable에는 약어를 붙이지 않는다.
	 * ex)
	 * 	   def 메타데이터 = OracleMetadata.getMedadata(sql, "BATCH_JOB","BATCH_STEP")
	 *     메타데이터.find{ it['테이블명'] == 'BATCH_JOB' }['도메인테이블'] = true
	 *     String joinSql = printMybatisJoin(sql,메타데이터)
	 * */
	public String printMybatisJoin(List 메타데이터){
		
		def 약어중복체크 =  Sets.newHashSet()
		메타데이터.each {
			while(!약어중복체크.add(it['약어'])){
				it['약어'] = StringUtil.plusAsLastNumber(it['약어'],1)
			}
		}
		
		def 테이블찾기 = { 테이블명 ->  return 메타데이터.find{ it['테이블명'] == 테이블명 } }
		
		def 조인절SQL = [];
		
		def 선행테이블 = null

		메타데이터.each{ 테이블정보 ->
			def 테이블명 = 테이블정보['테이블명']
			def 약어 = 테이블정보['약어']
			if(조인절SQL.isEmpty()){
				조인절SQL << "$테이블명 $약어"
				선행테이블 = 테이블정보  //첫번째 / 두번때 조인 테이블은 위치가 변경되어도 상관없어야 한다. 대충 하드코딩
			}else{
				Collection FK_SET = 테이블정보['FK_SET'] //[COLUMN_NAME:JOB_NAME, R테이블:BATCH_JOB, R컬럼:JOB_NAME] 이런식
				if(FK_SET.isEmpty() && 조인절SQL.size()==1)  FK_SET = 선행테이블['FK_SET']
				if(FK_SET.isEmpty()) {
					조인절SQL << "$테이블명 $약어 ON --"
					return
				}
				def 조인SQL = "JOIN $테이블명 $약어 ON "
				
				FK_SET.each {  FK ->
					def FK테이블 = 테이블찾기(FK['R테이블'])
					if( FK테이블 == 테이블정보 ) FK테이블 = 선행테이블  //둘이 같다면 조인순서때문에 바뀐거..
					if(FK테이블==null) println '이러기없기~'
					def FK테이블약어 = FK테이블['약어']
					조인SQL += "$FK테이블약어.$FK.R컬럼 = $약어.$FK.COLUMN_NAME"
				}
				조인절SQL << 조인SQL
			}
		}
		
		def SQL = "SELECT ";
		SQL += 메타데이터.collect{ 테이블정보 ->
			테이블정보['컬럼들'].collect {
				def 컬럼명 = it['컬럼명']
				테이블정보['도메인테이블'] ? "${테이블정보.약어}.$컬럼명" :  "${테이블정보.약어}.$컬럼명 AS \"${테이블정보.약어}_$컬럼명\""
			}.join(" , ") }.join(", \n")
		SQL += "\nFROM " +  조인절SQL.join("\n")

		return SQL.toString()
	}
	
	/** 통계 등 간이 SQL을 bean으로 매핑 */
	public void printSqlMappingScript(List<GroovyRowResult> list){
		
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
		
		println writer.toString()

		def javaResult = []
		cn.each{
			def value = oneRow[it]
			def type =  value==null ? 'Object' : value.class.simpleName
			javaResult << "private $type ${it.camelize()};"
		}
		
		println javaResult.join('\n')
		
	}
	
	
	

}
