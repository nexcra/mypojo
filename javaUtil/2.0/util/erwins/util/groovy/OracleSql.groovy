package erwins.util.groovy


import java.sql.Timestamp
import java.util.List

import org.apache.poi.hssf.record.formula.functions.T

import erwins.util.collections.MapForList
import erwins.util.exception.BusinessException
import erwins.util.lib.FormatUtil
import erwins.util.lib.StringUtil
import erwins.util.tools.TextFile
import erwins.util.valueObject.ShowTime
import erwins.util.vender.apache.Poi
import erwins.util.vender.apache.PoiReaderFactory
import erwins.util.vender.etc.OpenCsv
import groovy.sql.Sql

/** 표준형인 date는 년월일만 지원한다. 오라클타입을 TimeStamp로 해야한다.
 * 강제로 date를 TimeStamp로 변경하려면 -Doracle.jdbc.V8Compatible=true 를 JVM옵션으로 주면된다. (v1.9~)
 * or originalDataSource.addConnectionProperty( "oracle.jdbc.V8Compatible", "true" ); 
 * table의 스키마를 담기위해 별도의 class로 변경했다.
 * 그루비 리절트로우의 경우 전체 리스트맵을 모두 바꾸면 정상변경되나 일부만 바뀌면 해당 프로퍼티를 찾을 수 없다는 메세지가 나온다. 주의! */
public class OracleSql extends AbstractSql implements Iterable{

	public static OracleSql instance(ip,sid,id,pass){
		OracleSql oracleSql = new OracleSql()
		oracleSql.db = Sql.newInstance("jdbc:oracle:thin:@$ip:1521:$sid",id,pass,'oracle.jdbc.driver.OracleDriver');
		oracleSql.db.connection.autoCommit = false
		return oracleSql;
	}
	public static OracleSql instance(url,id,pass){
		OracleSql oracleSql = new OracleSql()
		oracleSql.db = Sql.newInstance(url,id,pass,'oracle.jdbc.driver.OracleDriver');
		oracleSql.db.connection.autoCommit = false
		return oracleSql;
	}
	
	/** 사용자 정의 */
	def exceptionHandler
	/** 디폴트로 중복은 무시한다. */
	protected void exceptionHandle(Exception e,String sql,List param){
		if(exceptionHandler==null){
			if(e.message.toString().startsWith('ORA-00001: 무결성 제약 조건')) println ("중복예외이나 무시 : " + param)
			else throw e
		}else exceptionHandler(e)
	}

	/** 테이블정보가 담긴다 */
	List tables = []
	public Iterator iterator(){
		return tables.iterator()
	}
	/** ex) db.loadInfo(tableSql).loadColumn().loadColumnKey() */
	public OracleSql loadInfo(where=''){
		def sql = """SELECT a.TABLE_NAME,a.NUM_ROWS, b.COMMENTS FROM USER_TABLES a JOIN USER_TAB_COMMENTS b ON a.TABLE_NAME = b.TABLE_NAME 
		WHERE a.TABLE_NAME NOT LIKE 'BIN%' $where  ORDER BY a.TABLE_NAME"""
		tables = list(sql)
		return this
	}
	public OracleSql loadCount(){
		tables.each { it['COUNT'] = count(it.TABLE_NAME) }
		return this
	}
	public OracleSql loadColumn(){
		tables.each { it['columns'] =  columns(it.TABLE_NAME)}
		return this
	}

