package erwins.util.spring.batch.component;

import java.util.Iterator;

import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;

import erwins.util.root.ThreadSafe;


/** IteratorItemReader가 생성자만 지원해서 새로 만들었다.
 * @BeforeStep
	public void beforeStep(StepExecution stepExecution) {
		it = reportMap.values().iterator();
	}
	이런식으로 설정하자
 *  */
@ThreadSafe
public class IteratorItemReader2<T> implements ItemReader<T>{

	protected Iterator<T> it;
	
	public IteratorItemReader2(){};
	
	public IteratorItemReader2(Iterator<T> it){
		this.it = it;
	};

	@Override
	public synchronized T read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
		if(it.hasNext()) return it.next();
		return null;
	}

    

}
