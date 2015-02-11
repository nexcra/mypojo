package erwins.util.spring.batch;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;
import java.util.Map.Entry;

import lombok.Data;

import org.springframework.batch.core.repository.ExecutionContextSerializer;
import org.springframework.batch.core.repository.dao.XStreamExecutionContextStringSerializer;
import org.springframework.beans.factory.InitializingBean;

import com.google.common.collect.Maps;


/** 
 * 특정 키나 타입을 시리얼라이즈 하고싶지 않을때 사용한다.
 * 기본설정으로 타입이 Map일 경우 시리얼라이즈 하지 않음  (JdbcExecutionContextDao 참조)
 * */
@Data
public class DelegateExecutionContextSerializer implements ExecutionContextSerializer,InitializingBean{
	
	private ExecutionContextSerializer delegate;
	
	@Override
	public void afterPropertiesSet() throws Exception {
		if(delegate==null){
			XStreamExecutionContextStringSerializer defaultSerializer = new XStreamExecutionContextStringSerializer();
			defaultSerializer.afterPropertiesSet();
			delegate = defaultSerializer;
		}
	}
	
	@Override
	public void serialize(Map<String, Object> object, OutputStream outputStream) throws IOException {
		Map<String, Object> newMap = Maps.newHashMap();
		for(Entry<String, Object> entry : object.entrySet()){
			Object value = entry.getValue();
			if(value instanceof Map) continue;
			newMap.put(entry.getKey(), value);
		}
		delegate.serialize(newMap, outputStream);
	}

	/**  ByteArrayInputStream in = new ByteArrayInputStream(serializedContext.getBytes("ISO-8859-1"));   */
	@Override
	public Map<String, Object> deserialize(InputStream inputStream) throws IOException {
		return delegate.deserialize(inputStream);
	}
	
	public Map<String, Object> deserialize(String context) throws IOException {
		ByteArrayInputStream in = new ByteArrayInputStream(context.getBytes("ISO-8859-1"));
		return delegate.deserialize(in);
	}
	


}
