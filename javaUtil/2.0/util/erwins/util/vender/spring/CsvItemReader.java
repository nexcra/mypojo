package erwins.util.vender.spring;


import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemStream;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.batch.item.file.ResourceAwareItemReaderItemStream;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

import au.com.bytecode.opencsv.CSVReader;

import com.google.common.collect.Lists;

import erwins.util.vender.etc.OpenCsv;

/** 스레드 세이프하당
 * FlatFileReader와 다른점은 텍스트 안에 \n가 들어있어도 정상적으로 파일을 읽는다.  */
public class CsvItemReader<T> implements ResourceAwareItemReaderItemStream<T>,ItemReader<T>, ItemStream,InitializingBean{
	
	public static final String READ_COUNT = "read.count";
	private CSVReader reader;
	private Resource resource;
	private CsvMapper<T> csvMapper;
	private String encoding = "MS949";
	private int linesToSkip = 0;
	private int lineCount = 0;
	
	@Override
	public void close() throws ItemStreamException {
		OpenCsv.closeQuietly(reader);
	}

	@Override
	public void open(ExecutionContext arg0) throws ItemStreamException {
		try {
			reader = new CSVReader(new InputStreamReader (new BufferedInputStream(resource.getInputStream()),encoding));
			for (int i = 0; i < linesToSkip; i++) {
				readLine();
			}
		} catch (IOException e) {
			throw new ItemStreamException(e);
		}
	}

	@Override
	public void update(ExecutionContext arg0) throws ItemStreamException {
		arg0.putInt(READ_COUNT, lineCount);
	}

	@Override
	public synchronized T read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
		try {
			String[] lines =  readLine();
			if(lines==null) return null;
			return csvMapper.mapLine(lines, lineCount);
		} catch (IOException e) {
			throw new ItemStreamException(e);
		}
	}
	
	public String[] readLine() throws IOException {
		lineCount++;
		return   reader.readNext();
	}

	@Override
	public void setResource(Resource resource) {
		this.resource = resource;
	}

	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}

	public void setLinesToSkip(int linesToSkip) {
		this.linesToSkip = linesToSkip;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		//non
	}

	public void setCsvMapper(CsvMapper<T> csvMapper) {
		this.csvMapper = csvMapper;
	}
	
	public static interface CsvMapper<T>{
		public T mapLine(String[] lines, int lineNumber) throws Exception;
	}
	
	/** 걍 내려준다.  */
	public static class PassThroughCsvMapper implements CsvMapper<String[]>{
		@Override
		public String[] mapLine(String[] lines, int lineNumber)throws Exception {
			return lines;
		}
	}
	
	public static interface ListStringArrayCallback{
		public void resultListStringArray(List<String[]> list) throws Exception;
	}
	
	/** 간단 사용법 예제
	 * ExecutionContext : read.count   */
	public static ExecutionContext read(File in,int commitSize,ListStringArrayCallback callback) throws Exception{
		ExecutionContext ex = new ExecutionContext();
		CsvItemReader<String[]> reader = new CsvItemReader<String[]>();
		reader.setCsvMapper(new PassThroughCsvMapper());
		reader.resource = new FileSystemResource(in);
		reader.afterPropertiesSet();
		
		try{
			List<String[]> list = Lists.newArrayList();
			reader.open(new ExecutionContext());
			while(true){
				String[] line = reader.read();
				if(line==null) {
					if(list.size()!=0) callback.resultListStringArray(list);
					break;
				}
				list.add(line);
				if(list.size() >= commitSize){
					callback.resultListStringArray(list);
					list = Lists.newArrayList();
				}
			}
		}finally{
			reader.close();
		}
		return ex;
	}

}