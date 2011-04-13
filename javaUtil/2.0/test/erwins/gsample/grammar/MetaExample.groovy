package erwins.gsample.grammar



import org.junit.Test
import erwins.util.tools.*

/** 쓸만한 메타데클래스 모음집 */
public class MetaExample{

    @Test
    public void search(){
		/** 요거 굿 */
		String.metaClass.swapCase = {
			->
			def sb = new StringBuffer()
			delegate.each {
				sb << (Character.isUpperCase(it as char) ? Character.toLowerCase(it as char) : 
						Character.toUpperCase(it as char))
			}
			sb.toString()
		}
		assert  "asEf".swapCase() == "ASeF"
   }
}

