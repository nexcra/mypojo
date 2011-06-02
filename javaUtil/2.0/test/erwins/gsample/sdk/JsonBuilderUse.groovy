package erwins.gsample.sdk


import groovy.json.JsonBuilder
import groovy.json.JsonOutput

import org.junit.Test
public class JsonBuilderUse{

	/** 예쁘게 호출도 가능 */
	@Test
	public void build(){
		def json = new JsonBuilder()

		json.person {
			name "Guillaume"
			age 33
			pets "Hector", "Felix"
		}

		assert json.toString() == '{"person":{"name":"Guillaume","age":33,"pets":["Hector","Felix"]}}'
		assert JsonOutput.prettyPrint('''{"person":{"name":"Guillaume","age":33,"pets":["Hector","Felix"]}}''') != null
	}
}
