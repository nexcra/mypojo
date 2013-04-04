package erwins.util.guava;

import java.util.Collection;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;

import com.google.common.eventbus.DeadEvent;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

/**
 * 각종 이벤트 처리를 등록해준다. EventBusRegistable가 붙은건 전부 가져온다.
 * 공통 삽입되는 소스이나, 프로젝트마다 처리유무가 틀린경우 아래처럼 사용 
 * ex) @Autowired(required=false) private EventBus eventBusDelegator;
 * XML에 등록해서 사용하도록 하자
 */
public class EventBusDelegator extends EventBus implements InitializingBean{
    
    @Resource private Collection<EventBusRegistable> eventBusRegistables;
    private Logger log = LoggerFactory.getLogger(this.getClass());
    
    @Subscribe
    public void deadEvent(DeadEvent e) {
        log.warn("이벤트 [{}]가 퍼블리시 되었으나 아무도 @Subscribe하지 않습니다. 디버깅 필요.",e.getEvent().getClass().getSimpleName());
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        for(EventBusRegistable each : eventBusRegistables){
            log.info("EventBus 에 {}를 registe합니다",each.getClass().getSimpleName());
            register(each);
        }
        register(this);
    }
 

}
