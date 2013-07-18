package erwins.util.spring.batch;


import java.util.List;

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

import erwins.util.collections.ListStore;


/** 
 * reader를 읽어서 부분 집합으로 만들때 사용한다. 특수용도.
 * 입력되는 데이터는 반드시 정렬되어있어야 한다.
 *  */
public class ItemListReader<T> implements ItemReader<List<T>>,ItemStream,ResourceAwareItemReaderItemStream<List<T>>,InitializingBean{
	
	/** 유효한 ITEM을 읽은 수 */
	private int itemReadCount = 0;
	/** 컬렉션을 반환한 수 */
	private int collectionReadCount = 0;
	
	private ItemStreamReader<T> reader;
	private ItemSeparator<T> itemSeparator;
	private int maxSize = 5000;
	private ListStore<T> stored;
	
	@Override
	public void afterPropertiesSet() throws Exception {
		Preconditions.checkNotNull(reader, "reader is required");
		Preconditions.checkNotNull(itemSeparator, "itemSeparator is required");
		if(reader instanceof InitializingBean) ((InitializingBean)reader).afterPropertiesSet();
		stored = new ListStore<T>(maxSize);
	}
	
	public static interface ItemSeparator<T>{
		/** 이전 자료와 분리된 자료인지? */
		public boolean isSameListItem(T vo);
	}
	
	/** 다음번 read를 해야지 이전 read가 끝났는지 알 수 있다.  */
	@SuppressWarnings("unchecked")
	@Override
	public synchronized List<T> read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
		while(true){
			T vo = reader.read();
			if(vo==null){
				if(stored.isEmpty()) return null; //리더를 끝내기 위한 리턴
				else {
					collectionReadCount++;
					return stored.clearAndGet(); //마지막 자료
				}
			}else{
				itemReadCount++;
				if(itemSeparator.isSameListItem(vo)) {
					boolean over = !stored.add(vo); 
					if(over){
						collectionReadCount++;
						return stored.clearAndGet(); //최대값 오버된 자료
					}
				}else{
					List<T> exist = stored.clearAndGet(vo);
					if(exist.size() > 0){
						collectionReadCount++;
						return exist; //신규 자료
					}
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

	public ItemSeparator<T> getItemSeparator() {
		return itemSeparator;
	}

	public void setItemSeparator(ItemSeparator<T> itemSeparator) {
		this.itemSeparator = itemSeparator;
	}

	public int getMaxSize() {
		return maxSize;
	}

	public void setMaxSize(int maxSize) {
		this.maxSize = maxSize;
	}
	
	



}
