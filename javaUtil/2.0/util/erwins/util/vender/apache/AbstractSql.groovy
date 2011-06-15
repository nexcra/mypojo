package erwins.util.vender.apache


import groovy.sql.Sql

/** 벤더별로 만들어 사용하자.
 *  sql.execute 로 테이블생성 등이 가능 */
public abstract class AbstractSql{
	
	@Delegate
	protected Sql db; 
	
	/** GString이 들어와도 정상작동하게 변경 */
	public list(it){ db.rows(it.toString())}
	/** 한개만 리턴 */
	public one(it){ list(it)[0]}
	public oneValue(it){ list(it)[0].iterator().next().value}
	/** tables.each { it['COUNT'] = db.count(it.TABLE_NAME) } 요런식으로 활용하자 */
	public count(tableName){ one("select COUNT(*) as COUNT from $tableName ")['COUNT']}
}


// allow resultSets to be able to be changed. 즉  => DataSet이나 eachRow 중 데이터의 변경이 가능하다.
//sql.resultSetConcurrency = java.sql.ResultSet.CONCUR_UPDATABLE

//DataSet book = db.dataSet("TB_BOOK");  //데어터셑을 직접 컨트롤 한다.
//book.add(book_name:"ㅋㅋㅋ");  //손쉬운 insert
//book.each{} //조회

// reset resultSetsConcurrency back to read only (no further changes required)  데이터 변경 못하게 막는다.
//sql.resultSetConcurrency = java.sql.ResultSet.CONCUR_READ_ONLY

/* SP 역시 간단하게 가능하다.
ql.call '{call Hemisphere(?, ?, ?)}', ['Guillaume', 'Laforge', Sql.VARCHAR], { dwells ->
	println dwells // => Northern Hemisphere
}*/