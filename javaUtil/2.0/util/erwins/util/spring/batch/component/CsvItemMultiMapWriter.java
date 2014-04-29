package erwins.util.spring.batch.component;


import java.text.MessageFormat;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import lombok.Data;

import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemWriter;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.io.FileSystemResource;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;

import erwins.util.root.NotThreadSafe;
import erwins.util.spring.batch.CsvItemWriter;
import erwins.util.text.StringUtil;

/** 
 * 커밋 주기별로 MultiMap으로 전환한 후 파일을  나누어 따로 쓴다.
 * 근데 병렬 처리가 불가능하니.. 불필요 하지 않을까 하다.
 * 중간에 예외가 발생하면 복구가 불가능하다.
 * 
 * CsvItemHashWriter가 더 좋아보인다. 
 * 파일을 나눈다는 작업은 동일하지만 병렬처리 가능하냐의 차이가 있다.
 * 
 * 50G, 0.7억 로우의 파일 분리 / 검증해봤음.
 */
@NotThreadSafe
@Data
public class CsvItemMultiMapWriter<ID,T> implements ItemWriter<T>{
	
	private CsvItemWriter<T> delegate;
	private Converter<T,ID>  idConverter;
	private Converter<ID,String>  idToFileNameConverter;
	private int  maxFileLineCount = 10000;
	private final Map<ID,MultiFileInfo> fileInfo = Maps.newConcurrentMap();
	
	@Override
	public void write(List<? extends T> items) throws Exception {
		Multimap<ID,T> map = ArrayListMultimap.create();
		for(T item : items){
			ID id = idConverter.convert(item);
			map.put(id, item);
		}
		for(Entry<ID, Collection<T>> entry : map.asMap().entrySet()){
			ID id = entry.getKey();
			String fileNameTemplate = idToFileNameConverter.convert(id); /// AA_FILE_{0}.csv 이런식으로 변경
			MultiFileInfo existFileInfo = fileInfo.get(id);
			if(existFileInfo==null){
				existFileInfo = new MultiFileInfo();
				fileInfo.put(id, existFileInfo);
			}
			
			Collection<T> values = entry.getValue();
			Iterator<T> i = values.iterator();
			List<T> write = Lists.newArrayList();
			
			while(i.hasNext()){
				T item = i.next();
				write.add(item);
				if(existFileInfo.lineCount + write.size() == maxFileLineCount){
					doWrite(getFileName(fileNameTemplate,existFileInfo.fileIndex), write);
					write.clear();
					existFileInfo.fileIndex ++;
					existFileInfo.lineCount = 0;
				}
			}
			if(write.size() > 0){
				doWrite(getFileName(fileNameTemplate,existFileInfo.fileIndex), write);
				existFileInfo.lineCount += write.size();
			}
		}
	}
	
	@Data
	public static class MultiFileInfo{
		private int fileIndex = 1;
		private int lineCount = 0;
	}

	private void doWrite(String fileName,List<T> write) throws Exception {
		try {
			delegate.setResource(new FileSystemResource(fileName));
			delegate.open(new ExecutionContext());
			delegate.write(write);
			delegate.close();
		} catch (Exception e) {
			System.out.println(e);
		}
	}

	private String getFileName(String fileNameTemplate, int fileIndex) {
		return MessageFormat.format(fileNameTemplate, StringUtil.leftPad(fileIndex,4));
	}
	
	

}