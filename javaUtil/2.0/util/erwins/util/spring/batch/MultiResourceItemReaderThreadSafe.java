package erwins.util.spring.batch;

import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.batch.item.file.MultiResourceItemReader;

/** 스레드 세이프하게 살짝 변경.
 *  이 리더를 사용하면 위임자는 스래드 세이프하지 않아도 된다.
 *  @see  DelegateThreadsafeItemReader
 *   */ 
@Deprecated
public class MultiResourceItemReaderThreadSafe<T> extends MultiResourceItemReader<T> {
    
	/** synchronized 만 적용 */
    @Override
    public synchronized T read() throws Exception, UnexpectedInputException, ParseException {
    	return super.read();
    }
    

}
