package erwins.gsample.sdk


import java.sql.Connection;

import erwins.util.lib.StringUtil;
import groovy.sql.DataSet;
import groovy.sql.Sql;

import org.junit.BeforeClass;
import org.junit.Test
/** 환상적인 그루비의 SQL 지원~ 굿. */
public class GSQL{
	
	static Sql db ;

	@BeforeClass
	static void setUpBeforeClass(){
		db = Sql.newInstance('jdbc:oracle:thin:@121.161.186.xxx:1522:sid','XXX','XXX','oracle.jdbc.driver.OracleDriver');
	}
	
	/** SM할때 유용하게 사용하자~ PL/SQL보다 100만배 정도 좋다. */
	public void gsql(){
		db.connection.autoCommit = false
		def c = db.executeUpdate("update tb_book set grade = grade+1 where book_name like '%java%' ")
		db.eachRow("select * from tb_book where book_name like '%java%'"){ println it.grade  }
	}
	
	/*
	 * 테이블 생성 
	sql.execute('''create table PERSON (
		id integer not null primary key,
		firstname varchar(20),
		lastname varchar(20),
		location_id integer,
		location_name varchar(30)
	)''') 
	또는 이렇게
	def typeMap = new TypeMap()
        def build = new RelationalBuilder(typeMap)
        def sqlGenerator = new SqlGenerator(typeMap,System.getProperty( "line.separator", "\n" ))

        def database = build.database(name:'genealogy') {
          table(name:'event') {
              column(name:'event_id', type:'integer', size:10, primaryKey:true, required:true)
              column(name:'description', type:'varchar', size:30)
          }
          table(name:'individual') {
            column(name:'individual_id', type:'integer', size:10, required:true, primaryKey:true, autoIncrement:true)
            column(name:'surname', type:'varchar', size:15, required:true)
            column(name:'event_id', type:'integer', size:10)
            foreignKey(foreignTable:'event') {
                reference(local:'event_id',foreign:'event_id')
            }
            index(name:'surname_index') {
                indexColumn(name:'surname')
            }
          }
        }
	*
	*/
	
	// allow resultSets to be able to be changed. 즉  => DataSet이나 eachRow 중 데이터의 변경이 가능하다.
	//sql.resultSetConcurrency = java.sql.ResultSet.CONCUR_UPDATABLE
	
	//DataSet book = db.dataSet("TB_BOOK");  //데어터셑을 직접 컨트롤 한다.
	//book.add(book_name:"ㅋㅋㅋ");  //손쉬운 insert
	//book.each{} //조회
	/*
	db.eachRow('select * from tb_book'){
		println it.book_name;
	};*/
	/*
	List books = db.rows('select book_name from TB_BOOK');
	assert books.size() > 100;
	//println books.findAll{Strings.containsIgnoreCase(it.book_name,'java')}.collect{"책이름 : ${it.book_name}"}.join('\n');
	   books.findAll{Strings.containsIgnoreCase(it.book_name,'java')}.each{
		assert it.book_name.toUpperCase().contains('JAVA')
	};*/

	// reset resultSetsConcurrency back to read only (no further changes required)  데이터 변경 못하게 막는다.
	//sql.resultSetConcurrency = java.sql.ResultSet.CONCUR_READ_ONLY

	/* SP 역시 간단하게 가능하다.
	ql.call '{call Hemisphere(?, ?, ?)}', ['Guillaume', 'Laforge', Sql.VARCHAR], { dwells ->
		println dwells // => Northern Hemisphere
	}*/
    
}
