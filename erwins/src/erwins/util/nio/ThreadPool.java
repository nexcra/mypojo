package erwins.util.nio;

import java.util.ArrayList;
import java.util.List;

import erwins.util.root.Shutdownable;

/** 걍 NIO에 넣었다.  생성/소비자 패턴에 주로 사용할것. */
public class ThreadPool implements Shutdownable{
	
	List<Thread> list = new ArrayList<Thread>();
	
	/** 리턴된 Thread에 이름을 달아주자~ */
	public Thread add(Thread thread){
		list.add(thread);
		return thread;
	}
	
	public void startup(){
		for (Thread each : list) each.start();
	}

	@Override
	public void shutdown() {
		for (Thread each : list) each.interrupt();
	}
	
}