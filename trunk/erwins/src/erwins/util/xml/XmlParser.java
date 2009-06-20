package erwins.util.xml;

import static org.apache.commons.lang.StringUtils.substringsBetween;

import java.util.*;

import org.apache.commons.lang.StringUtils;

import erwins.util.lib.Sets;

/**
 * 태그의 순서가 없는 간단한 xml파서이다. 
 * JDK 기본 내장 파서의 한글 지원문제 때문에 하나 만들었다.
 * 무쟈게 느리고 이스케이핑 같은거 전혀 안됨.  
 */
public class XmlParser {
    
    public HashMap<String,List<XmlElement>> map = new HashMap<String,List<XmlElement>>();    
    
    private String xml ;
    
    public XmlParser(String xml){
        this.xml = xml;
        String[] xmlSplits = substringsBetween(this.xml, "<", ">");
        for(String xmlSplit : xmlSplits){            
            if(xmlSplit.startsWith("?")) continue;
            
            String[] tagSplit = xmlSplit.split(" ",2);
            String tagname = tagSplit[0].replace("/","");
            
            if(xmlSplit.startsWith("/")){   //   
                setTagText(tagname);
            }else if(tagSplit.length > 1){
                String[] attributeSets = tagSplit[1].split("\" ");
                setAttribute(tagname, attributeSets);
            }
        }
    }

    /**
     * 루프를 순회하는동안 각각 한번씩 실행된다.
     */
    private void setAttribute(String tagname, String[] attributeSets) {
        List<XmlElement> elements =  Sets.getList(map,tagname);
        XmlElement xmlTag = new XmlElement();
        for(String attributeSet :  attributeSets){  //1부터 시작
            if(StringUtils.isEmpty(attributeSet)) continue;
            String[] keyValue = attributeSet.split("=");
            if(keyValue.length==0) continue;
            String key = keyValue[0].trim(); //찌거기 제거
            //수정의 여지가 있음
            String attribute = getAttribute(keyValue[1]);                                   
            xmlTag.attribute.put(key, attribute);                    
        }
        elements.add(xmlTag);
    }
    
    /**
     *  "/"로 끝날 수 있다. 
     *  공백 삭제 후 ""를 제거해 준다.  
     *  xml에서 문자열은 ""가 들어갈 수 없다. 
     */
    private String getAttribute(String value){
        if(value.endsWith("/")) value = StringUtils.substringBeforeLast(value, "/");
        return value.trim().replace("\"","");  
    }

    /**
     * 태그의 몸통 부분인 TagText를 얻는다. 
     * "/"로 시작하면 tagText가 있을 수 있다.
     * tagname을 만날때 마다 TagText를 검색해서 입력해준다.
     * 한번 입력한것은 다시 하지 않는다.
     */
    private void setTagText(String tagname){
        List<XmlElement> elements =  Sets.getList(map,tagname);
        if(getXmlElement(elements,0).tagText.equals("")){  //TagText를 이미 들어가 있다면 pass
            String[] temp2s = substringsBetween(this.xml, "<"+tagname, "</"+tagname+">");  //끝나는 태그만 닫혀있다.
            for(int i=0;i<temp2s.length;i++){
                if(StringUtils.contains(temp2s[i],"<")) break;
                getXmlElement(elements,i).tagText = StringUtils.substringAfterLast(temp2s[i],">") ; //attribute부분을 잘라준다.
            }
        }
    }
   
    /**
     * List로 부터  XmlTag을 가져온다.
     */
    public static <T> T getXmlElement(List<T> tags , int index){
        return Sets.getEntity(tags,index,XmlElement.class);
    }

    public static class XmlElement{
        public Map<String,String> attribute = new HashMap<String,String>();
        public String tagText = "";
        public String get(){
            if(!tagText.equals("")) return tagText;
            else return attribute.values().iterator().next();
        }
        @Override
        public String toString(){
            return get();
        }
    }

    /**
     * 첫번째 인다를 리턴 
     */
    public XmlElement get(String tagName) {
        return map.get(tagName).get(0);
    }
    /**
     * 모두 리턴 
     */
    public List<XmlElement> gets(String tagName) {
        return map.get(tagName);
    }
  
}

