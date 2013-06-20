package erwins.gsample.sdk



import org.junit.Test

import groovy.lang.GroovyShell
public class SimpleTemplateEngineUse{
    
	/** 배열도 가능한 템플릿 엔진~ 자바에서도 써보자.
	* 엔진은 '''로 감싸야 GString이 적용되지 않는다. */
   @Test
   public void template(){
	   def mailReminder = '''
	   Dear ${salutation?salutation+' ':''}$lastname,
	   another month has passed and again it's time for these
	   <%=tasks.size()%> tasks:
	   <% tasks.each { %>- $it
	   <% } %>
	   your collaboration is very much appreciated
	   '''

	   def engine   = new groovy.text.SimpleTemplateEngine();
	   //def engine   = new groovy.text.SimpleTemplateEngine(true);  //로그.
	   def template = engine.createTemplate(mailReminder)
	   def binding  = [
		   salutation: 'Mrs.',
		   lastname  : 'Davis',
		   tasks     : ['visit the Groovy in Action (GinA) page',
						'chat with GinA readers']
	   ]
	   println template.make(binding)
	   assert template.make(binding).toString().trim().startsWith("Dear")
	   /*
	   assert template.make(binding).toString() == '''
	   Dear Mrs. Davis,
	   another month has passed and again it's time for these
	   2 tasks:
	   - visit the Groovy in Action (GinA) page
	   - chat with GinA readers
		
	   your collaboration is very much appreciated
	   '''
	   */
   }
    
}

