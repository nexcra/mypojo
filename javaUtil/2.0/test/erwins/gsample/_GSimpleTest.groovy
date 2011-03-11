package erwins.gsample

import erwins.util.jdbc.TableInfos.TableInfo;
import erwins.util.vender.apache.Poi;
import groovy.swing.SwingBuilder 

import org.codehaus.groovy.gfreemarker.FreeMarkerTemplateEngine;
import org.codehaus.groovy.gfreemarker.IGroovyFreeMarkerPlugin;
import org.junit.Test

/** 그닥 좋지는 않다. */
public class _GSimpleTest{
	
	class urlencoder implements IGroovyFreeMarkerPlugin {
		String transform(Map params, String content) {
				content + 'asd'
		}
	}	
	

    @Test
	public void gsql(){
    	def tpl = '''
		Hello, ${user.name}
		this is a test ${user.name}'''
		println 'asd'
		def engine = new FreeMarkerTemplateEngine("D:/PROJECT/workspace/mysysbrain/test/erwins/gsample")
		println 'asd'
		def binding = ["user" : ["name":"cedric"]]
		println engine.createTemplate(tpl).make(binding)
	}
}





