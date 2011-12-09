package erwins.util.nio;

import java.util.ArrayList;
import java.util.List;

import erwins.util.root.Shutdownable;

/** 걍 NIO에 넣었다.  생성/소비자 패턴에 주로 사용할것. 
 * ExecutorService를 사용하자. 이걸 왜만들었더라? ....
 * */
public class ThreadPool implements Shutdownable{
	
	private List<Thread> list = new ArrayList<Thread>();
	
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
	
	public static class ThreadState{
		public int on;
		public int off;
		public int interrupted;
	}
	
	/** 모든 스래드가 기동중인지? */
	public boolean isOn(){
		ThreadState state = state();
		if(state.on == list.size()) return true;
		return false;
	}
	
	/** 모든 스래드가 대기중인지? */
	public boolean isOff(){
		ThreadState state = state();
		if(state.off == list.size()) return true;
		return false;
	}
	
	public ThreadState state(){
		ThreadState state = new ThreadState();
		for (Thread each : list) {
			if(each.isAlive()){
				if(each.isInterrupted()) state.interrupted ++;
				else state.on++;
			}else  state.off ++;
		}
		return state;
	}
	
}