	/** SEARCH_CONDITION과 KEY를 세팅해준다. */
	public OracleSql loadColumnKey(){
		tables.each { columnsKey(it.TABLE_NAME,it.columns) }
		return this
	}
	public Map getAt(String tableName) {
		return tables.find { it.TABLE_NAME == tableName }
	}
	public Map getAt(int tableIndex) {
		return tables[tableIndex]
	}
	/* ================================================================================== */
	/*                                Util                                                */
	/* ================================================================================== */
	public columns(TABLE_NAME){
		def sql = """
			SELECT a.TABLE_NAME,a.COLUMN_NAME,COMMENTS,'' as KEY,DATA_TYPE,DATA_LENGTH,DATA_PRECISION,DATA_SCALE,'' as SEARCH_CONDITION
			FROM user_tab_columns a JOIN USER_COL_COMMENTS b
			ON a.COLUMN_NAME = b.COLUMN_NAME AND a.TABLE_NAME = b.TABLE_NAME
			WHERE a.TABLE_NAME = '$TABLE_NAME'
			ORDER BY a.TABLE_NAME, COLUMN_ID """
		list(sql)
	}
	public columnsKey(TABLE_NAME,colums){
		def conSql = """
				SELECT aa.TABLE_NAME,COLUMN_NAME,CONSTRAINT_TYPE,SEARCH_CONDITION
				FROM USER_CONS_COLUMNS aa join USER_CONSTRAINTS bb on aa.CONSTRAINT_NAME = bb.CONSTRAINT_NAME
				WHERE aa.TABLE_NAME = '$TABLE_NAME' """
		def con = list(conSql)
		//제약조건들은 알아서 세팅
		con.each{ c ->
			colums.findAll { col-> c.COLUMN_NAME == col.COLUMN_NAME }.each { result->
				if(c.CONSTRAINT_TYPE=='C' && result.containsKey('SEARCH_CONDITION')) result['SEARCH_CONDITION'] = c.SEARCH_CONDITION
				else  if(c.CONSTRAINT_TYPE=='P' && result.containsKey('KEY')) result['KEY'] = 'PK'
			}
		}
	}
	public columnsMap(TABLE_NAME){
		def map = [:]
		columns(TABLE_NAME).each { map.put it.COLUMN_NAME , it }
		return map
	}
	/** header 순서로 찾아와야 한다. */
	public columnComment(TABLE_NAME,header){
		def c = this.getAt(TABLE_NAME).columns
		return header.collect{ h ->  c.find { it['COLUMN_NAME'] == h }['COMMENTS']   }
	}

	/** 내부적으로 select를 한번 더 중첩한다.  이경우 중첩되는 컬럼 이름이 강제치환된다. 주의! 
	 * 따라서 단순 샘플자료를 추출한 목적이라면 이거대신 걍 rownum을 사용하자. */
	public paging(sql,pageSize,pageNo){
		def pagingSQL = toOraclePaging(sql,pageSize,pageNo)
		list pagingSQL
	}
	/* ================================================================================== */
	/*                                STATIC                                              */
	/* ================================================================================== */

	/** 오라클에서 사용하는 페이징으로 바꿔준다. */
	public static String toOraclePaging(sql,pageSize,pageNo){
		int startNo = pageSize * (pageNo-1) +1;
		int endNo = pageSize * pageNo;
		return "SELECT * FROM (SELECT inner.*,ROWNUM \"PAGE_RN\" FROM ( $sql ) inner) WHERE PAGE_RN BETWEEN $startNo AND $endNo"
	}

	/* ================================================================================== */
	/*                                Simple  백업                                                              */
	/* ================================================================================== */

