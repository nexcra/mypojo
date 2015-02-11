package erwins.util.groovy

import erwins.util.text.StringUtil
import groovy.sql.Sql

/** 소스 참고용 */
public abstract class OracleMetadata {
	
	def static 전체테이블로드 = "SELECT TABLE_NAME FROM USER_TABLES WHERE TABLE_NAME NOT LIKE 'BIN%'"
	
	def static 테이블정보 = """
		SELECT a.TABLE_NAME,a.NUM_ROWS, b.COMMENTS 
		FROM USER_TABLES a JOIN USER_TAB_COMMENTS b ON a.TABLE_NAME = b.TABLE_NAME
		WHERE a.TABLE_NAME NOT LIKE 'BIN%' AND a.TABLE_NAME = ? 
		ORDER BY a.TABLE_NAME
	"""

	def static 컬럼정보 = """
		SELECT a.TABLE_NAME,a.COLUMN_NAME,COMMENTS,DATA_TYPE,DATA_LENGTH,DATA_PRECISION,DATA_SCALE
		FROM user_tab_columns a JOIN USER_COL_COMMENTS b
		ON a.COLUMN_NAME = b.COLUMN_NAME AND a.TABLE_NAME = b.TABLE_NAME
		WHERE a.TABLE_NAME = ?
		ORDER BY a.TABLE_NAME, COLUMN_ID
	"""

	def static 제약조건 = """
		SELECT bb.TABLE_NAME,aa.COLUMN_NAME,aa.POSITION,bb.CONSTRAINT_TYPE,cc.TABLE_NAME R_TABLE_NAME,cc.COLUMN_NAME R_COLUMN_NAME,SEARCH_CONDITION
		FROM USER_CONS_COLUMNS aa JOIN USER_CONSTRAINTS bb on aa.CONSTRAINT_NAME = bb.CONSTRAINT_NAME
		left JOIN USER_CONS_COLUMNS cc ON bb.R_CONSTRAINT_NAME = cc.CONSTRAINT_NAME
		WHERE  bb.TABLE_NAME = ?
		ORDER BY bb.CONSTRAINT_TYPE,aa.POSITION
	"""

	public static List getMetadata(Sql sql,String ... tableNames){

		def 처리목록 = tableNames.length == 0 ? sql.rows(전체테이블로드).collect{it[0]}  :  tableNames
										
		return 처리목록.collect { 테이블명 -> 
			
			def 테이블정보 = sql.rows(테이블정보,[테이블명])
			def 제약조건정보 = sql.rows(제약조건,[테이블명])
			
			def PK셑 = 제약조건정보.findAll{ it['CONSTRAINT_TYPE'] == 'P' } collect{ it.COLUMN_NAME }.toSet()
			def NOT_NULL셑 = 제약조건정보.findAll{ it['CONSTRAINT_TYPE'] == 'C' && StringUtil.contains(it['SEARCH_CONDITION'], 'NOT NULL') } collect{ it.COLUMN_NAME }.toSet()
			def FK셑 = 제약조건정보.findAll{ it['CONSTRAINT_TYPE'] == 'R' }. collect{ [COLUMN_NAME:it['COLUMN_NAME'],R테이블:it['R_TABLE_NAME'],R컬럼:it['R_COLUMN_NAME']] }

			def 컬럼들 = sql.rows(컬럼정보,[테이블명]).collect {
				def 컬럼명 = it['COLUMN_NAME']
				def PK = PK셑.contains(컬럼명)
				def 타입 = it['DATA_TYPE']
				def NOT_NULL = NOT_NULL셑.contains(컬럼명)
				def FK = FK셑.find { it['COLUMN_NAME'] == 컬럼명 }
				def COMMENTS = it['COMMENTS']
				[컬럼명:컬럼명,PK:PK,FK:FK,타입:타입,NOT_NULL:NOT_NULL,COMMENTS:COMMENTS]
			}
			def 약어 = StringUtil.getAbbr(테이블명) //조인할때 쓴다.
			[테이블명:테이블명,컬럼들:컬럼들,COMMENTS:테이블정보['COMMENTS'],약어:약어,FK_SET:FK셑] //FK는 편의상 한번 더 저장한다.
		}
	}

}
