package erwins.test.runAndTakeResult

import org.junit.Test
import groovy.sql.Sql
import org.junit.BeforeClassimport erwins.util.lib.StringUtil
/** 그닥 좋지는 않다. */
public class __ConstraintTestonstraintTest{
    
    static Sql db ;
    
    @BeforeClass
    static void setUpBeforeClass(){
        db = Sql.newInstance('jdbc:oracle:thin:@218.156.67.18:1521:erwins','erwins','erwins','oracle.jdbc.driver.OracleDriver');
    }
    
    @Test
     public void gsql(){
        //def book = db.dataSet('TB_BOOK');
        db.eachRow('select * from TB_BOOK'){ book->
            assert book.book_name;
        }
        List books = db.rows('select book_name from TB_BOOK');
        assert books.size() > 100;
        //println books.findAll{Strings.containsIgnoreCase(it.book_name,'java')}.collect{"책이름 : ${it.book_name}"}.join('\n');
        books.findAll{StringUtil.containsIgnoreCase(it.book_name,'java')}.each{
            assert it.book_name.toUpperCase().contains('JAVA') 
        };
     }
}

