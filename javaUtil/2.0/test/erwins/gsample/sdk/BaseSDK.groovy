package erwins.gsample.sdk



import erwins.util.collections.MapType
import groovy.transform.AutoClone
import groovy.transform.InheritConstructors
import groovy.transform.ToString

import java.text.SimpleDateFormat
import java.util.concurrent.locks.Lock
import java.util.concurrent.locks.ReentrantLock

import org.junit.Test

//@Log  이거면 멤버필드에 log가 자동추가
public class BaseSDK{
    
	/** file 자체 재귀함수가 있다... 그뿐. */
	@Test
	public void file(){
		File root = new File("D:/PROJECT/file");
		def files = [];
		root.eachFileRecurse{if(it.directory) files << it.name};
		assert files != '';
	}

	/** 아에 싱글톤을 문법적으로 자체 지원한다.. 굿! */
	@Singleton(lazy=true)
	class GSingleton{
		public String p = 'test';
	}
	@Test
	public void Singleton(){
		assert GSingleton.instance.p == 'test'
	}
	
	/** Lazy로딩 지원! 멤버필드를 실제 필요할때 생성/초기화 한다. */
	class Person {
		@Lazy pets = ['Cat', 'Dog', 'Bird'];
		//@Lazy List pets = { /* complex computation here */ }()
	}
	@Test
	public void lazy(){
		def p = new Person()
		assert !(p.dump().contains('Cat'))
		
		assert p.pets.size() == 3
		assert p.dump().contains('Cat')
	}
	
	/** 자동으로 위임을 해준다. 대박!! */
	class Event {
		@Delegate Date when
		String title, url
	}
	/** 여러개도 가능하다. */
	class LockableList {
		@Delegate private List list = []
		@Delegate private Lock lock = new ReentrantLock()
	}
	/** ㅅㅂ map생성자 사라진듯? */
	//@Test
	public void delegate(){
		def df = new SimpleDateFormat("yyyy/MM/dd")
		
		def gr8conf = new Event(title: "GR8 Conference",
				url: "http://www.gr8conf.org",
				when: df.parse("2009/05/18"))
		def javaOne = new Event(title: "JavaOne",
				url: "http://java.sun.com/javaone/",
				when: df.parse("2009/06/02"))
		
		assert gr8conf.before(javaOne.when)
		assert javaOne.when instanceof Date; //자동으로 @Delegate의 인터페이스를 구현한다. 굿~

		
		def list = new LockableList()  //이런게 바로 완전체!
		list.lock()
		try {
			list << 'Groovy'
			list << 'Grails'
			list << 'Griffon'
		} finally {
			list.unlock()
		}

		assert list.size() == 3
		assert list instanceof Lock
		assert list instanceof List
	}
	
	/** 트램폴린 / 자체 재귀함수호출을 가능하게한다. */
	@Test
	public void trampoline(){
		def factorial
		factorial = { int n, BigInteger accu = 1G ->
			if (n < 2) return accu
			factorial.trampoline(n - 1, n * accu) //재귀호출을 한다.
		}
		factorial = factorial.trampoline()
		
		assert factorial(1)    == 1
		assert factorial(3)    == 1 * 2 * 3
	}
	/** 클로저끼리 링크가 가능하다 */
	@Test
	public void closureComposition(){
		def plus2  = { it + 2 }
		def times3 = { it * 3 }
		
		def times3plus2 = plus2 << times3
		assert times3plus2(3) == 11
		assert times3plus2(4) == plus2(times3(4))
	}
	/** 클로저 실행을 캐싱할 수 있다. */
	@Test
	public void memoization(){
		//def plus = { a, b -> sleep 1000; a + b }.memoize()
		def plus = { a, b -> sleep 100; a + b }.memoize()
		assert plus(1, 2) == 3 // after 1000ms
		assert plus(1, 2) == 3 // return immediately
		assert plus(2, 2) == 4 // after 1000ms
		assert plus(2, 2) == 4 // return immediately
	}
	/** etc  */
	@Test
	public void etc(){
		//자동으로 자식객체까지 클론된다.
		Person p = new Person()  
		//as~~~  테스트 생략 (귀찮아)
		
		//toString하면 생성자처렴 보여준다
		Person2 p2 = new Person2(name: 'Pete', age: 15)
		println p2
		assert p2.toString() == 'erwins.gsample.sdk.Person2(name:Pete, age:15)'
		
	}
}

@AutoClone
class Person {
	String first, last
	List favItems
	Date since
	//이하 테스트용 추가
	MapType type
	Person parent
	Person2 parent2
	List<Person2> persons
}

@InheritConstructors // 상위생성자를 자동생성
@ToString(includeNames = true, includeFields = true)
class Person2 {
	String name
	int age
}

