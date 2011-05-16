package erwins.util.vender.apache


import groovy.sql.Sql

import erwins.util.vender.apache.Poi;
/** 표준형인 date는 년월일만 지원한다. 오라클타입을 TimeStamp로 해야한다. */
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
		Sql.metaClass."dbInfo"  = {
			Poi p = new Poi()
			def tables = db.list("SELECT TABLE_NAME,COMMENTS FROM USER_TAB_COMMENTS WHERE TABLE_NAME NOT LIKE 'BIN%' ORDER BY TABLE_NAME")
			p.setListedMap("1.테이블목록",tables);
			for(int i=0;i<tables.size();i++) p.addHyperlink i+1, 0, tables[i].TABLE_NAME, 'A', 1 
			//tables.each{  }
			tables.each {
				if(it.TABLE_NAME.startsWith('BIN')) return;
				def sql = """
					SELECT '' as KEY,a.COLUMN_NAME,COMMENTS,DATA_TYPE,DATA_LENGTH,DATA_PRECISION,DATA_SCALE,'' as SEARCH_CONDITION
					FROM user_tab_columns a JOIN USER_COL_COMMENTS b
					ON a.COLUMN_NAME = b.COLUMN_NAME AND a.TABLE_NAME = b.TABLE_NAME
					WHERE a.TABLE_NAME = '$it.TABLE_NAME'  ORDER BY COLUMN_ID """
				def colums = db.list(sql)
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
		Sql.metaClass."iBatisDml"  = { TABLE_NAME -> 
			def sql = """
				SELECT '' as KEY,a.COLUMN_NAME,COMMENTS,DATA_TYPE,DATA_LENGTH,DATA_PRECISION,DATA_SCALE,'' as SEARCH_CONDITION
				FROM user_tab_columns a JOIN USER_COL_COMMENTS b
				ON a.COLUMN_NAME = b.COLUMN_NAME AND a.TABLE_NAME = b.TABLE_NAME
				WHERE a.TABLE_NAME = '$TABLE_NAME'  ORDER BY COLUMN_ID """
			def colums = db.list(sql)
			def cn = colums.collect { it.COLUMN_NAME }
			def insert = "INSERT INTO $TABLE_NAME (" + cn.join(',') + ')\nVALUES ('+ cn.collect { ':'+it}.join(',')  +')'
			def update = "UPDATE $TABLE_NAME SET " + cn.collect { it + ' = :'+it}.join(',') + '\nWHERE ID = :ID'
			def select = 'SELECT ' + cn.collect{'a.'+it}.join(',') + "\nFROM $TABLE_NAME a \nWHERE ~~ ORDER BY ~~"
			println '==INSERT==\n'+insert
			println '\n==UPDATE==\n'+update
			println '\n==SELECT==\n'+select
		}
		
		
		
		
		
		return db;
	}
}

