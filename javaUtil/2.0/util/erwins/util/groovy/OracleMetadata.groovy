package erwins.util.groovy

import erwins.util.text.StringUtil
import groovy.sql.Sql

public abstract class OracleMetadata {
	
	def static 테이블정보 = """
		SELECT a.TABLE_NAME,a.NUM_ROWS, b.COMMENTS FROM USER_TABLES a JOIN USER_TAB_COMMENTS b ON a.TABLE_NAME = b.TABLE_NAME
		WHERE a.TABLE_NAME NOT LIKE 'BIN%' AND a.TABLE_NAME = 'ACC_INFO' 
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
		SELECT aa.TABLE_NAME,COLUMN_NAME,CONSTRAINT_TYPE,SEARCH_CONDITION
		FROM USER_CONS_COLUMNS aa join USER_CONSTRAINTS bb on aa.CONSTRAINT_NAME = bb.CONSTRAINT_NAME
		WHERE aa.TABLE_NAME = ?
	"""
	
	public static getMedadata(Sql sql){
		return sql.rows(테이블정보).collect {
			def 테이블명 = it['TABLE_NAME']
			
			def 제약조건정보 = sql.rows(제약조건,[테이블명])
			def PK셑 = 제약조건정보.findAll{ it['CONSTRAINT_TYPE'] == 'P' } collect{ it.COLUMN_NAME }.toSet()
			def NOT_NULL셑 = 제약조건정보.findAll{ it['CONSTRAINT_TYPE'] == 'C' && StringUtil.contains(it['SEARCH_CONDITION'], 'NOT NULL') } collect{ it.COLUMN_NAME }.toSet()
			
			def 컬럼들 = sql.rows(컬럼정보,[테이블명]).collect {
				def 컬럼명 = it['COLUMN_NAME']
				def PK = PK셑.contains(컬럼명)
				
				def 타입 = it['DATA_TYPE']
				def NOT_NULL = NOT_NULL셑.contains(컬럼명)
				def COMMENTS = it['COMMENTS']
				[컬럼명:컬럼명,PK:PK,타입:타입,NOT_NULL:NOT_NULL,COMMENTS:COMMENTS]
			}
			[테이블명:테이블명,컬럼들:컬럼들]
		}
	}

}
