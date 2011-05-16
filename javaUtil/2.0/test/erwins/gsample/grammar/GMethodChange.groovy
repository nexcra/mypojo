package erwins.gsample.grammar



import org.junit.Test
import erwins.util.tools.*

/** 동적으로 메소드 변경 가능. */
public class GMethodChange{
	
	class Person {
		String name = "Fred"
	 }
	
	/** 동적 메타클래스도 가능 */
	@Test
	public void test(){
		def methodName = "Bob"
		Person.metaClass."changeNameTo${methodName}" = {-> delegate.name = "Bob" }
		def p = new Person()
		assert "Fred" == p.name
		p.changeNameToBob()
		assert "Bob" == p.name
		
		//샘플
		Sql.metaClass."list"  = { delegate.rows(it.toString())  } //GString이 들어와도 정상작동하게 변경
		//이하는 오라클에서만 동작
		Sql.metaClass."paging"  = { sql,pageSize,pageNo -> 
			int startNo = pageSize * (pageNo-1) +1;
			int endNo = pageSize * pageNo;
			delegate.list("SELECT * FROM (SELECT inner.*,ROWNUM \"PAGE_RN\" FROM ( $sql ) inner) WHERE PAGE_RN BETWEEN $startNo AND $endNo")
		}
		Sql.metaClass."countForOracle"  = { 
			delegate.list('SELECT * FROM user_tables').each { 
				println "$it.TABLE_NAME : "+db.list("select count(*) from $it.TABLE_NAME")[0]['COUNT(*)'] 
			}
		}
	}
	

	/** 캐시  최적화가 아마 1.5 이후에 진행된듯하다. 1.5이전은 안해도 정상작동한다. */
    @Test
    public void search(){
        
        assert 1 == new SampleController().action();
        
        SampleDomain.metaClass.sample= {-> 2}
        assert 2 == new SampleController().action();
         
        
        SampleDomain.metaClass.sample={-> 3}
		assert 2 == new SampleController().action(); //캐시에 남아있기 때문에 여전히 2로 나온다.
		
		SampleDomain.metaClass = null //invalidate해 주자. 이러면 다시 클래스가 로드?된다. 
		SampleDomain.metaClass.sample={-> 3}
        assert 3 == new SampleController().action() //원하던 값인 3이 나온다.
   }
}

/** 자동으로 마지막 인자가 리턴된다. */
class SampleDomain{
	public sample() {1}
}
 
class SampleController{
	def action = { new SampleDomain().sample() }
}

/**
 * 아래의 엄청난 짓도 가능하다.. ㅋㅋ
 * def codecs = classes.findAll { it.name.endsWith('Codec') }

codecs.each { codec ->
    Object.metaClass."encodeAs${codec.name-'Codec'}" = { codec.newInstance().encode(delegate) }
    Object.metaClass."decodeFrom${codec.name-'Codec'}" = { codec.newInstance().decode(delegate) }
}
 */
 

