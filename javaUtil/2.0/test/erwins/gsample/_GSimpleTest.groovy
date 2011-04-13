package erwins.gsample

import erwins.util.jdbc.TableInfos.TableInfo;
import erwins.util.vender.apache.Poi;
import groovy.swing.SwingBuilder 

import org.codehaus.groovy.gfreemarker.FreeMarkerTemplateEngine;
import org.codehaus.groovy.gfreemarker.IGroovyFreeMarkerPlugin;
import org.junit.Test

/** 그닥 좋지는 않다. */
public class _GSimpleTest{
	
	class Person {
		String name = "Fred"
	 }
	
	@Test
	public void test(){
		def methodName = "Bob"	
		Person.metaClass."changeNameTo${methodName}" = {-> delegate.name = "Bob" }
		def p = new Person()
		assert "Fred" == p.name
		p.changeNameToBob()
		assert "Bob" == p.name
	}
}





