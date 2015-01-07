package erwins.util.spring.batch.component;

import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.XMLEvent;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import com.sun.xml.internal.stream.events.CharacterEvent;
import com.sun.xml.internal.stream.events.EndElementEvent;
import com.sun.xml.internal.stream.events.StartElementEvent;


public abstract class StaxUtil{
	
    /** 자료가 2단이라고 가정한다. 더 깊은 뎁스는 모 아메롱~ */
    public static Map<QName,XmlStreamData> toPlatData(List<XMLEvent> events) {
    	Map<QName,XmlStreamData> map = Maps.newHashMap();
    	StartElementEvent start = null;
    	CharacterEvent text = null;
    	for(XMLEvent each : events){
    		if(each instanceof StartElementEvent){
    			start = (StartElementEvent) each;
    			text = null;
    		}else if(each instanceof EndElementEvent){
    			EndElementEvent end = (EndElementEvent) each;
    			Preconditions.checkNotNull(start);
    			Preconditions.checkState(start.getName().equals(end.getName()));
    			map.put(start.getName(), new XmlStreamData(start, text));
    			start = null;
    			text = null;
    		}else if(each instanceof CharacterEvent){ //text는 1개만 기록된다.
    			text = (CharacterEvent)each;
    		}
    	}
    	return map;
    }
    
    /** XML은 짜증나게 text를 줄 수 있다. 이때문에 2개로 나눈다. */
    public static class XmlStreamData{
    	public final StartElementEvent start;
    	private final CharacterEvent text;
		public XmlStreamData(StartElementEvent start, CharacterEvent text) {
			super();
			this.start = start;
			this.text = text;
		}
		public String getText(String defaultValue) {
			return text ==null ? defaultValue : text.getData();
		}
		public String getText() {
			return getText(null);
		}
    }
    
    
    /** 간단 유틸 */
    public static String getNullSafeText(Map<QName,XmlStreamData> map,QName name,String defaultValue) {
    	XmlStreamData data = map.get(name);
    	return data == null ? defaultValue : data.getText();
    }
    
    /** 간단 유틸 */
    public static String getAttributeValue(StartElementEvent see,QName name) {
        return getAttributeValue(see,name,null);
    }
    
    /** 간단 유틸 */
    public static String getAttributeValue(StartElementEvent see,QName name,String defaultValue) {
    	Attribute attr = see.getAttributeByName(name);
    	if(attr==null) return defaultValue;
        return attr.getValue();
    }
    

}
