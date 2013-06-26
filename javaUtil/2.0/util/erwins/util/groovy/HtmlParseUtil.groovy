package erwins.util.groovy




/**  ex) def html = new XmlSlurper(new SAXParser()).parseText(text)
 * Node에  attributes() 해야 속성조절이 가능하다. */
public class HtmlParseUtil{

	/** 까먹기 말기용
	 * ex) def list = html.BODY[0].DIV[1]  */
	public static void printChild(node,search=''){
		node.childNodes().each {
			int size = it.childNodes().size()
			boolean match =  StringUtil.isMatch(it.text(), search);
			println it.name() + ' : ' + size + ' : ' + it.attributes['id'] + ' : ' + match
		}
	}
	
}

