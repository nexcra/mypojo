package erwins.util.spring.batch;

import java.util.Collection;
import java.util.Iterator;

import org.apache.commons.collections.iterators.EmptyIterator;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemStream;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.batch.item.ItemStreamReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.batch.item.file.ResourceAwareItemReaderItemStream;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.io.Resource;

import com.google.common.base.Preconditions;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;


/** reader를 읽어서 부분 집합으로 만들때 사용한다. 특수용도.
 * 같은 key끼리 묶어서 리턴하며, 메모리 때문에 최대치를 넘길경우 플러시 하고 다시 읽는다.
 * 즉 제한된 메모리 내에서만 그루핑 된다.
 * key별로 사전 처리(Lock등)를 해야 할 경우 사용
 * 정렬되지 않은 데이터를 처리할때 사용된다 */
public class ItemMapReader<T> implements ItemReader<Collection<T>>,ItemStream,ResourceAwareItemReaderItemStream<Collection<T>>,InitializingBean{
	
	@SuppressWarnings("unchecked")
	private Iterator<Collection<T>> it = EmptyIterator.INSTANCE;
	/** 유효한 ITEM을 읽은 수 */
	private int itemReadCount = 0;
	/** 컬렉션을 반환한 수 */
	private int collectionReadCount = 0;
	
	private ItemStreamReader<T> reader;
	private ItemMapper<T> itemMapper;
	private int maxSize = 5000;
	private Multimap<Object,T> stored;
	
	@Override
	public void afterPropertiesSet() throws Exception {
		Preconditions.checkNotNull(reader, "reader is required");
		Preconditions.checkNotNull(itemMapper, "itemMapper is required");
		if(reader instanceof InitializingBean) ((InitializingBean)reader).afterPropertiesSet();
		stored = ArrayListMultimap.create();
	}
	
	public static interface ItemMapper<T>{
		/** 이전 자료와 분리된 자료인지? */
		public Object getKey(T vo);
	}
	
	/** 다음번 read를 해야지 이전 read가 끝났는지 알 수 있다.  */
	@Override
	public synchronized Collection<T> read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
		while(true){
			
			if(it.hasNext()) {
				collectionReadCount++;
				return it.next();
			}
			
			T vo = reader.read();
			if(vo==null){
				if(stored.isEmpty()) return null; //리더를 끝내기 위한 리턴
				else{
					it = stored.asMap().values().iterator();
					stored = ArrayListMultimap.create();
				}
			}else{
				Object key = itemMapper.getKey(vo);
				if(key==null) key = "";
				stored.put(key, vo);
				itemReadCount++;
				if(stored.size() >= maxSize){
					it = stored.asMap().values().iterator();
					stored = ArrayListMultimap.create();
				}
			}
		}
	}

	@Override
	public void open(ExecutionContext executionContext) throws ItemStreamException {
		reader.open(executionContext);		
	}

	@Override
	public void update(ExecutionContext executionContext) throws ItemStreamException {
		executionContext.put("itemReadCount", itemReadCount);
		executionContext.put("collectionReadCount", collectionReadCount);
		reader.update(executionContext);
	}

	@Override
	public void close() throws ItemStreamException {
		reader.close();
	}

	@Override
	public void setResource(Resource resource) {
		((ResourceAwareItemReaderItemStream<T>)reader).setResource(resource);
	}
	

	public void setReader(ItemStreamReader<T> reader) {
		this.reader = reader;
	}


	public int getMaxSize() {
		return maxSize;
	}

	public void setMaxSize(int maxSize) {
		this.maxSize = maxSize;
	}

	public ItemMapper<T> getItemMapper() {
		return itemMapper;
	}

	public void setItemMapper(ItemMapper<T> itemMapper) {
		this.itemMapper = itemMapper;
	}



}
