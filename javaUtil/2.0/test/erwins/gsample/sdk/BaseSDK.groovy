package erwins.gsample.sdk



import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.junit.Test

import groovy.lang.Delegate;
import groovy.lang.GroovyShellimport groovy.lang.Lazy;

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
	@Test
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
}

