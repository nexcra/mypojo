package erwins.util.spring.batch;


import java.io.BufferedInputStream;
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
import org.springframework.core.io.Resource;

import au.com.bytecode.opencsv.CSVParser;
import au.com.bytecode.opencsv.CSVReader;
import erwins.util.vender.etc.OpenCsv;

/** 스레드 세이프하당
 * csvMapper에서 매핑하는데 시간이 많이 걸릴것으로 예상된다면(그럴일이 많이 없겠지만) PassThroughCsvMapper를 일단 사용한 후 processor에서 변환하도록 하자.
 * FlatFileReader와 다른점은 텍스트 안에 \n가 들어있어도 정상적으로 파일을 읽는다.  */
public class CsvItemReader<T> implements ResourceAwareItemReaderItemStream<T>,ItemReader<T>, ItemStream{
	
	public static final String READ_COUNT = "read.count";
	private CSVReader reader;
	private Resource resource;
	private CsvMapper<T> csvMapper;
	private String encoding = "MS949";
	private int linesToSkip = 0;
	private int lineCount = 0;
	private char separator =  CSVParser.DEFAULT_SEPARATOR;
	
	@Override
	public void close() throws ItemStreamException {
		OpenCsv.closeQuietly(reader);
	}

	@Override
	public void open(ExecutionContext arg0) throws ItemStreamException {
		try {
			reader = new CSVReader(new InputStreamReader (new BufferedInputStream(resource.getInputStream()),encoding),separator);
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
	
	/** 자료가 없으면 카운트를 올리지 않는다. */
	public String[] readLine() throws IOException {
		String[] lines = reader.readNext();
		if(lines!=null) lineCount++;
		return  lines;
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

	public char getSeparator() {
		return separator;
	}

	public void setSeparator(char separator) {
		this.separator = separator;
	}
	
}