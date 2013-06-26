package erwins.util.spring.batch;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;

import javax.xml.namespace.QName;
import javax.xml.stream.events.EndDocument;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartDocument;
import javax.xml.stream.events.XMLEvent;
import javax.xml.transform.Source;
import javax.xml.transform.stax.StAXSource;

import org.springframework.batch.item.xml.stax.DefaultFragmentEventReader;
import org.springframework.oxm.Unmarshaller;
import org.springframework.oxm.XmlMappingException;

import com.sun.xml.internal.stream.events.StartElementEvent;

/** StAXSource를 지원하는 간단 언마샬러.
 * 스프링배치가 XStream를 기본 지원하지만, 리플렉션 기반이라 더럽게 느리기 때문에 대체한다.  */
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
        DefaultFragmentEventReader r = (DefaultFragmentEventReader) StAXSource.getXMLEventReader();
        while(r.hasNext()){
            XMLEvent o = (XMLEvent) r.next();
            if(o instanceof StartDocument) continue;
            if(o instanceof EndDocument) continue;
            if(o instanceof EndElement) continue;
            if(o instanceof StartElementEvent){
                StartElementEvent see = (StartElementEvent) o;
                T object = elementToObject(see);
                return object;
            }
        }
        return null;
    }
    
    protected abstract T elementToObject(StartElementEvent see);
    
    /** 특수한경우 확장할것 */
    @Override
    public boolean supports(Class<?> clazz) {
    	return persistentClass.isAssignableFrom(clazz);
    }
    
    /** 간단 유틸 */
    protected static String getAttributeValue(StartElementEvent see,QName name) {
        return see.getAttributeByName(name).getValue();
    }
    

}
