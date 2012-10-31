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
 * 파일다운로드와 동시에 읽을때 정도 유용하다. 그 외에는 실시간 읽기가 더 좋다. */
public class StreamIterator<T>  extends Thread implements Iterator<T>{
	
	public static FileStreamLineToItem<String> DEFAULT = new FileStreamLineToItem<String>(){
		@Override
		public String lineToItem(String line) {
			return line;
		}
	};
    
    private final BlockingQueue<T> queue = new LinkedBlockingQueue<T>();
    private FileStreamLineToItem<T> fileStreamLineToItem;
    private int limitItem = 100000;
    private int sleepMSec = 1000;
    
    private File file;
    private InputStream in;
    
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
        if(file!=null){
            FileUtil.readLines(file, CharEncodeUtil.C_EUC_KR, new StringCallback(){
                @Override
                public void process(String line) {
                    T item = fileStreamLineToItem.lineToItem(line);
                    if(item != null){
                        if(queue.size() > limitItem) ThreadUtil.sleep(sleepMSec);
                        queue.add(item);
                    }
                }
            });
        }else if(in !=null){
            FileUtil.readLines(in, CharEncodeUtil.C_EUC_KR, new StringCallback(){
                @Override
                public void process(String line) {
                    T item = fileStreamLineToItem.lineToItem(line);
                    if(item != null){
                        if(queue.size() > limitItem) ThreadUtil.sleep(sleepMSec);
                        queue.add(item);
                    }
                }
            });
        }else throw new IllegalArgumentException("file or InputStream is required");
    }

    /** 부정확하다! */
    @Override
    public boolean hasNext() {
        boolean isEnd = !isAlive() && queue.size() == 0;
        return  !isEnd;
    }

    /** take()일경우 끝났는데 이게 호출되면 망한다. 무한 대기할 수 있음.
     * null이 나오면 끝이라고 인식하는 스프링 배치상 이걸로 해준다 */
    @Override
    public T next() {
        //take()
        try {
            return queue.poll(10, TimeUnit.SECONDS);
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
    public void setSleepMSec(int sleepMSec) {
        this.sleepMSec = sleepMSec;
    }
    public StreamIterator<T> setFile(File file) {
        this.file = file;
        return this;
    }
    public StreamIterator<T> setIn(InputStream in) {
        this.in = in;
        return this;
    }
    

}
