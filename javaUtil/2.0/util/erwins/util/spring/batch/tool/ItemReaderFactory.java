package erwins.util.spring.batch.tool;

import org.springframework.batch.item.ItemReader;

/** 리더를 설정해야 하지만, 동시에2개 이상을 읽는 경우가 발생한다면 이것으로 설정해야 한다. */
public interface ItemReaderFactory<T> {
	
	public ItemReader<T> readerInstance() throws Exception;

}
