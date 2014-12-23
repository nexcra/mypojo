package erwins.util.groovy


import erwins.util.groovy.Tb.Col
import groovy.sql.Sql


/** 표준형인 date는 년월일만 지원한다. 오라클타입을 TimeStamp로 해야한다.
 * 강제로 date를 TimeStamp로 변경하려면 -Doracle.jdbc.V8Compatible=true 를 JVM옵션으로 주면된다. (v1.9~)
 * or originalDataSource.addConnectionProperty( "oracle.jdbc.V8Compatible", "true" ); 
 * table의 스키마를 담기위해 별도의 class로 변경했다.
 * 그루비 리절트로우의 경우 전체 리스트맵을 모두 바꾸면 정상변경되나 일부만 바뀌면 해당 프로퍼티를 찾을 수 없다는 메세지가 나온다. 주의! */
@Deprecated
public class OracleSql extends AbstractSql implements Iterable<Tb>{

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
	List<Tb> tables = []
	public Iterator<Tb> iterator(){
		return tables.iterator()
	}
	
	/** ex) db.loadInfo(tableSql).loadColumn().loadColumnKey() */
	public OracleSql loadInfo(where=''){
		def sql = """SELECT a.TABLE_NAME,a.NUM_ROWS, b.COMMENTS FROM USER_TABLES a JOIN USER_TAB_COMMENTS b ON a.TABLE_NAME = b.TABLE_NAME 
		WHERE a.TABLE_NAME NOT LIKE 'BIN%' $where  ORDER BY a.TABLE_NAME"""
		tables = list(sql).collect { it.toBean(Tb.class) }
		return this
	}
	public OracleSql loadCount(){
		tables.each { it['COUNT'] = count(it.TABLE_NAME) }
		return this
	}
	public OracleSql loadColumn(){
		tables.each { it['COLUMNS'] =  columns(it.TABLE_NAME)}
		return this
	}

	/** SEARCH_CONDITION과 KEY를 세팅해준다. */
	public OracleSql loadColumnKey(){
		tables.each { columnsKey(it.TABLE_NAME,it.COLUMNS) }
		return this
	}
	public Tb getAt(String tableName) {
		return tables.find { it.TABLE_NAME == tableName }
	}
	public Tb getAt(int tableIndex) {
		return tables[tableIndex]
	}
	
	/* ================================================================================== */
	/*                                Util                                                */
	/* ================================================================================== */
	public List<Col> columns(TABLE_NAME){
		def sql = """
			SELECT a.TABLE_NAME,a.COLUMN_NAME,COMMENTS,DATA_TYPE,DATA_LENGTH,DATA_PRECISION,DATA_SCALE
			FROM user_tab_columns a JOIN USER_COL_COMMENTS b
			ON a.COLUMN_NAME = b.COLUMN_NAME AND a.TABLE_NAME = b.TABLE_NAME
			WHERE a.TABLE_NAME = '$TABLE_NAME'
			ORDER BY a.TABLE_NAME, COLUMN_ID """
		list(sql).collect { it.toBean(Col.class) }
	}
	
	public columnsKey(TABLE_NAME,List<Col> colums){
		def conSql = """
				SELECT aa.TABLE_NAME,COLUMN_NAME,CONSTRAINT_TYPE,SEARCH_CONDITION
				FROM USER_CONS_COLUMNS aa join USER_CONSTRAINTS bb on aa.CONSTRAINT_NAME = bb.CONSTRAINT_NAME
				WHERE aa.TABLE_NAME = '$TABLE_NAME' """
		def constraintList = list(conSql)
		//제약조건들은 알아서 세팅
		constraintList.each{ c ->
			colums.findAll { col-> c.COLUMN_NAME == col.COLUMN_NAME }.each {Col result ->
				if(c.CONSTRAINT_TYPE=='C') result.C = c.CONSTRAINT_TYPE
				else  if(c.CONSTRAINT_TYPE=='P') result.P = c.CONSTRAINT_TYPE
			}
		}
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
   
   /** 해당 테이블 컬럼순서대로 파라메터를 입력 후 사용한다.  컬럼순서가 변경되면 안되니 주의! */
   public void insertList(tableName,List list){
	   def columnNames = columns(tableName).collect { it.COLUMN_NAME }
	   insertList(tableName,columnNames,list)
   }
   
   public void insertListNoLog(tableName,List list){
	   def columnNames = columns(tableName).collect { it.COLUMN_NAME }
	   insertListNoLog(tableName,columnNames,list)
   }


}
