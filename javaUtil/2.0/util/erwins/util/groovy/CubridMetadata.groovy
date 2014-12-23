package erwins.util.groovy

import groovy.sql.Sql

public abstract class CubridMetadata {
	
public static getMedadata(Sql sql){
		
		def 테이블코멘트 = [:]
		def 컬럼코멘트 = [:]
		sql.rows("SELECT table_name,column_name, description FROM _cub_schema_comments").each {
			if(it.column_name == '*') 테이블코멘트.put(it.table_name,it.description)
			else 컬럼코멘트.put(it.table_name+'*'+it.column_name,it.description)
		}
		
		return  sql.rows("SHOW TABLES").collect {
			def 테이블명 = it.values().iterator().next()
			//if(테이블명.startsWith('_'))
			def 컬럼들 = sql.rows("SHOW COLUMNS FROM $테이블명").collect {
				def 컬럼명 = it['Field']
				def PK = it['Key'] == 'PRI'
				def 타입 = it['Type']
				def NOT_NULL = it['Null'] == 'NO'
				[컬럼명:컬럼명,PK:PK,타입:타입,NOT_NULL:NOT_NULL,COMMENTS:컬럼코멘트[테이블명+'*'+컬럼명]]
			}
			[테이블명:테이블명,컬럼들:컬럼들,COMMENTS:테이블코멘트[테이블명]]
		}
	}

}
