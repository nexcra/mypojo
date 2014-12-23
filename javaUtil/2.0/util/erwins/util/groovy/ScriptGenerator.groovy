package erwins.util.groovy

import erwins.util.text.StringUtil

/** 
 * SQL의 메타데이터를 넣어주면 간단한 스크립트를 생성해준다.
 *  */
public class ScriptGenerator {
	
	public static def ORACLE_TO_JDBC_TYPE = ['NUMBER':'NUMERIC','DATE':'TIMESTAMP ','VARCHAR2':'VARCHAR','CHAR':'CHAR']
	public static def ORACLE_TO_JAVA_TYPE = ['NUMBER':'Long','DATE':'Date','VARCHAR2':'String','CHAR':'String']
	
	public static def CUBRID_TO_JDBC_TYPE = ['NUMERIC':'NUMERIC','TIMESTAMP':'TIMESTAMP','DATETIME':'DATETIME','VARCHAR':'VARCHAR','CHAR':'CHAR','CLOB':'CLOB']
	public static def CUBRID_TO_JAVA_TYPE = ['NUMERIC':'Long','TIMESTAMP':'Date ','DATETIME':'Date','VARCHAR':'String','CLOB':'String']
	
	private final def 메타데이터;
	public def 업데이트무시컬럼 = []
	public def 프리픽스컬럼 = [:]
	
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
			테이블['UPDATE'] = 테이블['UPDATE'].toString()
				
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
			def invoices = builder.resultMap(id:클래스명.capitalize()+'Map',type:클래스명.capitalize()){
				PK컬럼들.collect{ it['컬럼명'] }.each { id(property:it.camelize(),column:it) }
				일반컬럼들.collect{ it['컬럼명'] }.each { result(property:it.camelize(),column:it)  }
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
				if(it['COMMENTS']!=null) 자바빈 << "/** $it.COMMENTS */"
				자바빈 << "private $type $name;"
				//자바빈 << ""
			}
			
			테이블['JAVA_BEAN'] = 자바빈.join("\n")
		}
		
	}
	
	
	

}
