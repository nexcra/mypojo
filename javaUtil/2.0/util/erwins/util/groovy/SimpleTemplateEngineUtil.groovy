package erwins.util.groovy

import javax.annotation.concurrent.ThreadSafe

import com.google.common.collect.Maps

import erwins.util.lib.ReflectionUtil
import groovy.text.Template

/**
 * template.make에는 map만 지원한다. 따라서 바인딩의 1뎁스들을 하나의 map으로 매핑해서 템플릿을 완성해준다.
 *
 * <%=tasks.size()%> tasks:
	   <% tasks.each { %>- $it
	   <% } %>
	   
	   ${salutation?salutation+' ':''}
	   
 *  */
@ThreadSafe
public abstract class SimpleTemplateEngineUtil {
	
	public static String make(String text,List bindings){
		def engine   = new groovy.text.SimpleTemplateEngine();
		//def engine   = new groovy.text.SimpleTemplateEngine(true);  //로그.
		Template template = engine.createTemplate(text)
		Map map = Maps.newHashMap()
		for(Object binding : bindings){
			if( !(binding instanceof Map) ) map.putAll(ReflectionUtil.toMapAsFirstDepth(binding))
		}
		return template.make(map);
	}

}
