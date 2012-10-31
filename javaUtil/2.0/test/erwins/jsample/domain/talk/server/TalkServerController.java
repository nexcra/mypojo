package erwins.jsample.domain.talk.server;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Controller;

@Controller
public class TalkServerController {
	
	private Log log = LogFactory.getLog(this.getClass());
	private static final int PORT = 9988;
	private TalkServer server;

	@PostConstruct
	public void startup() {
		server = new TalkServer(PORT);
		server.startup();
		log.info("채팅 서버가 새 스래드로 실행됩니다.");
	}
	
	@PreDestroy
	public void shutdown() {
		if(server==null) return;
		server.shutdown();
		log.info("채팅 서버가 shutdown됩니다.");
	}
	
	/** 테스트용 */ 
	public static void main(String args[]) {
		TalkServer server = new TalkServer(PORT);
		server.startup();
	}
}
