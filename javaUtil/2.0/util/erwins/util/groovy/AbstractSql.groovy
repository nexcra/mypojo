package erwins.util.groovy


import java.sql.Timestamp

import erwins.util.counter.Accumulator
import erwins.util.counter.Accumulator.ThreashHoldRun
import groovy.lang.Closure
import groovy.sql.Sql

/** 벤더별로 만들어 사용하자.
 *  sql.execute 로 테이블생성 등이 가능 */
public abstract class AbstractSql{

	@Delegate
	protected Sql db;

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

	/** 이 안에서 작업하자. 아래 withTransaction와는 closure의 쓰임새가 틀리다. 주의! */
	public void withTransaction(Closure closure){
		db.resultSetConcurrency = java.sql.ResultSet.CONCUR_UPDATABLE
		try{
			closure()
			db.commit()
		}catch (Exception e) {
			db.rollback()
			e.printStackTrace()
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
	
	protected abstract void exceptionHandle(Exception e,String sql,List param)
	
	/** 디비 스키마 기준으로 엑셀 내용을 insert할때 사용된다. Map에 잡데이터가 들어가있어도 해당 컬럼만 입력된다.  */
	public int insertListMap(tableName,columnNames,List listMap){
		def insertSql = "INSERT INTO $tableName (" + columnNames.join(',') + ') VALUES ('+ columnNames.collect { '?' }.join(',')  +')'
		def ac = new Accumulator(10000,{ println it * 10000 } as ThreashHoldRun);
		int success=0
		withTransaction {
			listMap.each { map ->
				def param = columnNames.collect { map[it] instanceof Date ? new Timestamp(map[it].getTime()) : map[it] } //Date는지원하지 않는다.
				try{
					db.executeInsert(insertSql, param)
					success++
				}catch(e){
					exceptionHandle(e,insertSql,param)
				}
				ac.next()
			}
		}
		println "테이블 ${tableName}에 $success 건의 데이터가 입력되었습니다 "
		return ac.count()
	}
	
	/** map대신 List<List>를 사용한다.  */
	public int insertList(tableName,columnNames,List list){
		def insertSql = "INSERT INTO $tableName (" + columnNames.join(',') + ') VALUES ('+ columnNames.collect { '?' }.join(',')  +')'
		int success=0
		withTransaction {
			list.each {
				try{
					db.executeInsert(insertSql, it)
					success++
				}catch(e){
					exceptionHandle(e,insertSql,it)
				}
			}
		}
		println "테이블 ${tableName}에 $success 건의 데이터가 입력되었습니다 "
		return success
	}

	/** Map 내용 전체가 입력된다. 
	 * ex) db.delete('메타컬럼01').insertListMap('메타컬럼01', new ERWinToXls(DIR+'Columns').convert()) */
	public int insertListMap(tableName,List listMap){
		def columnNames = listMap[0].keySet()
		return insertListMap(tableName,columnNames,listMap)
	}
}
