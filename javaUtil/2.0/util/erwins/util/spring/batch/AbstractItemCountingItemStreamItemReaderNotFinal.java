package erwins.util.spring.batch;

import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemStream;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.batch.item.util.ExecutionContextUserSupport;
import org.springframework.util.Assert;

/**
 * 기존 AbstractItemCountingItemStreamItemReader가 final  read()라서 새로 만들었다.
 * 3.1 기준으로 AbstractItemCountingItemStreamItemReader를 복사/붙여넣기 함
 * 스프링에서 왜 read()를 final로 지정했는지 모르겠다.. ㅅㅂ
 * final하고 몇개 빼고는 동일
 * read()를 동기화 하여 읽기는 스래드 세이프 하다. 
 */
public abstract class AbstractItemCountingItemStreamItemReaderNotFinal<T> implements ItemReader<T>, ItemStream{
    
    private static final String READ_COUNT = "read.count";

    private static final String READ_COUNT_MAX = "read.count.max";

    protected int currentItemCount = 0;  //protected로 수정

    private int maxItemCount = Integer.MAX_VALUE;

    private ExecutionContextUserSupport ecSupport = new ExecutionContextUserSupport();

    private boolean saveState = true;

    /**
     * Read next item from input.
     * @return item
     * @throws Exception
     */
    protected abstract T doRead() throws Exception;

    /**
     * Open resources necessary to start reading input.
     */
    protected abstract void doOpen() throws Exception;

    /**
     * Close the resources opened in {@link #doOpen()}.
     */
    protected abstract void doClose() throws Exception;

    /**
     * Move to the given item index. Subclasses should override this method if
     * there is a more efficient way of moving to given index than re-reading
     * the input using {@link #doRead()}.
     */
    protected void jumpToItem(int itemIndex) throws Exception {
        for (int i = 0; i < itemIndex; i++) {
            read();
        }
    }

    /** final 빼고 synchronized 추가  */
    public synchronized T read() throws Exception, UnexpectedInputException, ParseException {
        if (currentItemCount >= maxItemCount-1) {
            return null;
        }
        currentItemCount++;
        return doRead();
    }

    protected int getCurrentItemCount() {
        return currentItemCount;
    }

    protected void setCurrentItemCount(int count) {
        this.currentItemCount = count;
    }

    public void close() throws ItemStreamException {
        currentItemCount = 0;
        try {
            doClose();
        }
        catch (Exception e) {
            throw new ItemStreamException("Error while closing item reader", e);
        }
    }

    public void open(ExecutionContext executionContext) throws ItemStreamException {

        try {
            doOpen();
        }
        catch (Exception e) {
            throw new ItemStreamException("Failed to initialize the reader", e);
        }

        if (executionContext.containsKey(ecSupport.getKey(READ_COUNT_MAX))) {
            maxItemCount = executionContext.getInt(ecSupport.getKey(READ_COUNT_MAX));
        }

        if (executionContext.containsKey(ecSupport.getKey(READ_COUNT))) {
            int itemCount = executionContext.getInt(ecSupport.getKey(READ_COUNT));

            if (itemCount < maxItemCount) {
                try {
                    jumpToItem(itemCount);
                }
                catch (Exception e) {
                    throw new ItemStreamException("Could not move to stored position on restart", e);
                }
            }
            currentItemCount = itemCount;

        }

    }

    public void update(ExecutionContext executionContext) throws ItemStreamException {
        if (saveState) {
            Assert.notNull(executionContext, "ExecutionContext must not be null");
            executionContext.putInt(ecSupport.getKey(READ_COUNT), currentItemCount);
            if (maxItemCount < Integer.MAX_VALUE) {
                executionContext.putInt(ecSupport.getKey(READ_COUNT_MAX), maxItemCount);
            }
        }

    }

    public void setName(String name) {
        ecSupport.setName(name);
    }

    /**
     * Set the flag that determines whether to save internal data for
     * {@link ExecutionContext}. Only switch this to false if you don't want to
     * save any state from this stream, and you don't need it to be restartable.
     * 
     * @param saveState flag value (default true).
     */
    public void setSaveState(boolean saveState) {
        this.saveState = saveState;
    }
    
}