	/** sql을 페이징처리해서 콜백 돌린다. 대규모 자료 변환에 사용하자.
	 * ex) def columns = columns(TABLE_NAME)
	 * insertList(~~)
	 *  */
	public void batch(tableName,start,batchSize,maxSize,callback,sqlAppend=''){
		int maxCount = one("select COUNT(*) as COUNT from $tableName $sqlAppend")['COUNT']
		int maxPage = Math.round( maxCount / batchSize )
		long beforeTime = 0
		long cumulatedTime
		long finalPage = maxPage > maxSize ? maxSize : maxPage //둘중 작은게 결정자이다.
		for(int i=start;i<finalPage+1;i++){
			long startTime = System.nanoTime()
			if(beforeTime==0) println "$i / $maxPage 파일 처리중...."
			else{
				def nowTime = new ShowTime(beforeTime).toString()
				long avg = Math.round(cumulatedTime /  (i-start))
				def avgTime = new ShowTime( avg ).toString()
				def maxTime = new ShowTime(avg * (finalPage - i + 1)).toString()
				println "$i / $maxPage 파일 처리중... 이전배치동작시간 $nowTime / 평균 $avgTime / 남은예상시간 $maxTime "
			}
			def list = paging "select * from $tableName "+sqlAppend,batchSize,i
			if(list.size()==0){
				println '데이터가 없습니다.'
				return;
			}
			callback(list,i);
			if(list.size() < batchSize) {
				println '마지막 자료입니다. 배치를 종료합니다.'
				return
			}
			if(i==maxSize) println '배치의 maxSize에 도달했습니다. 배치를 종료합니다.'
			beforeTime = System.nanoTime() - startTime
			cumulatedTime += beforeTime
		}
	}
	
	/** DB의 내용을 csv로 옮긴다.
	* 실DB의 일정분량만을 테스트DB로 이관할때 사용하였다.  batchSize * maxSize 가 전체수
	* ex) db.dbToXls ROOT, 500, 2, " and TABLE_NAME = 'AGR_AGRI_DAY' "
	* 전부 다 하고싶으면 maxSize를 매우 높게 주도록 하자.  */
	public dbToCsv(dir,batchSize,maxSize,where = ''){
		loadInfo(where).loadCount().loadColumn()
		tables.each { tableToCsv(it,dir,batchSize,maxSize)}
	}

	private void tableToCsv(table,dir,batchSize,maxSize){
		for(int i=1;i<maxSize+1;i++){
			println "$table.TABLE_NAME : $i 번째 파일 처리중"
			def list = paging "select * from $table.TABLE_NAME ",batchSize,i
			if(list.size()==0) return;
			
			def fileCount = StringUtil.leftPad(i.toString(),4,'0')
			def fileForWrite = new File(dir , "${table.TABLE_NAME}#${fileCount}.csv")
			OpenCsv.writeMap(fileForWrite, list);
			
			if(list.size() < batchSize) return
		}
	}
	
	public fileToDb(dir,toDir,errorDir=null){
		if(dir instanceof String) dir = new File(dir)
		if(toDir instanceof String) toDir = new File(toDir)
		if(!toDir.exists()) toDir.mkdirs()
		dir.listFiles().each {
			try{
				def name = StringUtil.getFirst(it.name, '.');
				println "$name 시작"
				def list
				if(it.name.toUpperCase().endsWith('.TXT')) list = new TextFile().readAsMap(it)
				else if(it.name.toUpperCase().endsWith('.CSV')) list =  OpenCsv.readAsMap(it)
				else list =   PoiReaderFactory.instance(it)[0].read()
				println "$name 로드완료 : size = ${list.size()}"
				
				def tableName = StringUtil.getFirst(name, '#');
				eachFileToDb(tableName, list)
				assert it.renameTo(new File(toDir,it.name))
			}catch(e){
				if(errorDir==null) throw e
				else{
					System.err.out(e.message);
					if(errorDir instanceof String) errorDir = new File(errorDir)
					if(!errorDir.exists()) errorDir.mkdirs()
					assert it.renameTo(new File(errorDir,it.name))
				}
			}
		}
	}
	
