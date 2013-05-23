package erwins.util.vender.springBatch;

import java.io.IOException;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;
import javax.xml.transform.Result;
import javax.xml.transform.stax.StAXResult;

import org.springframework.batch.item.xml.StaxWriterCallback;
import org.springframework.oxm.Marshaller;
import org.springframework.oxm.XmlMappingException;

import com.sun.xml.internal.fastinfoset.stax.events.AttributeBase;
import com.sun.xml.internal.stream.events.EndElementEvent;
import com.sun.xml.internal.stream.events.StartElementEvent;

import erwins.util.lib.ReflectionUtil;
import erwins.util.lib.StringUtil;
import erwins.util.root.PairObject;

/** 스트리밍 쓰기를 지원하는 마샬러 */
public abstract class StaxMarshaller<T> implements Marshaller {
	
	private Class<T> persistentClass;
	private QName elementName;
    
	public StaxMarshaller() {
        this.persistentClass = ReflectionUtil.genericClass(getClass(), 0);
        this.elementName = new QName(StringUtil.uncapitalize(persistentClass.getSimpleName()));
    }

	@Override
	public void marshal(Object obj, Result arg1) throws IOException,XmlMappingException {
		@SuppressWarnings("unchecked")
		T item = (T) obj;
		StAXResult result = (StAXResult)arg1;
		XMLEventWriter writer = result.getXMLEventWriter();
		try {
			writer.add(new StartElementEvent(elementName));
			writeAttribute(writer,item);
			writer.add(new EndElementEvent());
		} catch (XMLStreamException e) {
			throw new RuntimeException(e);
		}
	}
	
	/**  new AttributeBase("a","b"); 이런식으로 추가하자 */
	protected abstract void writeAttribute(XMLEventWriter writer,T item) throws XMLStreamException;

    /** 특수한경우 확장할것 */
    @Override
    public boolean supports(Class<?> clazz) {
    	return persistentClass.isAssignableFrom(clazz);
    }

	public void setElementName(String elementName) {
		this.elementName = new QName(elementName);
	}
	
	public static XMLEvent makeAttributeBase(String name,Object value ) {
		if(value==null) value = "";
		return new AttributeBase(name, value.toString());
	}
	
	public abstract static class StaxWriterSimpleCallback implements StaxWriterCallback{
		protected String elementName;
		public StaxWriterSimpleCallback(String elementName) {
			this.elementName = elementName;
		}
		@Override
		public void write(XMLEventWriter writer) throws IOException {
			try {
				writer.add(new StartElementEvent(new QName(elementName)));
				writeAttribute(writer);
				writer.add(new EndElementEvent());
			} catch (XMLStreamException e) {
				throw new RuntimeException(e);
			}			
		}
		protected abstract void writeAttribute(XMLEventWriter writer) throws XMLStreamException;
	}
	
	/** 간단 메타정보 말고는 쓸일이 없을듯. 완료 후 상황 판단이 불가능  **/
	public static StaxWriterCallback makeCallback(final String elementName,final PairObject ... pairs ) {
		return new StaxWriterCallback(){
			@Override
			public void write(XMLEventWriter writer) throws IOException {
				try {
					writer.add(new StartElementEvent(new QName(elementName)));
					for(PairObject each : pairs){
						writer.add(new AttributeBase(each.getName(),each.getValue()));
					}
					writer.add(new EndElementEvent());
				} catch (XMLStreamException e) {
					throw new RuntimeException(e);
				}
			}
		};
	}
    
    
    
    

}