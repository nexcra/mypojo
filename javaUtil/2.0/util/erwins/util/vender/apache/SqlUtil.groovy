package erwins.util.vender.apache


import java.sql.SQLException;

import groovy.sql.Sql

import erwins.util.lib.StringUtil;
import erwins.util.vender.apache.Poi;
/** 표준형인 date는 년월일만 지원한다. 오라클타입을 TimeStamp로 해야한다.
 * 강제로 date를 TimeStamp로 변경하려면 -Doracle.jdbc.V8Compatible=true 를 JVM옵션으로 주면된다. (v1.9~)
 * or originalDataSource.addConnectionProperty( "oracle.jdbc.V8Compatible", "true" );  */
public class SqlUtil{
	
	/** 오라클에서 사용하는 페이징으로 바꿔준다. */
	public static String toOraclePaging(sql,pageSize,pageNo){
		int startNo = pageSize * (pageNo-1) +1;
		int endNo = pageSize * pageNo;
		return "SELECT * FROM (SELECT inner.*,ROWNUM \"PAGE_RN\" FROM ( $sql ) inner) WHERE PAGE_RN BETWEEN $startNo AND $endNo"
	}
	
	public static Sql newOracleInstance(ip,sid,id,pass){
		Sql db = Sql.newInstance("jdbc:oracle:thin:@$ip:1521:$sid",id,pass,'oracle.jdbc.driver.OracleDriver');
		Sql.metaClass."list"  = { delegate.rows(it.toString())  } //GString이 들어와도 정상작동하게 변경
		/** 한개만 리턴한다. */
		Sql.metaClass."one"  = {
			delegate.list(it)[0]
		}
		/** tables.each { it['COUNT'] = db.count(it.TABLE_NAME) } 요런식으로 활용하자 */
		Sql.metaClass."count"  = { tableName ->
			delegate.one("select COUNT(*) as COUNT from $tableName ")['COUNT']
		}
		//이하는 오라클에서만 동작
		Sql.metaClass."paging"  = { sql,pageSize,pageNo -> 
			def pagingSQL = toOraclePaging(sql,pageSize,pageNo)
			delegate.list pagingSQL
		}
		
		Sql.metaClass."countForOracle"  = { 
			delegate.list('SELECT * FROM user_tables').each { 
				println "$it.TABLE_NAME : "+db.list("select count(*) from $it.TABLE_NAME")[0]['COUNT(*)']
			}
		}
		Sql.metaClass."tableList"  = { where ->
			db.list("SELECT TABLE_NAME,COMMENTS FROM USER_TAB_COMMENTS WHERE TABLE_NAME NOT LIKE 'BIN%' $where ORDER BY TABLE_NAME")
		}
		
		Sql.metaClass."columnList"  = { TABLE_NAME ->
			def sql = """
				SELECT a.TABLE_NAME,a.COLUMN_NAME,COMMENTS,'' as KEY,DATA_TYPE,DATA_LENGTH,DATA_PRECISION,DATA_SCALE,'' as SEARCH_CONDITION
				FROM user_tab_columns a JOIN USER_COL_COMMENTS b
				ON a.COLUMN_NAME = b.COLUMN_NAME AND a.TABLE_NAME = b.TABLE_NAME
				WHERE a.TABLE_NAME = '$TABLE_NAME'
				ORDER BY a.TABLE_NAME, COLUMN_ID """
			db.list(sql)
		}
		Sql.metaClass."columnMap"  = { TABLE_NAME ->
			def map = [:]
			delegate.columnList(TABLE_NAME).each { map.put it.COLUMN_NAME , it }
			return map
		}
		/** 컬럼 정보를 한 시트에 모두 담는다. */
		Sql.metaClass."dbInfo"  = { it,where = '' ->
			Poi p = new Poi()
			def tables = delegate.tableList(where)
			tables.each { it['COUNT'] = db.count(it.TABLE_NAME) }
			p.setListedMap("1.테이블목록",tables);
			def allColums = []
			int tableCount = 1 
			tables.each {
				if(it.TABLE_NAME.startsWith('BIN')) return 
					def colums = delegate.columnList2(it.TABLE_NAME)
				def conSql = """
					SELECT aa.TABLE_NAME,COLUMN_NAME,CONSTRAINT_TYPE,SEARCH_CONDITION
					FROM USER_CONS_COLUMNS aa join USER_CONSTRAINTS bb on aa.CONSTRAINT_NAME = bb.CONSTRAINT_NAME
					WHERE aa.TABLE_NAME = '$it.TABLE_NAME' """
				def con = db.list(conSql)
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
			p.write(it)
		}
		
		/** iBatis등으로 개발할때 활용하자. */
		Sql.metaClass."iBatisDml"  = { TABLE_NAME -> 
			def sql = """
				SELECT '' as KEY,a.COLUMN_NAME,COMMENTS,DATA_TYPE,DATA_LENGTH,DATA_PRECISION,DATA_SCALE,'' as SEARCH_CONDITION
				FROM user_tab_columns a JOIN USER_COL_COMMENTS b
				ON a.COLUMN_NAME = b.COLUMN_NAME AND a.TABLE_NAME = b.TABLE_NAME
				WHERE a.TABLE_NAME = '$TABLE_NAME'  ORDER BY COLUMN_ID """
			def cn = db.list(sql).collect { it.COLUMN_NAME }
			def insert = "INSERT INTO $TABLE_NAME (" + cn.join(',') + ')\nVALUES ('+ cn.collect { ':'+it}.join(',')  +')'
			def update = "UPDATE $TABLE_NAME SET " + cn.collect { it + ' = :'+it}.join(',') + '\nWHERE ID = :ID'
			def select = 'SELECT ' + cn.collect{'a.'+it}.join(',') + "\nFROM $TABLE_NAME a \nWHERE ~~ ORDER BY ~~"
			println '==INSERT==\n'+insert
			println '\n==UPDATE==\n'+update
			println '\n==SELECT==\n'+select
		}
		
		/** 엑셀 등을 읽어서 대량 insert할때 사용한다. -> 메모리 문제로 사용안함.  추후 수정 */
		Sql.metaClass."insertList"  = { tableName,colimnNames,parameterList ->
			def parameter = StringUtil.iterateStr( '?', ',', colimnNames.length)
			def INSERT = "INSERT INTO $tableName ( ${colimnNames.join(',')} ) VALUES ( ${parameter} )"
			
			def (success,duplicated) = [0, 0]
			parameterList.each {
				try{
					db.executeInsert INSERT.toString(), it
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
		
		return db;
	}
}

/*
 * Sql.metaClass."columnList"  = { TABLE_NAME ->
			def sql = """
					SELECT '' as KEY,a.COLUMN_NAME,COMMENTS,DATA_TYPE,DATA_LENGTH,DATA_PRECISION,DATA_SCALE,'' as SEARCH_CONDITION
					FROM user_tab_columns a JOIN USER_COL_COMMENTS b
					ON a.COLUMN_NAME = b.COLUMN_NAME AND a.TABLE_NAME = b.TABLE_NAME
					WHERE a.TABLE_NAME = '$TABLE_NAME'  ORDER BY COLUMN_ID """
			db.list(sql)
		} 
		
//컬럼 정보를 각각의 시트에 담는다.
		Sql.metaClass."dbInfo"  = { it,where ->
			Poi p = new Poi()
			def tables = delegate.tableList(where)
			tables.each { it['COUNT'] = db.count(it.TABLE_NAME) }
			p.setListedMap("1.테이블목록",tables);
			for(int i=0;i<tables.size();i++) p.addHyperlink i+1, 0, tables[i].TABLE_NAME, 'A', 1 
			tables.each {
				if(it.TABLE_NAME.startsWith('BIN')) return 
					def colums = delegate.columnList(it.TABLE_NAME)
				def conSql = """
					SELECT aa.TABLE_NAME,COLUMN_NAME,CONSTRAINT_TYPE,SEARCH_CONDITION
					FROM USER_CONS_COLUMNS aa join USER_CONSTRAINTS bb on aa.CONSTRAINT_NAME = bb.CONSTRAINT_NAME
					WHERE aa.TABLE_NAME = '$it.TABLE_NAME' """
				def con = db.list(conSql)
				//제약조건들은 알아서 세팅
				con.each{ c ->
					colums.findAll { col-> c.COLUMN_NAME == col.COLUMN_NAME }.each { result->
						if(c.CONSTRAINT_TYPE=='C') result['SEARCH_CONDITION'] = c.SEARCH_CONDITION
						else  if(c.CONSTRAINT_TYPE=='P') result['KEY'] = 'PK'
					}
				}
				p.setListedMap(it.TABLE_NAME,colums)
			}
			p.wrap()
			p.write(it)
		}		
 * 
 * */

