package erwins.util.groovy


import java.sql.Timestamp

import erwins.util.collections.MapForList


public class OracleSqlSchema{

	final OracleSql db;
	public OracleSqlSchema(OracleSql db){
		this.db = db;
	}
   
   /** 제약조건 불러옴 */
   private def colsSql = """SELECT aa.TABLE_NAME,COLUMN_NAME,CONSTRAINT_TYPE,SEARCH_CONDITION,R_CONSTRAINT_NAME
   FROM USER_CONS_COLUMNS aa join USER_CONSTRAINTS bb on aa.CONSTRAINT_NAME = bb.CONSTRAINT_NAME """
   
   /** 스키마 비교용 테이블 3곳에 데이터를 입력한다.  시간에따라 or 계정에 따라 적절히 입력하자 테이블은 스키마 참조 */
   public schema(desc){
	   def constraints = db.list(colsSql)
	   MapForList map = new MapForList()
	   constraints.each { map.add(it.TABLE_NAME+':'+it.COLUMN_NAME, it)  }

	   int id =  db.oneValue('SELECT NVL(MAX(ID),0)+1 FROM TEMP_SCHEMA')
	   db.insertList('TEMP_SCHEMA',[[id,new Timestamp(new Date().time),desc]]);
	   
	   db.withTransaction {
		   def sql = """INSERT INTO TEMP_SCHEMA_TABLE (ID,TABLE_NAME,NUM_ROWS,COMMENTS)
		   SELECT $id,a.TABLE_NAME,a.NUM_ROWS, b.COMMENTS
		   FROM USER_TABLES a JOIN USER_TAB_COMMENTS b ON a.TABLE_NAME = b.TABLE_NAME"""
		   db.execute(sql);
		   
		   def sqlCol = """INSERT INTO TEMP_SCHEMA_COL (ID,TABLE_NAME,COLUMN_NAME,COMMENTS,DATA_TYPE,DATA_LENGTH,DATA_PRECISION,DATA_SCALE,KEY,NOT_NULL)
		   SELECT $id,a.TABLE_NAME,A.COLUMN_NAME,B.COMMENTS,DATA_TYPE,DATA_LENGTH,DATA_PRECISION,DATA_SCALE,'N','N'
		   FROM user_tab_columns a JOIN USER_COL_COMMENTS b
		   ON a.COLUMN_NAME = b.COLUMN_NAME AND a.TABLE_NAME = b.TABLE_NAME"""
		   db.execute(sqlCol);
	   }
	   
	   db.withTransaction('TEMP_SCHEMA_COL','1=1') { data ->
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
		   def map = db.list("SELECT ID, TABLE_NAME, COMMENTS, NUM_ROWS  FROM TEMP_SCHEMA_TABLE a where ID = $id").toMap('TABLE_NAME')
		   MapForList colMap = new MapForList()
		   db.list("SELECT ID, TABLE_NAME, COLUMN_NAME, COMMENTS, DATA_TYPE, DATA_LENGTH, DATA_PRECISION, DATA_SCALE, KEY, NOT_NULL, R_CONSTRAINT_NAME FROM TEMP_SCHEMA_COL a WHERE ID = $id ")
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
   

}
