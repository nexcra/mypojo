package erwins.util.tools;

import java.util.Deque;

import org.apache.ecs.xml.XML;

import com.google.common.base.Preconditions;
import com.google.common.collect.Queues;


/**
 * 간단한 XML 하드코딩기.
 * ECS가 스래드 안전하지 않아 맨붕하고, 표준API도입의 위험때문에 임시로 만듬
 * 순서대로만 쓸수 있다.
 * 요구사항이 늘어날때마다 수정하자.
 * ex) XmlCoder xml = new XmlCoder().open("Results")
    		.e("ChannelID", adReq.getChannelId())
	    	.e("numResults", adReq.getImpressions().size())
	    	.cdata("Query", adReq.getKeyword())
	    	;
 * 
 * XMLBuilder 가 미친듯이 느리기때문에 대안으로도 사용된다.
 * @author sin
 */
public class XmlCoder{
	
	private StringBuilder sb = new StringBuilder();
	
	private Deque<String> endTag = Queues.newArrayDeque();
	
	private static final char START = '<';
	private static final char CLOSE = '/';
	private static final char END = '>';
	
	private static final String CDATA_START = "<![CDATA[";
	private static final String CDATA_END = "]]>";
	
	@Override
	public String toString() {
		while(endTag.size()>0){
			close();
		}
		return sb.toString();
	}
	
	public XmlCoder open(String title){
		addStartTag(title);
		endTag.add(title);
		return this;
	}
	
	public XmlCoder close(){
		String tag = endTag.pollLast();
		Preconditions.checkNotNull(tag);
		addEndTag(tag);
		return this;
	}
	
	public XmlCoder e(String title,String body) {
		addStartTag(title);
		sb.append(body);
		addEndTag(title);
		return this;
	}
	
	public XmlCoder e(String title,Number body) {
		addStartTag(title);
		sb.append(body);
		addEndTag(title);
		return this;
	}
	
	public XmlCoder cdata(String title,String body) {
		addStartTag(title);
		addCDATA(body);
		addEndTag(title);
		return this;
	}
	
	protected void addCDATA(String title) {
		sb.append(CDATA_START);
		sb.append(title);
		sb.append(CDATA_END);
	}

	protected void addStartTag(String title) {
		sb.append(START);
		sb.append(title);
		sb.append(END);
	}
	
	protected void addEndTag(String title) {
		sb.append(START);
		sb.append(CLOSE);
		sb.append(title);
		sb.append(END);
	}
	
	
    /*
    public static XML addXml(XML body,String title){
        XML newXml = new XML(title);
        if(isLocal) newXml.setPrettyPrint(true);
        body.addElement(newXml);
        return newXml;
    }
    
    public static XML setText(XML body,String title,String text){
        XML newXml = new XML(title);
        if(isLocal) newXml.setPrettyPrint(true);
        newXml.setTagText(text);
        body.addElement(newXml);
        return newXml;
    }
    
    public static XML setCData(XML body,String title,String text){
        XML newXml = new XML(title);
        if(isLocal) newXml.setPrettyPrint(true);
        newXml.setTagText("<![CDATA["+text+"]]>");
        body.addElement(newXml);
        return newXml;
    }
    */
    
    /** 나중에 Document로 고칠것 */
    public static String makeXml(XML xml) {
        //if(isLocal) xml.setPrettyPrint(true);
        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"+xml.toString(); // \n 조심
    }

}
