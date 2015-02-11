package erwins.util.groovy


import groovy.sql.Sql

import com.google.common.base.Preconditions



public abstract class GroovyMetaUtilSql{

	public static void addMeta(){
		Sql.metaClass.rows = { GString str -> return delegate.rows(str.toString()) } //그냥하면 오류난다.  ps로 치환할 수 있는건 그걸쓰자.
		
		Sql.metaClass.columns = { String tableName -> 
			def sql = """
			SELECT a.TABLE_NAME,a.COLUMN_NAME,COMMENTS,DATA_TYPE,DATA_LENGTH,DATA_PRECISION,DATA_SCALE
			FROM user_tab_columns a JOIN USER_COL_COMMENTS b
			ON a.COLUMN_NAME = b.COLUMN_NAME AND a.TABLE_NAME = b.TABLE_NAME
			WHERE a.TABLE_NAME = '$tableName'
			ORDER BY a.TABLE_NAME, COLUMN_ID """
			return delegate.rows(sql) 
		}
		
		Sql.metaClass.insertList = { String tableName, List list ->
			def columnNames = delegate.columns(tableName).collect { it.COLUMN_NAME }
			Preconditions.checkState(columnNames.size() != 0,'$tableName : 컬럼수가 0입니다.')
			def insertSql = "INSERT INTO $tableName (" + columnNames.join(',') + ') VALUES ('+ columnNames.collect { '?' }.join(',')  +')'
			delegate.withTransaction {
				delegate.withBatch 1000,insertSql,{ ps->
					list.each { ps.addBatch(it) }
				}
			}
			int success =  list.size()
			println "테이블 ${tableName}에 $success 건의 데이터가 입력되었습니다 "
		}
		
		/**
		 * SQL을 읽으면서 커서를 수정한다.  원커밋 처리 가능한 간단 수정용. (대용량 금지)
		 * SQL구문에 .* 대신 정확한 인자를 입력하자
		 *  */
		Sql.metaClass.withUpdate = { String selectSql,Closure closure ->
			//allow resultSets to be able to be changed. 즉  => DataSet이나 eachRow 중 데이터의 변경이 가능하다.
			delegate.resultSetConcurrency = java.sql.ResultSet.CONCUR_UPDATABLE
			//delegate.resultSetType = ResultSet.TYPE_SCROLL_INSENSITIVE  <-- 이건 안해도 될듯
			try{
				delegate.eachRow(selectSql.toString()){ closure(it) }
				delegate.commit()
			}catch (Exception e) {
				delegate.rollback()
				e.printStackTrace()
			}finally{
				delegate.resultSetConcurrency = java.sql.ResultSet.CONCUR_READ_ONLY
			}
		}
		
	}
	
	
	/* ================================================================================== */
	/*                                Util                                                */
	/* ================================================================================== */
   /** 해당 테이블 컬럼순서대로 파라메터를 입력 후 사용한다.  컬럼순서가 변경되면 안되니 주의! */
   public void insertList(tableName,List list){
	   def columnNames = columns(tableName).collect { it.COLUMN_NAME }
	   insertList(tableName,columnNames,list)
   }
   
   /** 1000개씩 배치로 묶어서 인서트한다. */
   public void insertList(tableName,columnNames,List list){
	   def insertSql = "INSERT INTO $tableName (" + columnNames.join(',') + ') VALUES ('+ columnNames.collect { '?' }.join(',')  +')'
	   Sql.withTransaction {
		   db.withBatch 1000,insertSql,{ ps->
			   list.each { ps.addBatch(it) }
		   }
	   }
	   int success =  list.size()
	   println "테이블 ${tableName}에 $success 건의 데이터가 입력되었습니다 "
   }
   


}
