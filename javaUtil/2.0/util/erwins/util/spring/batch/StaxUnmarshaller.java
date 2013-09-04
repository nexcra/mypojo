package erwins.util.spring.batch;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
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
import com.sun.xml.internal.stream.events.EndElementEvent;
import com.sun.xml.internal.stream.events.StartElementEvent;

/** StAXSource를 지원하는 간단 언마샬러.
 * 스프링배치가 XStream를 기본 지원하지만, 리플렉션 기반이라 더럽게 느리기 때문에 대체한다.
 * 
 *  이제 사용하지 않는다. 나중에 삭제할것
 * @see StaxDefaultUnmarshaller */
@Deprecated
public abstract class StaxUnmarshaller<T> implements Unmarshaller{
	
	private Class<T> persistentClass;
    
    @SuppressWarnings("unchecked")
	public StaxUnmarshaller() {
        this.persistentClass = (Class<T>)((ParameterizedType)getClass().getGenericSuperclass()).getActualTypeArguments()[0];
    }
    
    /** 나중에 추가할것 */
    @Override
    public T unmarshal(Source source) throws IOException, XmlMappingException {
        StAXSource StAXSource = (javax.xml.transform.stax.StAXSource) source;
        DefaultFragmentEventReader reader = (DefaultFragmentEventReader) StAXSource.getXMLEventReader();
        while(reader.hasNext()){
            XMLEvent event = (XMLEvent) reader.next();
            if(event instanceof StartDocument) startDocumentEvent((StartDocument)event); //이 2가지는 무조건 호출된다. 왜인지 몰라
            if(event instanceof EndDocument) continue; //이 2가지는 무조건 호출된다. 왜인지 몰라
            if(event instanceof StartElementEvent){
            	StartElementEvent see = (StartElementEvent) event;
                List<XMLEvent> events = Lists.newArrayList();
                while(true){
                	XMLEvent next = (XMLEvent) reader.next();
                	if(next instanceof EndElementEvent){
                		EndElementEvent endEvent = (EndElementEvent) next;
                		if(endEvent.getName().equals(see.getName())) break;
                	}
                	events.add(next);
                }
                return elementToObject(see,events);
            }
        }
        return null;
    }
    
    /** 필요하다면 오버라이드 하자. */
    protected  void startDocumentEvent(StartDocument startDocument){
    	//아무것도 하지 않는다.
    }
    
    protected abstract T elementToObject(StartElementEvent see,List<XMLEvent> events);
    
    /** 특수한경우 확장할것 */
    @Override
    public boolean supports(Class<?> clazz) {
    	return persistentClass.isAssignableFrom(clazz);
    }
    

}
