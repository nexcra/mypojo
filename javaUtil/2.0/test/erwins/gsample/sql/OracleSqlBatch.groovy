package erwins.gsample.sql


import erwins.util.vender.apache.Poi

/** 이상한거, 대용량 다 지움.. */
/** 소스 참고용 */
public class OracleSqlBatch{

	final OracleSql db;
	public OracleSqlBatch(OracleSql db){
		this.db = db;
	}

	/** 컬럼 정보를 한 시트에 모두 담는다. */
	public dbInfo(fileName,where = ''){
		Poi p = new Poi()
		db.loadInfo(where).loadColumn()
		db.tables.each { it['COUNT'] = count(it.TABLE_NAME) }
		p.setListedMap("1.테이블목록",tables);
		def allColums = []
		int tableCount = 1
		db.tables.each {
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

}
