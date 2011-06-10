package erwins.util.vender.apache


import erwins.util.lib.FormatUtil;
import erwins.util.lib.StringUtil;
import groovy.sql.Sql
/** 표준형인 date는 년월일만 지원한다. 오라클타입을 TimeStamp로 해야한다.
 * 강제로 date를 TimeStamp로 변경하려면 -Doracle.jdbc.V8Compatible=true 를 JVM옵션으로 주면된다. (v1.9~)
 * or originalDataSource.addConnectionProperty( "oracle.jdbc.V8Compatible", "true" ); 
 * table의 스키마를 담기위해 별도의 class로 변경했다. */
public class OracleSql extends AbstractSql implements Iterable{
	
	public static OracleSql instance(ip,sid,id,pass){
		OracleSql oracleSql = new OracleSql()
		oracleSql.db = Sql.newInstance("jdbc:oracle:thin:@$ip:1521:$sid",id,pass,'oracle.jdbc.driver.OracleDriver');
		//db.connection.autoCommit = false
		return oracleSql;
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
	 * 실DB의 일정분량만을 테스트DB로 이관할때 사용하였다.  batchSize * maxSize 가 전체수 */
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
	
	/** iBatis등으로 개발할때 활용하자. */
	public sqlIBatis(TABLE_NAME){
		def sql = """
			SELECT '' as KEY,a.COLUMN_NAME,COMMENTS,DATA_TYPE,DATA_LENGTH,DATA_PRECISION,DATA_SCALE,'' as SEARCH_CONDITION
			FROM user_tab_columns a JOIN USER_COL_COMMENTS b
			ON a.COLUMN_NAME = b.COLUMN_NAME AND a.TABLE_NAME = b.TABLE_NAME
			WHERE a.TABLE_NAME = '$TABLE_NAME'  ORDER BY COLUMN_ID """
		def cn = list(sql).collect { it.COLUMN_NAME }
		def insert = "INSERT INTO $TABLE_NAME (" + cn.join(',') + ')\nVALUES ('+ cn.collect { ':'+it}.join(',')  +')'
		def update = "UPDATE $TABLE_NAME SET " + cn.collect { it + ' = :'+it}.join(',') + '\nWHERE ID = :ID'
		def select = 'SELECT ' + cn.collect{'a.'+it}.join(',') + "\nFROM $TABLE_NAME a \nWHERE ~~ ORDER BY ~~"
		println '==INSERT==\n'+insert
		println '\n==UPDATE==\n'+update
		println '\n==SELECT==\n'+select
	}
	
	/**
	 *  엑셀 등을 읽어서 대량 insert할때 사용한다. -> 메모리 문제로 사용안함.  추후 수정
		Sql.metaClass."insertList"  = { tableName,colimnNames,parameterList ->
			def parameter = StringUtil.iterateStr( '?', ',', colimnNames.length)
			def INSERT = "INSERT INTO $tableName ( ${colimnNames.join(',')} ) VALUES ( ${parameter} )"
			
			def (success,duplicated) = [0, 0]
			parameterList.each {
				try{
					executeInsert INSERT.toString(), it
					success++
				}catch(SQLException e){
					if(e.message.toString().startsWith('ORA-00001: 무결성 제약 조건')) duplicated++
					else{
						println it
						throw e
					}
				}
			}
			return [success, duplicated]
		}
	 */

	
}
