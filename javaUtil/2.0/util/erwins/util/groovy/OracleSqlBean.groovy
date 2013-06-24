package erwins.util.groovy


import erwins.util.exception.BusinessException
import erwins.util.lib.StringUtil


public class OracleSqlBean{

	final OracleSql db;
	public OracleSqlBean(OracleSql db){
		this.db = db;
	}
	
	/** my-batis같은거 쓸때 null예외를 회피하기위해 XML에 추가해주는 용도. => 변경을 해야함 */
	public static def ORACLE_TO_JDBC_TYPE = ['NUMBER':'NUMERIC','DATE':'TIMESTAMP ','VARCHAR2':'VARCHAR','CHAR':'CHAR']
	public static def ORACLE_TO_JAVA_TYPE = ['NUMBER':'BigDecimal','DATE':'Date','VARCHAR2':'String','CHAR':'String']
	
	/** 통계성 쿼리 등 간이 SQL을 bean으로 매핑하고싶을때.  */
	public printSqlToMyBatisBean(sql){
		//def list = paging(sql,1,1); //로우넘 생겨서 안씀 ㅋ
		def list = db.list(sql);
		if(list.size()==0) throw new BusinessException('1개 이상의 결과가 있어야 합니다.')

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
	
	/** iBatiis or MyBatis용 */
	public printBatis(TABLE_NAME){
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
		sqls['SELECT'] = 'SELECT ' + cn.collect{'a.'+it}.join(', ') + " \nFROM $TABLE_NAME a "
		sqls['INSERT'] = "INSERT INTO $TABLE_NAME (" + cn.join(', ') + ') \nVALUES ('+ cn.collect { $$2(it) }.join(',')  +')'
		
		def pks = columns.findAll { it['P'] == 'P' }.collect { it.COLUMN_NAME }
		def nonPks = columns.findAll { it['P'] != 'P' }.collect { it.COLUMN_NAME }
		
		sqls['UPDATE'] = "UPDATE $TABLE_NAME SET " + cn.collect { it + ' = '+$$2(it)}.join(', ') + ' \nWHERE ' + pks.collect { it + ' = '+$$(it) }.join(' AND ')
		
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
		return sqls
	}
	
	/** java 도메인 객체 기본생성 */
	public printJava(TABLE_NAME){
		db.loadInfo("and a.TABLE_NAME = '$TABLE_NAME'").loadColumn().loadColumnKey()
		def cols = db[TABLE_NAME].COLUMNS
		def cn = cols.collect { it.COLUMN_NAME }
		
		def result = [StringUtil.getCamelize(TABLE_NAME).capitalize()]
		cols.each {
			def type = ORACLE_TO_JAVA_TYPE[it['DATA_TYPE']]
			if(type=='BigDecimal' && it['DATA_SCALE']==0) type ='Long'
			def name = StringUtil.getCamelize(it['COLUMN_NAME'])
			if(it.COMMENTS==null) it.COMMENTS = 'COMMENTS를 달아주세요'
			result << "/** $it.COMMENTS */"
			result << "private $type $name;"
		}
		return result
	}
	
}
