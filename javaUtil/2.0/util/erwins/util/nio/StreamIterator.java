package erwins.util.nio;

import java.io.File;
import java.io.InputStream;
import java.util.Iterator;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import erwins.util.lib.CharEncodeUtil;
import erwins.util.lib.FileUtil;
import erwins.util.root.StringCallback;


/** 스래드로 파일을 읽어서 큐에 저장해준다.
 * item으로 분류되기 전까지는 동일 스래드에서 순서대로 라인을 읽어야 한다.
 * 라인세퍼레이터로 분리되는 파일만 가능하다. 
 * 이것을 스프링 배치에서 읽을때는 synch를 해주어야 한다.
 * 파일다운로드와 동시에 읽을때 정도 유용하다. 그 외에는 실시간 읽기가 더 좋다. 
 * --> 웬만하면 근데 사용금지 */
public class StreamIterator<T>  extends Thread implements Iterator<T>{
	
	public static FileStreamLineToItem<String> DEFAULT = new FileStreamLineToItem<String>(){
		@Override
		public String lineToItem(String line) {
			return line;
		}
	};
    /** limitItem을 넘어가면 queue가 소진될때까지 block된다.  */
    private BlockingQueue<T> queue;
    private FileStreamLineToItem<T> fileStreamLineToItem;
    private int limitItem = 100000;
    
    private File file;
    private InputStream in;
    private int pollTimeoutSec = 10;
    
    public StreamIterator(){
        setDaemon(true);
        setName(this.getClass().getSimpleName());
    }
    
    public static interface FileStreamLineToItem<T>{
        public T lineToItem(String line);
    }

    public void setFileStreamLineToItem(FileStreamLineToItem<T> fileStreamLineToItem) {
        this.fileStreamLineToItem = fileStreamLineToItem;
    }
    
    @Override
    public void run() {
        if(fileStreamLineToItem==null) throw new IllegalArgumentException("fileStreamLineToItem is required");
        queue = new LinkedBlockingQueue<T>(limitItem);
        if(file!=null){
            FileUtil.readLines(file, CharEncodeUtil.C_EUC_KR, new StringCallback(){
                @Override
                public void process(String line) {
                    T item = fileStreamLineToItem.lineToItem(line);
                    if(item != null){
                        try {
							queue.put(item);
						} catch (InterruptedException e) {
							//아무것도 하지 않는다.
						}
                    }
                }
            });
        }else if(in !=null){
            FileUtil.readLines(in, CharEncodeUtil.C_EUC_KR, new StringCallback(){
                @Override
                public void process(String line) {
                    T item = fileStreamLineToItem.lineToItem(line);
                    if(item != null){
                    	try {
							queue.put(item);
						} catch (InterruptedException e) {
							//아무것도 하지 않는다.
						}
                    }
                }
            });
        }else throw new IllegalArgumentException("file or InputStream is required");
    }

    /** 부정확하다!
     * hasNext()가 true이더라도 next가 없을 수 있다 */
    @Override
    public boolean hasNext() {
    	if(queue.size() > 0) return true;
    	if(isAlive()) return true;
    	//if(getState()  != Thread.State.TERMINATED) return true;
        return  false;
    }

    /** 
     * hasNext()가 true이더라도 next가 없을 수 있다
     * 스래드가 종료되지 않았지만 대기시간이 넘어서 null이 리턴될 가능성이 있다.
     * 스트림이 불안정 하다면 pollTimeoutSec을 크게 주자
     *  */
    @Override
    public T next() {
        //take()
        try {
            return queue.poll(pollTimeoutSec, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            return null;
        }
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }
    public int getLimitItem() {
        return limitItem;
    }
    public StreamIterator<T> setFile(File file) {
        this.file = file;
        return this;
    }
    public StreamIterator<T> setIn(InputStream in) {
        this.in = in;
        return this;
    }
	public void setPollTimeoutSec(int pollTimeoutSec) {
		this.pollTimeoutSec = pollTimeoutSec;
	}
    

}
