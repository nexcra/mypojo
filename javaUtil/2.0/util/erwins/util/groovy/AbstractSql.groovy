package erwins.util.groovy


import groovy.sql.Sql

import java.text.Format

/** 벤더별로 만들어 사용하자.
 *  sql.execute 로 테이블생성 등이 가능
 *  --> 이건 샘플 소스코드로 님기고 원본객체를 활용하자. 
 *  */
@Deprecated
public abstract class AbstractSql{
	
	/** 그루비 입력시 반드시 옵션 확인!!!
	* 24시간 체계를 사용한다. 00시~ 23시
	* hh로 포매팅한다면 12시와 00시를 구분하지 못한다. */
   public static Format format = new java.text.SimpleDateFormat("yyyy/MM/dd HH:mm:ss"); //오렌지 디폴트

	@Delegate
	public Sql db;
	
	/** @Delegate가 Java에서는 되지 않는다. Java에서 쓸려고 close를 코딩해둔다. */
	public close(){
		db.close();
	}

	/** GString이 들어와도 정상작동하게 변경 */
	public list(it){
		db.rows(it.toString())
	}
	public row(String tableName,String where = null){
		def sql = "SELECT * FROM $tableName "
		if(where!=null) sql+= " WHERE $where"
		list(sql)
	}
	/** 한개만 리턴 */
	public one(it){
		list(it)[0]
	}
	public oneValue(it){
		list(it)[0].iterator().next().value
	}
	public isContain(it){
		oneValue(it) > 0
	}
	/** tables.each { it['COUNT'] = db.count(it.TABLE_NAME) } 요런식으로 활용하자 */
	public count(tableName){
		one("select COUNT(*) as COUNT from $tableName ")['COUNT']
	}
	
	/** 간단한 sql문장을 생성한다. 사전에 전체카운트 체크할것! */
	public List simpleInsertSql(tableName){
		def list = list("select * from $tableName")
		return list.collect { "insert into $tableName values (" + it.collect{
			it.value==null ? 'null' : "'"+it.value+"'"  
		}.join(',') + ");" }
	}

	/** 이 안에서 작업하자. 범용 API로서 
	 * 아래 withTransaction와는 closure의 쓰임새가 틀리다. 주의! */
	public void withTransaction(Closure closure){
		db.resultSetConcurrency = java.sql.ResultSet.CONCUR_UPDATABLE
		try{
			closure()
			db.commit()
		}catch (Exception e) {
			db.rollback()
			throw e
		}finally{
			//reset resultSetsConcurrency back to read only (no further changes required)  데이터 변경 못하게 막는다.
			db.resultSetConcurrency = java.sql.ResultSet.CONCUR_READ_ONLY
		}
	}

	public void withTransaction(String tableName,String where,Closure closure){
		withTransaction("SELECT a.* FROM $tableName a WHERE $where",closure)
	}

	/** 이 안에서 작업하자. 오라클의 경우 알리아스를 필수로 입력해야 한다. 개편함 */
	public void withTransaction(String sql,Closure closure){
		if(!sql.toUpperCase().startsWith('SELECT')) sql = "SELECT a.* from $sql a "
		//allow resultSets to be able to be changed. 즉  => DataSet이나 eachRow 중 데이터의 변경이 가능하다.
		db.resultSetConcurrency = java.sql.ResultSet.CONCUR_UPDATABLE
		try{
			db.eachRow(sql.toString()){ closure(it) }
			db.commit()
		}catch (Exception e) {
			db.rollback()
			e.printStackTrace()
		}finally{
			db.resultSetConcurrency = java.sql.ResultSet.CONCUR_READ_ONLY
		}
	}

	/** insert전 삭제할때 사용된다 */
	public AbstractSql delete(String tableName){
		withTransaction {
			int i = db.executeUpdate('DELETE FROM ' + tableName)
			println "테이블 ${tableName}에 $i 건의 데이터가 삭제되었습니다 "
		}
		return this
	}
	
	/** false이면 예외를 던진다. */
	protected abstract void exceptionHandle(Exception e,String sql,List param)
	
	/** 그루비로 가져온 map파일을 다이렉트로 인서트 가능하다.
	 * ex) DB  마이그레이션  */
	public void insertListMap(tableName,List<Map> listMap){
		def columnNames = listMap[0].keySet().collect  { it }
		def list = listMap.collect { it.values().toList() }
		insertList(tableName,columnNames,list)
	}
	
	/** 1000개씩 배치로 묶어서 인서트한다. */
	public void insertList(tableName,columnNames,List list){
		def insertSql = "INSERT INTO $tableName (" + columnNames.join(',') + ') VALUES ('+ columnNames.collect { '?' }.join(',')  +')'
		withTransaction {
			db.withBatch 1000,insertSql,{ ps->
				list.each { ps.addBatch(it) }
			}
		}
		int success =  list.size()
		println "테이블 ${tableName}에 $success 건의 데이터가 입력되었습니다 "
	}
	
	/** 로그 없는버전 */
	public void insertListNoLog(tableName,columnNames,List list){
		def insertSql = "INSERT INTO $tableName (" + columnNames.join(',') + ') VALUES ('+ columnNames.collect { '?' }.join(',')  +')'
		withTransaction {
			db.withBatch 1000,insertSql,{ ps->
				list.each { ps.addBatch(it) }
			}
		}
		list.size()	
	}
	
	/** 한번에 너무많이 하지말긔~ update에 사용하자.
	 * ex) result.splitByNumber(1000).each 백만건 이상 시.  */
	public void withBatch(sql,List list){
		withTransaction {
			db.withBatch 1000,sql,{ ps->
				list.each { ps.addBatch(it) }
			}
		}
		int success =  list.size()
		println "$success 건의 sql이 실행되었습니다."
	}
	
}
