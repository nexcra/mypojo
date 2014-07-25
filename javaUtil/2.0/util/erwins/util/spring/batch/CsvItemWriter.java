package erwins.util.spring.batch;


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.List;

import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemStream;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.ResourceAwareItemWriterItemStream;
import org.springframework.core.io.Resource;

import au.com.bytecode.opencsv.CSVParser;
import au.com.bytecode.opencsv.CSVWriter;

import com.google.common.base.CharMatcher;
import com.google.common.base.Preconditions;

import erwins.util.root.NotThreadSafe;
import erwins.util.text.StringUtil;
import erwins.util.vender.etc.OpenCsv;

/** 
 * CsvItemReader 와는 다르게 스레드 세이프해질 수 없다~
 */
@NotThreadSafe
public class CsvItemWriter<T> implements ResourceAwareItemWriterItemStream<T>,ItemWriter<T>, ItemStream {
	
	public static final String WRITE_COUNT = "write.count";
	
	private CSVWriter writer;
	private Resource resource;
	private File currentFile;
	protected String encoding = "MS949";
	/** 1.수동 헤더 */
	protected String[] header;
	/** 2.SQL등으로 생성되는 헤더 */
	private CsvHeaderCallback csvHeaderCallback;
	protected CsvAggregator<T> csvAggregator;
	private int lineCount = 0;
	protected int bufferSize = 1024*1024; //1메가?
	private boolean first = true;
	/** true이면 뒤에 붙여쓴다 */
	protected boolean append = false;
	/** 이 숫자(헤더 포함)를 넘어가면 현재 파일에 쓰기를 중단하고 다음 파일에 쓰기를 시도한다.
	 * 엑셀로 CSV를 읽을때 제한이 1048576 인거같다. */
	private Integer maxLineCount;
	private int currentMaxLineCount = 0;
	/** CSV로 write한것을 다시 CSV로 읽으려면 동일한 이스케이퍼(\)를 사용해야 한다.
	 * 대신 이렇게 이스케이핑 하면 MS의 엑셀 프로그램으로 읽지 못한다.(엑셀의 경우 기본 이스케이퍼(")를 사용한다.)  */
	protected boolean csvRead = false;
	/** 가끔 tap \t 으로 요구할때도 있다. */
	private char separator = CSVWriter.DEFAULT_SEPARATOR;
	
	public static interface CsvHeaderCallback{
		public List<String[]> headers();
	}
	
	/** 확실히 버퍼는 작동하는듯 하다. F5 연타하면 깔끔하게 1메가씩 올라간다. 
	 * 근데 성능 차이는 없는거 같다.. (확인은 안해봄) */
	@Override
	public void open(ExecutionContext executionContext) throws ItemStreamException {
		try {
			currentFile = resource.getFile();
			if(maxLineCount!=null){
				boolean digitFileName = CharMatcher.DIGIT.matchesAnyOf(currentFile.getName());
				Preconditions.checkArgument(digitFileName, "maxLineCount option required DIGIT fileName : " + currentFile.getName());
				currentMaxLineCount = maxLineCount;
			}
			doOpen();
		} catch (IOException e) {
			throw new ItemStreamException(e);
		}
	}

	private void doOpen() throws IOException {
		writer = mekeCsvWriter(currentFile);
		if(header!=null) writeLine(header);
	}

	/** 별도로 쓸 일이 있다. */
	protected CSVWriter mekeCsvWriter(File file) throws FileNotFoundException,UnsupportedEncodingException {
		FileOutputStream os = new FileOutputStream(file,append);
		OutputStreamWriter w = new OutputStreamWriter(os,encoding);
		BufferedWriter ww = new BufferedWriter(w,bufferSize); //디폴트가 8192 일듯
		char escaper = csvRead ? CSVParser.DEFAULT_ESCAPE_CHARACTER : CSVWriter.DEFAULT_ESCAPE_CHARACTER;
		return new CSVWriter(ww,separator,CSVWriter.DEFAULT_QUOTE_CHARACTER,escaper);
	}
	
	@Override
	public void update(ExecutionContext executionContext) throws ItemStreamException {
		executionContext.putInt(WRITE_COUNT, lineCount);
	}
	
	/** 닫으면서 flush 하는것으로 추청된다. */
	@Override
	public void close() throws ItemStreamException {
		OpenCsv.closeQuietly(writer);
	}
	
	/** SQL의 메타데이터는 read()후에 계산됨으로 header에 쓰는 부분을 여기에 둔다 */
	@Override
	public void write(List<? extends T> items) throws Exception {
		if(first && csvHeaderCallback!=null){
			List<String[]> headers = csvHeaderCallback.headers(); 
			for(String[] each : headers) writeLine(each);
			first = false;
		}
		for(T item : items){
			String[] lines =  csvAggregator.aggregate(item);
			writeLine(lines);
		}
	}
	
	public void writeLine(String[] lines){
		if(maxLineCount!=null){
			if(currentMaxLineCount <= lineCount){
				String nextFileName = StringUtil.plusAsLastNumber(currentFile.getName(), 1);
				File nextFile = new File(currentFile.getParentFile(),nextFileName);
				currentFile = nextFile;
				currentMaxLineCount += maxLineCount;
				try {
					OpenCsv.closeQuietly(writer);
					doOpen();
				} catch (IOException e) {
					throw new ItemStreamException(e);
				}
			}
		}
		lineCount++;
		writer.writeNext(lines);
	}
	
	/** CsvItemHashWriter 에서 사용한다. */
	/*
	public static interface CsvItemWriterFactory{
		public <T> CsvItemWriter<T> instance();
	}*/
	
	@Override
	public void setResource(Resource resource) {
		this.resource = resource;
	}

	public void setHeader(String[] header) {
		this.header = header;
	}
	
	public void setCsvAggregator(CsvAggregator<T> csvAggregator) {
		this.csvAggregator = csvAggregator;
	}
	
	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}
	
	public int getBufferSize() {
		return bufferSize;
	}

	public void setBufferSize(int bufferSize) {
		this.bufferSize = bufferSize;
	}

	public static interface CsvAggregator<T> {
		public String[] aggregate(T item);
	}
	
	/** 걍 내려준다.  */
	public static class PassThroughCsvAggregator implements CsvAggregator<String[]>{
		@Override
		public String[] aggregate(String[] item) {
			return item;
		}
	}
	
	public CsvHeaderCallback getCsvHeaderCallback() {
		return csvHeaderCallback;
	}

	public void setCsvHeaderCallback(CsvHeaderCallback csvHeaderCallback) {
		this.csvHeaderCallback = csvHeaderCallback;
	}

	public void setAppend(boolean append) {
		this.append = append;
	}

	public void setCsvRead(boolean csvRead) {
		this.csvRead = csvRead;
	}

	public Integer getMaxLineCount() {
		return maxLineCount;
	}

	public void setMaxLineCount(Integer maxLineCount) {
		this.maxLineCount = maxLineCount;
	}

	public void setSeparator(char separator) {
		this.separator = separator;
	}
	

}