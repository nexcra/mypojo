package erwins.util.spring.batch.component;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.core.convert.converter.Converter;

import au.com.bytecode.opencsv.CSVWriter;

import com.google.common.base.Preconditions;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;

import erwins.util.guava.MultiMapUtil;
import erwins.util.root.ThreadSafe;
import erwins.util.spring.batch.CsvItemWriter;
import erwins.util.text.StringUtil;
import erwins.util.vender.etc.OpenCsv;

/** 
 * 키값을 해시한 다음 각 파일에 나눠쓴다. 
 * 각 파일을을 인메모리 소팅해서 DB에 넣기 위해서 사용한다. (적절하게 파일은 분산된다.)
 * 즉 병렬처리 + PK중복입력/수정 방지 (수정을 위한 select시 readlock을 회피하기 위함)
 * 주의!!! 개별 PK중복은 처리 가능하지만 소팅되지는 않는다.
 * afterPropertiesSet 에서 IO를 모두 열어놓는다.
 * too many open file 오류가 안나도록 미리 OS 설정파일을 수정해둘것! 보통1024 -> 4096으로 수정해달라고 요청.
 * BufferSize 역시 조절해야 한다. 기본 설정으로 1000개 열면 1000G 쓴다..
 * 
 * 노트북 기준. 병렬처리 한게 5배 정도 빠르다.
 */
@ThreadSafe
public class CsvItemHashWriter<T> extends CsvItemWriter<T>{
	
	/** 보통 OS에서 최대 4000개 까지를 권장함으로, 3000개를 MAX로 보면 될듯 하다. */
	private int length = 10;
	/** 파일을 쌓아놓을 디렉토리 */
	private File tempDir;
	/** 파일명  */
	private String fileName = "temp";
	
	private Map<String,CSVWriter> writerMap = Maps.newHashMap();
	private Converter<T,String>  idConverter;
	
	/** 일단 open 순서나 ExecutionContext는 관리하지 않는다. */
	@Override
	public void open(ExecutionContext executionContext) throws ItemStreamException {
		
		Preconditions.checkNotNull(tempDir,"tempDir is required");
		Preconditions.checkState(tempDir.isDirectory(),"tempDir must be directory");
		Preconditions.checkNotNull(idConverter,"idConverter is required");
		
		try {
			List<String> datas = StringUtil.toNumberList(length);
			for(String each : datas){
				File file = new File(tempDir,fileName+each+".csv");
				CSVWriter writer = mekeCsvWriter(file);
				writerMap.put(each, writer);
			}
		} catch (FileNotFoundException e) {
			throw new ItemStreamException(e);
		} catch (UnsupportedEncodingException e) {
			throw new ItemStreamException(e);
		}
	}

	/** 일단 open 순서나 ExecutionContext는 관리하지 않는다. */
	@Override
	public void update(ExecutionContext executionContext) throws ItemStreamException {
		//none
	}

	/** 일단 open 순서나 ExecutionContext는 관리하지 않는다. */
	@Override
	public void close() throws ItemStreamException {
		for(CSVWriter each : writerMap.values()) OpenCsv.closeQuietly(each);
	}
	
	@Override
	public void write(List<? extends T> items) throws Exception {
		Multimap<String,T> multimap = ArrayListMultimap.create();
		for(T item : items){
			//실제 넘어오는 객체가 Collection<T>일 경우 수정. 잘 작동한다는 보장은 없다. 나중에 수정하자. 일단 귀찮아서 일케 함.
			if(item instanceof Collection){
				@SuppressWarnings("unchecked")
				Collection<T> colls = (Collection<T>) item;
				for(T each : colls){
					String id = idConverter.convert(each);
					multimap.put(StringUtil.getHashIntString(id, length), each);		
				}
			}else{
				String id = idConverter.convert(item);
				multimap.put(StringUtil.getHashIntString(id, length), item);	
			}
		}
		List<String> keys = MultiMapUtil.sortrdKey(multimap); //락 잡는 순서는 정렬되어 있어야 한다.
		for (String key : keys) {
			CSVWriter writer = writerMap.get(key);
			Collection<T> group =  multimap.get(key);
			synchronized (writer) {
				for(T item : group){
					String[] line = csvAggregator.aggregate(item);
					writer.writeNext(line);		
				}
			}
		}
	}

	public void setTempDir(File tempDir) {
		this.tempDir = tempDir;
	}

	public void setIdConverter(Converter<T, String> idConverter) {
		this.idConverter = idConverter;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public void setLength(int length) {
		this.length = length;
	}

}