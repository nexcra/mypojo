
package erwins.util.vender.apache;

import org.apache.ecs.html.Span;


public abstract class HtmlTool{
    
	/** 빨간색으로 강조 */
    public static String highlight(String str){
    	Span span = new Span(str);
    	span.setStyle("color:red;font-weight:bold;");
    	return span.toString();
    }
    
    
}
