
package erwins.util.tools;

import java.io.File;
import java.io.InputStream;
import java.util.*;

import javax.xml.parsers.*;

import org.w3c.dom.*;
import org.xml.sax.InputSource;

import erwins.util.lib.Sets;

/**
 * org.w3c.dom의 Document를 래핑한다. 아.. 한글 포기. 쓰기는 자유로우나 입력 받을때 다 깨짐.
 * 그루비 때문에 사용되긴 힘들듯.
 * @author  erwins(my.pojo@gmail.com)
 */
public class DocParser{

    /**
     * @uml.property  name="doc"
     */
    Document doc = null;
    StringBuilder xmlString;
    
    DocumentBuilderFactory factory;
    DocumentBuilder builder;
    
    public DocParser(InputStream stream){                
        try {
            init();
            this.doc =  builder.parse(stream);
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    @Deprecated
    public DocParser(InputSource source){                
        try {
            init();
            this.doc = builder.parse(source);
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    public DocParser(File file){                
        try {
            init();
            this.doc = builder.parse(file);
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    private void init() throws ParserConfigurationException{
        factory = DocumentBuilderFactory.newInstance();
        factory.setValidating(false); //??
        factory.setNamespaceAware(true); //??
        builder = factory.newDocumentBuilder();
        //factory.setIgnoringElementContentWhitespace(true); //공백 무시
    }
    
    /**
     * tagName으로 doc의 속성을 가져와 HashMap에 담은 후 List로 반환한다. 
     * tagName에 속한 그룹은 하나의 아이템으로 간주한다.
     */
    public List<HashMap<String,String>> getElementsByTagName(String ... tagNames){
        HashMap<String,List<String>> map = new HashMap<String,List<String>>();
        
        for(String str : tagNames){
            NodeList qwe =  doc.getElementsByTagName(str);
            List<String> thisList = new ArrayList<String>();
            for(int i=0;i<qwe.getLength();i++){                 
                Element x = (Element)qwe.item(i);
                thisList.add(getValue(x));
            }
            map.put(str, thisList);            
        }
        
        return Sets.swap(map);
    }
    
    /**
     * tagName으로 doc 속성을 가져와 HashMap에 담아 리턴한다. 
     * tagName은 반드시 하나만을 리턴해야 한다.
     */
    public HashMap<String,String> getElementByTagName(String ... tagNames){
        HashMap<String,String> map = new HashMap<String,String>();
        doc.getDocumentElement().normalize();
        for(String str : tagNames){
            NodeList qwe =  doc.getElementsByTagName(str);
            Element x = (Element)qwe.item(0);
            map.put(str,getValue(x));
        }
        return map;
    }
    
    /**
     * 값이 없으면 첫번째 attribute를 찾아서 리턴한다.
     */
    private String getValue(Element element){
        String value = element.getTextContent();
        if(value.equals("")){
            NamedNodeMap at =  element.getAttributes();   
            value = at.item(0).getNodeValue();
        }
        //return URLDecoder.decode(value, "UTF-8");
        return value;
    }
    
    /**
     * @return
     * @uml.property  name="doc"
     */
    public Document getDoc(){
        return doc;
    }

    
    /**
     * Doc을 XML 형태로 리턴 
     * 가라 제작임.
     */
    public String print(){
        xmlString = new StringBuilder();
        return  selfLoop(doc);
    }
    
    private String selfLoop(Node node) {
        
        int type = node.getNodeType();

        switch (type) {
            case Node.DOCUMENT_NODE:
                xmlString.append("<?xml version='1.0' encoding='euc-kr' ?>\n"); //?????
                selfLoop(((Document) node).getDocumentElement());
                break;
            case Node.ELEMENT_NODE:
                xmlString.append("<");
                xmlString.append(node.getNodeName());
                NamedNodeMap attrs = node.getAttributes();
                for (int i = 0; i < attrs.getLength(); i++) {
                    Node attr = attrs.item(i);
                    xmlString.append(" ");
                    xmlString.append(attr.getNodeName());
                    xmlString.append("='");
                    xmlString.append(attr.getNodeValue());
                    xmlString.append("'");
                }
                xmlString.append(">");
                NodeList children = node.getChildNodes();
                if (children != null) {
                    for (int i = 0; i < children.getLength(); i++) {
                        selfLoop(children.item(i));
                    }
                }
                break;
            case Node.CDATA_SECTION_NODE:
                xmlString.append("<![CDATA [");
                xmlString.append(node.getNodeValue());
                xmlString.append("]]>");
                break;
            case Node.TEXT_NODE:
                xmlString.append(node.getNodeValue());
                break;
            case Node.PROCESSING_INSTRUCTION_NODE: //??
                xmlString.append("<?");
                xmlString.append(node.getNodeName());
                xmlString.append(" ");
                xmlString.append(node.getNodeValue());
                xmlString.append("?>");
                break;
        }

        if (type == Node.ELEMENT_NODE) {
            xmlString.append("</");
            xmlString.append(node.getNodeName());
            xmlString.append(">");
        }
        return xmlString.toString();
    }
    
    //참고용
    /*
    private static void getNode(Node n) {
        for (Node ch = n.getFirstChild(); ch != null; ch = ch.getNextSibling()) {
            if (ch.getNodeType() == Node.ELEMENT_NODE) {
                ch.getNodeName());
                getNode(ch);
            } else if (ch.getNodeType() == Node.TEXT_NODE && ch.getNodeValue().trim().length() != 0) {
                ch.getNodeValue());
            }
        }
    }
    */
}
