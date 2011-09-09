package erwins.util.groovy


import java.sql.Timestamp

import org.apache.poi.hssf.record.formula.functions.T

import erwins.util.lib.FormatUtil
import erwins.util.lib.StringUtil
import erwins.util.tools.TextFile
import erwins.util.vender.apache.Poi
import erwins.util.vender.apache.PoiReaderFactory
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
		tables = list("SELECT TABLE_NAME,COMMENTS FROM USER_TAB_COMMENTS WHERE TABLE_NAME NOT LIKE 'BIN%' $where ORDER BY TABLE_NAME")
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
	/*                                Simple                                              */
	/* ================================================================================== */

	/** DB의 내용을 xls로 옮긴다. (임시용이다. 대용량은 txt로 처리하자)
	 * 실DB의 일정분량만을 테스트DB로 이관할때 사용하였다.  batchSize * maxSize 가 전체수 
	 * ex) db.dbToXls ROOT, 500, 2, " and TABLE_NAME = 'AGR_AGRI_DAY' "*/
	public dbToXls(file,batchSize,maxSize,where = ''){
		loadInfo(where).loadCount().loadColumn()
		tables.each { tableToXls(file,batchSize,maxSize,it)}
	}

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

	/** ex) db.sqlJoin(['AFFILIATION','AIR_METAR_JUN','AWS_AIR']) */
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

	/** 테이즐정보를 로드해서 columnNames을 생략할 수 있게 해준다. Map안의 데이터중 컬럼과 매핑되는것만 insert한다.
	 * ex) println db.insert('도메인정의서',PoiReaderFactory.instance(ROOT+'KMA_DA_도메인정의서_v1.72')[1].read()) \
	 * 근데 이거 쓸일이 거의 없는듯. 망함. */
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
	public def format = new java.text.SimpleDateFormat("yyyy/MM/dd HH:mm:ss"); //오렌지 디폴트
	
	private Object stringToOracleType(String oracleType,String value){
		if(value==null || value=='') return null
		if(oracleType=='DATE') return new Timestamp(format.parse(value).getTime());
		return value
	}

	/** iBatis등으로 개발할때 활용하자. */
	public printIBatis(TABLE_NAME){
		def columns = columns(TABLE_NAME)
		columnsKey(TABLE_NAME,columns)
		def cn = columns.collect { it.COLUMN_NAME }
		//println "INSERT INTO $TABLE_NAME (" + cn.join(',') + ') VALUES ('+ cn.collect { ':'+it}.join(',')  +')'
		println "INSERT INTO $TABLE_NAME (" + cn.join(',') + ') VALUES ('+ cn.collect { '#'+StringUtil.getCamelize(it)+'#'}.join(',')  +')'
		//println "UPDATE $TABLE_NAME SET " + cn.collect { it + ' = :'+it}.join(',') + ' WHERE ID = :ID'
		println "UPDATE $TABLE_NAME SET " + cn.collect { it + ' = '+'#'+StringUtil.getCamelize(it)+'#'}.join(',') + ' WHERE ID = :ID'
		println 'SELECT ' + cn.collect{'a.'+it}.join(',') + " FROM $TABLE_NAME a "
		
		def pks = columns.findAll { it['KEY'] == 'PK' }.collect { it.COLUMN_NAME }
		def nonPks = columns.findAll { it['KEY'] != 'PK' }.collect { it.COLUMN_NAME }
		def sql  = "MERGE INTO $TABLE_NAME USING dual ON ( " + pks.collect { it + ' = '+'#'+StringUtil.getCamelize(it)+'#' }.join(' AND ')  + " )\n"
		sql += "WHEN matched THEN UPDATE SET " + nonPks.collect { it + ' = '+'#'+StringUtil.getCamelize(it)+'#' }.join(',') + '\n'
		sql += "WHEN NOT matched THEN INSERT (" + cn.join(',') + ') VALUES ('+ cn.collect { '#'+StringUtil.getCamelize(it)+'#'}.join(',')  +')'
		println sql 
	}
	
	/*MERGE INTO DB_CTRL_SMS_WARN USING dual ON ( MONG_CLAU_CD = :mongClauCd AND WARN_RNK = :warnRnk)
	WHEN matched THEN UPDATE SET TSHD_CNT = :tshdCnt
	WHEN NOT matched THEN
	INSERT (MONG_CLAU_CD,WARN_RNK,TSHD_CNT) VALUES (:mongClauCd,:warnRnk,:tshdCnt)*/
	
	public static def JAVA_TYPE = ['NUMBER':'BigDecimal','DATE':'Date','VARCHAR2':'String','CHAR':'String']
	public static def SIMPLE_TYPE = ['NUMBER':'Num','DATE':'Date','VARCHAR2':'VC2','CHAR':'Char']
	
	/** java 도메인 객체 기본생성 */
	public printJava(TABLE_NAME){
		def cols = columns(TABLE_NAME)
		def cn = cols.collect { it.COLUMN_NAME }
		println StringUtil.getCamelize(TABLE_NAME).capitalize()
		cols.each {
			def type = JAVA_TYPE[it['DATA_TYPE']]
			if(type=='BigDecimal' && it['DATA_SCALE']==0) type ='Long'
			def name = StringUtil.getCamelize(it['COLUMN_NAME'])
			println "/** $it.COMMENTS */"
			println "private $type $name;"
		}
	}
	
	/** 해당 디렉토리의 모든 텍스트/엑셀 파일을 DB로 입력한다.
	 * 파일이름 = 테이블명, 헤더=컬럼명 이다 
	 * 다 입력되면 완료 디렉토리로 파일을 이동시킨다.
	 * 
	 * => 나중에 별도 객체로 빼고 조건을 클로저화 하자. */
	public simpleInsert(dir,toDir,errorDir=null){
		if(dir instanceof String) dir = new File(dir)
		if(toDir instanceof String) toDir = new File(toDir)
		if(!toDir.exists()) toDir.mkdirs()
		def tf = new TextFile()
		dir.listFiles().each {
			try{
				def name = StringUtil.getFirst(it.name, '.');
				println "$name 시작"
				def list
				if(it.name.toUpperCase().endsWith('.TXT')) list = tf.readAsMap(it)
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

}
