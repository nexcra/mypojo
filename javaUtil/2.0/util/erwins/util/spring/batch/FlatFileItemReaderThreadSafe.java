package erwins.util.spring.batch;


import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemStream;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.batch.item.file.BufferedReaderFactory;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.LineCallbackHandler;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.ResourceAwareItemReaderItemStream;
import org.springframework.batch.item.file.separator.RecordSeparatorPolicy;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.io.Resource;

/** FlatFileItemReader를 위임한다
 * 멀티 라인의 경우 RecordSeparatorPolicy를 조절하면 해결 가능한것으로 보인다(추정)
 *  -> mapper가 null을 리턴하는것과, 실제 데이터가 없어서 null을 리턴하는것을 구분할 수 없다 */
public class FlatFileItemReaderThreadSafe<T> implements ResourceAwareItemReaderItemStream<T>, InitializingBean,ItemReader<T>, ItemStream {

    FlatFileItemReader<T> reader = new FlatFileItemReader<T>();

    /** 전체를 동기화 해서 읽는다. */
    public synchronized T read() throws Exception, UnexpectedInputException, ParseException {
        return reader.read();
    }
    
    public void close() throws ItemStreamException {
        reader.close();
    }

    public void afterPropertiesSet() throws Exception {
        reader.afterPropertiesSet();
    }

    public boolean equals(Object arg0) {
        return reader.equals(arg0);
    }

    public int hashCode() {
        return reader.hashCode();
    }

    public boolean isSaveState() {
        return reader.isSaveState();
    }

    public void open(ExecutionContext executionContext) throws ItemStreamException {
        reader.open(executionContext);
    }

    public void setBufferedReaderFactory(BufferedReaderFactory bufferedReaderFactory) {
        reader.setBufferedReaderFactory(bufferedReaderFactory);
    }

    public void setStrict(boolean strict) {
        reader.setStrict(strict);
    }

    public void setCurrentItemCount(int count) {
        reader.setCurrentItemCount(count);
    }

    public void setSkippedLinesCallback(LineCallbackHandler skippedLinesCallback) {
        reader.setSkippedLinesCallback(skippedLinesCallback);
    }

    public void setLinesToSkip(int linesToSkip) {
        reader.setLinesToSkip(linesToSkip);
    }

    public void setLineMapper(LineMapper<T> lineMapper) {
        reader.setLineMapper(lineMapper);
    }

    public void setEncoding(String encoding) {
        reader.setEncoding(encoding);
    }

    public void setComments(String[] comments) {
        reader.setComments(comments);
    }

    public void setMaxItemCount(int count) {
        reader.setMaxItemCount(count);
    }

    public void update(ExecutionContext executionContext) throws ItemStreamException {
        reader.update(executionContext);
    }

    public void setName(String name) {
        reader.setName(name);
    }

    public void setSaveState(boolean saveState) {
        reader.setSaveState(saveState);
    }

    public void setResource(Resource resource) {
        reader.setResource(resource);
    }

    public void setRecordSeparatorPolicy(RecordSeparatorPolicy recordSeparatorPolicy) {
        reader.setRecordSeparatorPolicy(recordSeparatorPolicy);
    }

    public String toString() {
        return reader.toString();
    }
    
    

}