	/** 테이즐정보를 로드해서 columnNames을 생략할 수 있게 해준다. Map안의 데이터중 컬럼과 매핑되는것만 insert한다.
	* ex) println db.insert('도메인정의서',PoiReaderFactory.instance(ROOT+'KMA_DA_도메인정의서_v1.72')[1].read()) \
	* 근데 이거 쓸일이 거의 없는듯. 망함. */
   public int eachFileToDb(TABLE_NAME,List listMap){
	   def columns = columns(TABLE_NAME)
	   if(columns.size()==0) throw new RuntimeException("테이블 $TABLE_NAME 를 찾을 수 없습니다.")
	   listMap.each { map ->
		   def mapForReplace = [:]
		   map.each{ key,value ->
			   if('PAGE_RN' == key) return //페이징 처리에 사용됨
			   def col = columns.find { it['COLUMN_NAME'] == key  }
			   if(col==null) throw new RuntimeException("자료의 $key 컬럼을 DB스키마에서 찾을 수 없습니다")
			   if(col['DATA_TYPE']=='DATE') mapForReplace[key] = new Timestamp( Long.valueOf(value) )
		   }
		   mapForReplace.each { k,v -> map[k] == v }
	   }
	   //columnsKey(TABLE_NAME,columns)
	   def columnNames  = columns.collect { it.COLUMN_NAME }
	   return insertListMap(TABLE_NAME,columnNames,listMap)
   }
   
   /** 문자열을 DB타입으로 바꿔준다.  */
   private Object stringToJavaSqlType(String oracleType,String value){
	   println value
	   println oracleType
	   if( value == null || value == '' ) return null
	   if(oracleType == 'DATE') return new Timestamp( Long.valueOf(value) );
	   return value
   }
   
   /** columnNames을 안넣었을때  */
   public void insertList(tableName,List list){
	   def columnNames = columns(tableName).collect { it.COLUMN_NAME }
	   insertList(tableName,columnNames,list)
   }
	
	/** DB의 내용을 xls로 옮긴다. (임시용이다. 대용량은 txt로 처리하자)
	 * 실DB의 일정분량만을 테스트DB로 이관할때 사용하였다.  batchSize * maxSize 가 전체수 
	 * ex) db.dbToXls ROOT, 500, 2, " and TABLE_NAME = 'AGR_AGRI_DAY' "*/
	@Deprecated
	public dbToXls(file,batchSize,maxSize,where = ''){
		loadInfo(where).loadCount().loadColumn()
		tables.each { tableToXls(file,batchSize,maxSize,it)}
	}

	@Deprecated
	private void tableToXls(file,batchSize,maxSize,table){
		for(int i=1;i<maxSize+1;i++){
			println "$table.TABLE_NAME : $i 번째 파일 처리중"
			def list = paging "select * from $table.TABLE_NAME ",batchSize,i
			if(list.size()==0) continue
				Poi p = new Poi()
			p.setListedMap table.TABLE_NAME, list
			p.wrap()
			def fileCount = StringUtil.leftPad(i.toString(),4,'0')
			p.write new File(file + "/${table.TABLE_NAME}#${fileCount}.xls")
			if(list.size() < batchSize) return
		}
	}
	

	/** 테이즐정보를 로드해서 columnNames을 생략할 수 있게 해준다. Map안의 데이터중 컬럼과 매핑되는것만 insert한다.
	 * ex) println db.insert('도메인정의서',PoiReaderFactory.instance(ROOT+'KMA_DA_도메인정의서_v1.72')[1].read()) \
	 * 근데 이거 쓸일이 거의 없는듯. 망함. */
	@Deprecated
	public int insert(TABLE_NAME,List listMap){
		def columns = columns(TABLE_NAME)
		if(columns.size()==0) throw new RuntimeException("테이블 $TABLE_NAME 를 찾을 수 없습니다.")
		listMap.each {
			it.each{
				def key = it.key
				def col = columns.find { it['COLUMN_NAME'] == key  }
				if(col==null) throw new RuntimeException("자료의 $key 컬럼을 DB스키마에서 찾을 수 없습니다")
				it.value = stringToOracleType(col['DATA_TYPE'],it.value)
			}
		}
		columnsKey(TABLE_NAME,columns)
		def columnNames  = columns.collect { it.COLUMN_NAME }
		return insertListMap(TABLE_NAME,columnNames,listMap)
	}
	
