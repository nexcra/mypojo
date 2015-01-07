package erwins.util.spring.batch.tool;

import org.springframework.batch.item.ItemWriter;

/** 
 * 라이터를 설정해야 하지만, 동시에2개 이상을 쓰는 경우가 발생한다면 이것으로 설정해야 한다. 딴방법은 안찾아봄.. ㅠ
 * MergeSortor 에서 사용중
 *  */
public interface ItemWriterFactory<T> {
	
	public ItemWriter<T> writerInstance() throws Exception;

}
