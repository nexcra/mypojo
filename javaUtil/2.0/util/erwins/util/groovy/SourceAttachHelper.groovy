package erwins.util.groovy

import java.text.MessageFormat

import org.junit.Test

import erwins.util.lib.FileUtil
import erwins.util.text.StringUtil

/** 
 * 이클립스 소스붙이기 노가다를 줄여준다. 메이븐 필요!
 * \.metadata\.plugins\org.eclipse.jst.common.frameworks 의 classpath.decorations.xml 를 수정하면 된다
 *  */
public abstract class SourceAttachHelper {

	private static final def TEMPLATE = 
"""    <entry id="{0}">
        <source-attachment-path>{1}</source-attachment-path>
    </entry>"""

	@Test
	public void test(){
		def mavenDir = new File("C:/Users/Administrator/.m2/repository");
		def libDir = new File("C:/DATA/PROJECT/erwins/erwinsMaven/WebContent/WEB-INF/lib");
		println "    <!-- 자동생성 source-attachment -->"
		buildXml(mavenDir,libDir).each {
			println it
		}
	}
	
	public static List<String> buildXml(File mavenDir,File libDir){
		/** 메이븐의 소스파일 형태로 변경 */
		def toSourceName = {
			def exts =  StringUtil.getExtentions(it)
			return exts[0]+ '-sources.' +exts[1]
		}
		def toPath = { return it.absolutePath.replaceAll("\\\\", "/") }
		
		def mavenMap = [:]
		FileUtil.iterateFiles(mavenDir).findAll {  it.name.endsWith('-sources.jar') }.each {  
			mavenMap.put it.name,it
		}
		
		def result = []
		libDir.listFiles().findAll { it.name.endsWith('.jar') }.each {
			def sourceName =  toSourceName(it.name)
			File sourceFile = mavenMap[sourceName]
			if(sourceFile!=null){
				result << MessageFormat.format(TEMPLATE,toPath(it),toPath(sourceFile))
			}
		}
		return result
	}
	

}
