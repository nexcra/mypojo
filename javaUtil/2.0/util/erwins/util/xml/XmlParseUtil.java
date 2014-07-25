package erwins.util.xml;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.io.IOUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import erwins.util.text.StringUtil;

/**
 * XML 간이파서.  
 * 인메모리 방식만 대응된다.
 * -> 추후 이름 바꾸고 위임객체로 변경할것
 * @author sin
 */
public class XmlParseUtil {
    
    private String encoding = "UTF-8";
    
    /** 자식 Element만 가져온다  */
    public List<Element> getChildElement(Element parent){
        List<Element> nodes = new ArrayList<Element>();
        NodeList list = parent.getChildNodes();
        for (int i = 0; i < list.getLength(); i++) {
            Node each = list.item(i);
            if(each instanceof Element) nodes.add((Element)each);
        }
        return nodes;
    }
    
    /** 기본 api를 사용하면 모든 트리내의 자식이 다 나온다. 현재 객체자식꺼만 구할때 사용  */
    public Element getChildByName(Element parent,String tagName){
        NodeList list = parent.getChildNodes();
        for (int i = 0; i < list.getLength(); i++) {
            Node each = list.item(i);
            if(each instanceof Element){
                Element el = (Element)each;
                if(el.getTagName().equals(tagName)) return el;
            }
        }
        return null;
    }
    
    /** 기본 api를 사용하면 모든 트리내의 자식이 다 나온다. 현재 객체자식꺼만 구할때 사용  */
    public List<Element> getChildrenByName(Element parent,String ... tagNames){
        List<Element> nodes = new ArrayList<Element>();
        NodeList list = parent.getChildNodes();
        for (int i = 0; i < list.getLength(); i++) {
            Node each = list.item(i);
            if(each instanceof Element){
                Element el = (Element)each;
                String tagName = el.getTagName();
                if(StringUtil.isEquals(tagName, tagNames)) nodes.add(el);
            }
        }
        return nodes;
    }
    
    /** Element만 가져온다 */
    public List<Element> parseOnlyElement(String xml){
        InputStream is = null;
        try {
            is = IOUtils.toInputStream(xml, encoding);
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(is);
            doc.getDocumentElement().normalize();
            Element root = doc.getDocumentElement();
            return getChildElement(root);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ParserConfigurationException e) {
            throw new XmlParseException("XML파싱오류 : " + e.getMessage(),e);
        } catch (SAXException e) {
            throw new XmlParseException("XML파싱오류 : " + e.getMessage(),e);
        }finally{
            IOUtils.closeQuietly(is);
        }
    }
    
    /** 파싱예외를 런타임으로 변환 */
    @SuppressWarnings("serial")
    public static class XmlParseException extends RuntimeException{

        public XmlParseException() {
            super();
        }

        public XmlParseException(String message, Throwable cause) {
            super(message, cause);
        }

        public XmlParseException(String message) {
            super(message);
        }

        public XmlParseException(Throwable cause) {
            super(cause);
        }
    }
    
    
    /** 간이로 쓰던거 걍 복사 붙여넣기 함. 나중에 수정할것  */
    public static List<Node> parse(String xml,String encoding){
        InputStream is = null;
        List<Node> nodes;
        try {
            is = IOUtils.toInputStream(xml, encoding);
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(is);
            doc.getDocumentElement().normalize();
            Element root = doc.getDocumentElement();
            NodeList list = root.getChildNodes();
            nodes = new ArrayList<Node>();
            for (int i = 0; i < list.getLength(); i++)  nodes.add(list.item(i));
        } catch (Exception e) {
            throw new XmlParseException(xml,e);
        }finally{
            IOUtils.closeQuietly(is);
        }
        return nodes;
    }

}
