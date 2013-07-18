package erwins.util.spring.batch;

import java.io.IOException;
import java.util.List;

import javax.xml.stream.events.EndDocument;
import javax.xml.stream.events.StartDocument;
import javax.xml.stream.events.XMLEvent;
import javax.xml.transform.Source;
import javax.xml.transform.stax.StAXSource;

import org.springframework.batch.item.xml.stax.DefaultFragmentEventReader;
import org.springframework.oxm.Unmarshaller;
import org.springframework.oxm.XmlMappingException;

import com.google.common.collect.Lists;
import com.sun.xml.internal.stream.events.CharacterEvent;
import com.sun.xml.internal.stream.events.EndElementEvent;
import com.sun.xml.internal.stream.events.StartElementEvent;

import erwins.util.lib.ReflectionUtil;

/** vo 변환에 시간이 많이 걸릴경우 스래드 세이프하게 읽으면서, 빠른 성능을 내려면 기본 데이터만 읽고, 나머지는 별도 스래드에서 실행해야 한다.
 * ex) 복잡한 정규식 필터, DB재조회 등등  */
public abstract class StaxDefaultUnmarshaller<T> implements Unmarshaller{
    
    /** 나중에 추가할것 */
    @Override
    public T unmarshal(Source source) throws IOException, XmlMappingException {
        StAXSource StAXSource = (javax.xml.transform.stax.StAXSource) source;
        DefaultFragmentEventReader reader = (DefaultFragmentEventReader) StAXSource.getXMLEventReader();
        while(reader.hasNext()){
            XMLEvent event = (XMLEvent) reader.next();
            if(event instanceof StartDocument) continue; //이 2가지는 무조건 호출된다. 왜인지 몰라
            if(event instanceof EndDocument) continue; //이 2가지는 무조건 호출된다. 왜인지 몰라
            if(event instanceof StartElementEvent){
            	StartElementEvent see = (StartElementEvent) event;
                List<XMLEvent> events = Lists.newArrayList();
                while(true){
                	XMLEvent next = (XMLEvent) reader.next();
                	CharacterEvent text = null;
                	if(next instanceof EndElementEvent){
                		EndElementEvent endEvent = (EndElementEvent) next;
                		if(endEvent.getName().equals(see.getName())){
                			return eventToVo(new XmlStreamEvent(see, events, text));
                		}
                	}else if(next instanceof CharacterEvent){
            			text = (CharacterEvent)text;
            		}
                	events.add(next);
                }
            }
        }
        return null;
    }
    
    protected abstract T eventToVo(XmlStreamEvent event);
    
    public static class XmlStreamEvent{
    	/** 루트 태그. 여기서 attr 추출 가능 */
    	public final StartElementEvent start;
    	/** 루트 태그 이하의 이벤트들. map으로 변경 가능 */
    	public final List<XMLEvent> events;
    	/** 루트 태그의 텍스트 */
    	public final CharacterEvent text;
		private XmlStreamEvent(StartElementEvent start, List<XMLEvent> events, CharacterEvent text) {
			super();
			this.start = start;
			this.events = events;
			this.text = text;
		}
    }
    
    private Class<T> genericClass = ReflectionUtil.genericClass(this.getClass(), 0);

	@Override
	public boolean supports(Class<?> arg0) {
		return genericClass.isAssignableFrom(arg0);
	}

}
