package erwins.util.spring.batch;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.batch.item.ExecutionContext;


/**
 * serializeContext 가 provate라서  애도 걍 복사함. ㅅㅂ 답이 없네 이거 
 * @author sin
 */
public class JdbcExecutionContextDaoForTempData extends JdbcExecutionContextDao_Copy {

	@Override
	protected String serializeContext(ExecutionContext ctx) {
		Map<String, Object> m = new HashMap<String, Object>();
		for (Entry<String, Object> me : ctx.entrySet()) {
			if(me.getValue() instanceof Map) continue;
			m.put(me.getKey(), me.getValue());
		}
		return serializer.serialize(m);
	}
}
