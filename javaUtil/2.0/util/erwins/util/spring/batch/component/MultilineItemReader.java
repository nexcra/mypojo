package erwins.util.spring.batch.component;

import java.util.List;

import javax.annotation.concurrent.ThreadSafe;

import lombok.Data;

import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemStream;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.convert.converter.Converter;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

import erwins.util.spring.batch.tool.SpringBatchUtil;

/** 
 * 스프링배치에서 지원하지 않아서(정확히 확인안해봄) 만듬.
 * 여러 줄의 데이터가 하나의 item을 구성할때 사용한다. (하지만 이렇게 만드는것은 매우 비추!)
 * itemReader에 뭐가 오던간에 스래드 안전하긴 하다...
 * ex) DB에서 다수의 광고를 읽지만 키워드를 기준으로 GROUP BY 한 후 묶음으로 읽어야 할때
 * ex) Flat 파일이 구성되어 있지만, 다수의 라인을 읽어서 하나의 item을 구성할때 (옥션에서 만든 자체 전송구조)  
 *   */ 
@Data
@ThreadSafe
public class MultilineItemReader<T,R> implements ItemReader<R>,ItemStream,InitializingBean{
    
	private ItemReader<T> itemReader;
	/** 아무것도 없다면 PassThroughConverter 사용 */
	private Converter<List<T>,R> converter;
	private MultilineItemEnd<T> multilineItemEnd;
	
	public static interface MultilineItemEnd<T>{
		public boolean isEndLine(T line);
	}
	
	/** 실제 ITEM수 */
	private int multilineItem = 0;

    /** 전체를 동기화 해서 읽는다. */
    @Override
    public synchronized R read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
    	List<T> lines = Lists.newArrayList();
        while(true){
        	T line = itemReader.read();
        	if(line==null) return null; //lines가 남아있을때 끝나면 경고해야 할수도 있다. 일단 진행
        	
        	lines.add(line);
        	
        	if(multilineItemEnd.isEndLine(line)){
        		R item = converter.convert(lines);
        		//lines.clear();  clear하지 않는다
        		multilineItem++;
        		return item;
        	}
        }
    }

	@Override
	public void open(ExecutionContext executionContext) throws ItemStreamException {
		multilineItem = 0;
		SpringBatchUtil.openIfAble(itemReader, executionContext);
	}

	@Override
	public void update(ExecutionContext executionContext) throws ItemStreamException {
		executionContext.putInt("multilineItem", multilineItem);
		SpringBatchUtil.updateIfAble(itemReader, executionContext);		
	}

	@Override
	public void close() throws ItemStreamException {
		SpringBatchUtil.closeIfAble(itemReader);		
	}
	
	@Override
	public void afterPropertiesSet() throws Exception {
		Preconditions.checkNotNull(itemReader,"itemReader는 필수임");
		Preconditions.checkNotNull(converter,"converter는 필수임");
		Preconditions.checkNotNull(multilineItemEnd,"multilineItemEnd는 필수임");
		SpringBatchUtil.afterPropertiesSetIfAble(itemReader);
	}
    

}