	/** 그루비 입력시 반드시 옵션 확인!!!  
	 * 24시간 체계를 사용한다. 00시~ 23시
	 * hh로 포매팅한다면 12시와 00시를 구분하지 못한다. */
	@Deprecated
	public def format = new java.text.SimpleDateFormat("yyyy/MM/dd HH:mm:ss"); //오렌지 디폴트
	
	@Deprecated
	private Object stringToOracleType(String oracleType,String value){
		if(value==null || value=='') return null
		if(oracleType=='DATE') return new Timestamp(format.parse(value).getTime());
		return value
	}
	
	/** 해당 디렉토리의 모든 텍스트/엑셀 파일을 DB로 입력한다.
	 * 파일이름 = 테이블명, 헤더=컬럼명 이다 
	 * 다 입력되면 완료 디렉토리로 파일을 이동시킨다.
	 * 
	 * => 나중에 별도 객체로 빼고 조건을 클로저화 하자. */
	@Deprecated
	public simpleInsert(dir,toDir,errorDir=null){
		if(dir instanceof String) dir = new File(dir)
		if(toDir instanceof String) toDir = new File(toDir)
		if(!toDir.exists()) toDir.mkdirs()
		dir.listFiles().each {
			try{
				def name = StringUtil.getFirst(it.name, '.');
				println "$name 시작"
				def list
				if(it.name.toUpperCase().endsWith('.TXT')) list = new TextFile().readAsMap(it)
				else list =   PoiReaderFactory.instance(it)[0].read()
				println "$name 로드완료 : size = ${list.size()}"
				insert(name, list)
				assert it.renameTo(new File(toDir,it.name))
			}catch(e){
				if(errorDir==null) throw e
				else{
					println e.message
					if(errorDir instanceof String) errorDir = new File(errorDir)
					if(!errorDir.exists()) errorDir.mkdirs()
					assert it.renameTo(new File(errorDir,it.name))
				}
			}
		}
	}
	
	/* ================================================================================== */
	/*                                print                                               */
	/* ================================================================================== */
	
