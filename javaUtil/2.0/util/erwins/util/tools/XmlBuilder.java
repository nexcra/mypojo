package erwins.util.tools;

import org.apache.ecs.xml.XML;
import org.apache.ecs.xml.XMLDocument;


/**
 * 간단한 XML 생성기
 */
public abstract class XmlBuilder{
    
    public XML addXml(XML body,String title){
        XML newXml = new XML(title);
        body.addElement(newXml);
        return newXml;
    }
    
    public XML setText(XML body,String title,String text){
        XML newXml = new XML(title);
        newXml.setTagText(text);
        body.addElement(newXml);
        return newXml;
    }
    
    public XML setCData(XML body,String title,String text){
        XML newXml = new XML(title);
        newXml.setTagText("<![CDATA["+text+"]]>");
        body.addElement(newXml);
        return newXml;
    }
    
    /** 테스트 필요 */
    public String buildXml(XML xml) {
    	XMLDocument doc = new XMLDocument();
    	doc.addElement(xml);
        return doc.toString();
    }
    
    /** 나중에 Document로 고칠것 */
    @Deprecated
    public String makeXml(XML xml) {
        xml.setPrettyPrint(true);
        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"+xml.toString(); // \n 조심
    }

}
