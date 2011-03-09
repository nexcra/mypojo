
package erwins.util.vender.apache;

import org.apache.ecs.xml.XML;
import org.apache.ecs.xml.XMLDocument;


/**
 * XML을 래핑 , attribute는 |로 연결된다.
 * 2단 까지만 지원된다. ㅋ
 * 이거 하지 전에 bean to XML을 먼저 고려할것!!
 */
public class Xmls{
    
    XML root;
    XML node;
    
    public Xmls(String root){
        this.root = new XML(root);
    }
    
    public void add(String tagName,String ... attributes){
        node = new XML(tagName);
        setAttribute(node,attributes);
        root.addElement(node);
    }
    public void addItem(String tagName,String body,String ... attributes){
        XML xml = new XML(tagName);
        xml.setTagText(body);
        setAttribute(xml,attributes);
        node.addElement(xml);
    }
    
    private static void setAttribute(XML xml , String ...attributes ){
        for(String attribute : attributes){
            String[] temp = attribute.split("\\|");
            if(temp.length != 2) throw new RuntimeException(attribute + " : attributes는 한쌍의 값이 |로 연결되어야 함.");
            xml.addAttribute(temp[0], temp[1]);
        }        
    }
    
    /*
    public void write(HttpServletResponse response){
        XMLDocument x = new XMLDocument();
        x.setCodeset("UTF-8");
        x.addElement(root);
        x.output(Writers.getWriter(response));
    }*/
    
    @Override
    public String toString(){
        XMLDocument x = new XMLDocument();
        x.setCodeset("UTF-8");
        x.addElement(root);
        return x.toString();
    }
    

}