	/** 통계성 쿼리 등 간이 SQL을 bean으로 매핑하고싶을때.  */
	public printSqlToMyBatisBean(sql){
		//def list = paging(sql,1,1); //로우넘 생겨서 안씀 ㅋ
		def list = list(sql);
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
		def columns = columns(TABLE_NAME)
		columnsKey(TABLE_NAME,columns)
		def cn = columns.collect { it.COLUMN_NAME }
		
		/* 알아서 바꿔주자 */
		def $$ = {'#{'+it.camelize()+'}'  }; //mybatis용
		def $$2 = { cName ->
			def type = ''
			def col =  columns.find { it['COLUMN_NAME'] == cName }
			if(col['SEARCH_CONDITION']==null){   //null가능하다면 타입을 명시해준다. (mybatis의경우 타입이 없으면 null파라메터를 허용하지 않는다.)
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
		
		def pks = columns.findAll { it['KEY'] == 'PK' }.collect { it.COLUMN_NAME }
		def nonPks = columns.findAll { it['KEY'] != 'PK' }.collect { it.COLUMN_NAME }
		
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
	
	/** my-batis같은거 쓸때 null예외를 회피하기위해 XML에 추가해주는 용도. => 변경을 해야함 */
	public static def ORACLE_TO_JDBC_TYPE = ['NUMBER':'NUMERIC','DATE':'TIMESTAMP ','VARCHAR2':'VARCHAR','CHAR':'CHAR']
	public static def ORACLE_TO_JAVA_TYPE = ['NUMBER':'BigDecimal','DATE':'Date','VARCHAR2':'String','CHAR':'String']
	
	/** java 도메인 객체 기본생성 */
	public printJava(TABLE_NAME){
		def cols = columns(TABLE_NAME)
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
	
	
	/** 컬럼 정보를 한 시트에 모두 담는다. */
	public dbInfo(fileName,where = ''){
		Poi p = new Poi()
		loadInfo(where).loadColumn()
		tables.each { it['COUNT'] = count(it.TABLE_NAME) }
		p.setListedMap("1.테이블목록",tables);
		def allColums = []
		int tableCount = 1
		tables.each {
			if(it.TABLE_NAME.startsWith('BIN')) return
				def colums = it.columns
			def conSql = """
				SELECT aa.TABLE_NAME,COLUMN_NAME,CONSTRAINT_TYPE,SEARCH_CONDITION
				FROM USER_CONS_COLUMNS aa join USER_CONSTRAINTS bb on aa.CONSTRAINT_NAME = bb.CONSTRAINT_NAME
				WHERE aa.TABLE_NAME = '$it.TABLE_NAME' """
			def con = list(conSql)
			//제약조건들은 알아서 세팅
			con.each{ c ->
				colums.findAll { col-> c.COLUMN_NAME == col.COLUMN_NAME }.each { result->
					if(c.CONSTRAINT_TYPE=='C') result['SEARCH_CONDITION'] = c.SEARCH_CONDITION
					else  if(c.CONSTRAINT_TYPE=='P') result['KEY'] = 'PK'
				}
			}
			p.addHyperlink tableCount++, 0, '2.컬럼정보', 'A', allColums.size()+2
			allColums.addAll colums
		}
		p.setListedMap '2.컬럼정보',allColums
		p.wrap()
		p.getMerge(1).setAbleRow(0).setAbleCol(0).merge();
		p.write(fileName)
	}

	/**
	* 졸 쓸모없는듯
	* ex) db.sqlJoin(['AFFILIATION','AIR_METAR_JUN','AWS_AIR']) */
   public sqlJoin(joinTables){
	   def select = 'SELECT '
	   def from = 'FROM '
	   def first = true
	   for(int i=0;i<joinTables.size();i++){
		   def name = joinTables[i]
		   def ali = FormatUtil.intToAlpha(i)
		   def table = getAt(name)
		   if(table==null){ //특수환경을 위한 로직.. ㅅㅂ  인제 안쓰니 지워도 될듯
			   table = one("SELECT TABLE_NAME,COMMENTS FROM USER_TAB_COMMENTS WHERE TABLE_NAME = '$name' ")
			   table['columns'] = columns(name)
		   }
		   def cols = table.columns.collect { "$ali.$it.COLUMN_NAME" }
		   if(first) first = false
		   else{
			   select+= ','
			   from+= 'JOIN '
		   }
		   select += cols.join(',') + "  --$name($table.COMMENTS) \n"
		   from += "$name $ali "
	   }
	   println select + from + '\nWHERE ~~'
   }
   
   /** 제약조건 불러옴 */
   private def colsSql = """SELECT aa.TABLE_NAME,COLUMN_NAME,CONSTRAINT_TYPE,SEARCH_CONDITION,R_CONSTRAINT_NAME
   FROM USER_CONS_COLUMNS aa join USER_CONSTRAINTS bb on aa.CONSTRAINT_NAME = bb.CONSTRAINT_NAME """
   
   /** 스키마 비교용 테이블 3곳에 데이터를 입력한다.  시간에따라 or 계정에 따라 적절히 입력하자 테이블은 스키마 참조 */
   public schema(desc){
	   def constraints = list(colsSql)
	   MapForList map = new MapForList()
	   constraints.each { map.add(it.TABLE_NAME+':'+it.COLUMN_NAME, it)  }

	   int id =  oneValue('SELECT NVL(MAX(ID),0)+1 FROM TEMP_SCHEMA')
	   insertList('TEMP_SCHEMA',[[id,new Timestamp(new Date().time),desc]]);
	   
	   withTransaction {
		   def sql = """INSERT INTO TEMP_SCHEMA_TABLE (ID,TABLE_NAME,NUM_ROWS,COMMENTS)
		   SELECT $id,a.TABLE_NAME,a.NUM_ROWS, b.COMMENTS
		   FROM USER_TABLES a JOIN USER_TAB_COMMENTS b ON a.TABLE_NAME = b.TABLE_NAME"""
		   execute(sql);
		   
		   def sqlCol = """INSERT INTO TEMP_SCHEMA_COL (ID,TABLE_NAME,COLUMN_NAME,COMMENTS,DATA_TYPE,DATA_LENGTH,DATA_PRECISION,DATA_SCALE,KEY,NOT_NULL)
		   SELECT $id,a.TABLE_NAME,A.COLUMN_NAME,B.COMMENTS,DATA_TYPE,DATA_LENGTH,DATA_PRECISION,DATA_SCALE,'N','N'
		   FROM user_tab_columns a JOIN USER_COL_COMMENTS b
		   ON a.COLUMN_NAME = b.COLUMN_NAME AND a.TABLE_NAME = b.TABLE_NAME"""
		   execute(sqlCol);
	   }
	   
	   withTransaction('TEMP_SCHEMA_COL','1=1') { data ->
		   def cons = map[data.TABLE_NAME+':'+data.COLUMN_NAME]
		   if(cons==null) return
		   cons.each {
			   if(it.CONSTRAINT_TYPE == 'C') data['NOT_NULL'] = 'Y'
			   else if(it.CONSTRAINT_TYPE == 'R') data['R_CONSTRAINT_NAME'] = it['R_CONSTRAINT_NAME']
			   else if(it.CONSTRAINT_TYPE == 'P') data['KEY'] = 'Y'
		   }
	   }
   }
   
   /** 변경된 스키마를 비교해서 바뀐점을 찾아낸다. */
   public schemaChangeLog(long oldId,long newId){
	   
	   def buildSchema = { id ->
		   def map = list("SELECT ID, TABLE_NAME, COMMENTS, NUM_ROWS  FROM TEMP_SCHEMA_TABLE a where ID = $id").toMap('TABLE_NAME')
		   MapForList colMap = new MapForList()
		   list("SELECT ID, TABLE_NAME, COLUMN_NAME, COMMENTS, DATA_TYPE, DATA_LENGTH, DATA_PRECISION, DATA_SCALE, KEY, NOT_NULL, R_CONSTRAINT_NAME FROM TEMP_SCHEMA_COL a WHERE ID = $id ")
			   .each { colMap.add(it.TABLE_NAME, it) }
		   colMap.each { k,v->
			   def table = map[k] //null이면 view이다.
			   if(table==null) return
			   table['columns'] = v
		   }
		   return map
	   }
	   def oldDb = buildSchema(oldId)
	   def newDb = buildSchema(newId)
	   
	   def deleted = []
	   def created = []
	   def changed = []
	   def result = [deleted:deleted,created:created,changed:changed]
	   
	   oldDb.each { if(newDb[it.key]==null) deleted << it.value }
	   newDb.each {
		   def table = it.value
		   def oldTable = oldDb[it.key]
		   if(oldTable==null){
			   created << table
			   return
		   }
		   def oldColumns = oldTable.columns.toMap('COLUMN_NAME')
		   def newColumns = table.columns.toMap('COLUMN_NAME')
		   
		   def colDeleted = []
		   def colCreated = []
		   def colChanged = []
		   oldColumns.each { if(newColumns[it.key]==null) colDeleted << it.value }
		   newColumns.each {
			   def oldColumn = oldColumns[it.key]
			   if(oldColumn==null){
				   colCreated << it.value
				   return
			   }
			   def log = { a,b,name ->
				   if(a[name] != b[name])  colChanged << "$a.COLUMN_NAME : $name changed : ${a[name]} to ${b[name]}"
			   }
			   log(it.value,oldColumn,'DATA_TYPE')
			   log(it.value,oldColumn,'DATA_LENGTH')
			   log(it.value,oldColumn,'DATA_PRECISION')
			   log(it.value,oldColumn,'DATA_SCALE')
		   }
		   boolean isChanged =  (colDeleted.size() + colCreated.size() + colChanged.size()) != 0
		   changed << [isChanged:isChanged,tableInfo:table,colDeleted:colDeleted,colCreated:colCreated,colChanged:colChanged]
	   }
	   return result
   }
   
   /** 스키마 비교결과를 문자열로 만든다. */
   public schemaResultToString(result){
	   def log = ''
	   StringBuilder b = new StringBuilder()
	   log += '\n=== 삭제된 테이블 ==='
	   result.deleted.each { log += "\n$it.TABLE_NAME : $it.COMMENTS ($it.NUM_ROWS)"  }
	   log += '\n=== 생성된 테이블 ==='
	   result.created.each { log += "\n$it.TABLE_NAME : $it.COMMENTS ($it.NUM_ROWS)" }
	   log += '\n=== 변경된 테이블 ==='
	   result.changed.each {
		   if(!it.isChanged) return
		   def tableInfo = it.tableInfo
		   log += "\n$tableInfo.TABLE_NAME : $tableInfo.COMMENTS ($tableInfo.NUM_ROWS)"
		   it.colDeleted.each { log += "\n  삭제된 컬럼 : $it.COLUMN_NAME : $it.COMMENTS ($it.DATA_TYPE)" }
		   it.colCreated.each { log += "\n  생성된 컬럼 : $it.COLUMN_NAME : $it.COMMENTS ($it.DATA_TYPE)" }
		   it.colChanged.each { log += "\n  변경된 컬럼 : $it" }
	   }
	   return log
   }
   
   
   /**  */
   public sqlMigration(beforeId,boolean first){
	   
	   def whereConstraint = """ SELECT distinct aa.TABLE_NAME 
	   FROM USER_CONS_COLUMNS aa join USER_CONSTRAINTS bb on aa.CONSTRAINT_NAME = bb.CONSTRAINT_NAME
	   WHERE CONSTRAINT_TYPE = 'R' and aa.TABLE_NAME not like '%_DATA' """
   
	   def w1 = """and a.TABLE_NAME = ''  and a.TABLE_NAME NOT IN ( $whereConstraint )"""
	   def w2 = """and a.TABLE_NAME IN ( $whereConstraint )"""
	   
	   loadInfo(first ? w1 : w2).loadColumn().loadColumnKey()
	   def result = []
	   each {
		   def comment = "-- $it.COMMENTS ($it.TABLE_NAME)";
		   
		   def cn = it.columns.collect { it.COLUMN_NAME }
		   def remoteTableName = "$beforeId.$it.TABLE_NAME";
		   
		   try{
			   int count =  count(remoteTableName)
			   if(count==0){
				   result << "$comment : is empty remote table "
			   }else{
			   
				   def pks = it.columns.findAll { it['KEY'] == 'PK' }.collect { it.COLUMN_NAME }
				   def nonPks = it.columns.findAll { it['KEY'] != 'PK' }.collect { it.COLUMN_NAME }
				   
				   if(pks.size()==0){
					   result << "--$it.COMMENTS : $count\nINSERT INTO $it.TABLE_NAME ($colNames) SELECT $colNames FROM $remoteTableName ;"
				   }else{
					   def mergeSql  = "$comment\nMERGE INTO $it.TABLE_NAME o USING $remoteTableName r ON ( " + pks.collect { 'o.'+it + ' = r.'+ it }.join(' AND ')  + " )\n"
					   mergeSql += "WHEN matched THEN UPDATE SET " + nonPks.collect { it + ' = r.'+ it }.join(', ') + '\n'
					   mergeSql += "WHEN NOT matched THEN INSERT (" + cn.join(', ') + ') VALUES ('+ cn.collect { 'r.'+it }.join(', ')  +')'
					   result << mergeSql
				   }
			   }
		   }catch(e){
			   result << "$comment : error - $e.message "
		   }
	   }
	   return result
   }

}
