package erwins.util.vender.springBatch;


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemStream;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.file.ResourceAwareItemWriterItemStream;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.PreparedStatementSetter;

import au.com.bytecode.opencsv.CSVWriter;
import erwins.util.vender.etc.OpenCsv;

/** 
 * CsvItemReader 와는 다르게 스레드 세이프해질 수 없다~
 * 스레드 세이프하지 않다. 네버   */
public class CsvItemWriter<T> implements ResourceAwareItemWriterItemStream<T>,ItemWriter<T>, ItemStream ,InitializingBean{
	
	public static final String WRITE_COUNT = "write.count";
	
	private CSVWriter writer;
	private Resource resource;
	private String encoding = "MS949";
	private String[] header;
	private CsvAggregator<T> csvAggregator;
	private int lineCount = 0;
	private int bufferSize = 1024*1024; //1메가?
	
	
	/** 확실히 버퍼는 작동하는듯 하다. F5 연타하면 깔끔하게 1메가씩 올라간다. 
	 * 근데 성능 차이는 없는거 같다.. (확인은 안해봄) */
	@Override
	public void open(ExecutionContext executionContext) throws ItemStreamException {
		try {
			FileOutputStream os = new FileOutputStream(resource.getFile());
			OutputStreamWriter w = new OutputStreamWriter(os,encoding);
			BufferedWriter ww = new BufferedWriter(w,bufferSize); //디폴트가 8192 일듯
			writer = new CSVWriter(ww);
			if(header!=null) writeLine(header);
		} catch (IOException e) {
			throw new ItemStreamException(e);
		}
	}
	
	@Override
	public void update(ExecutionContext executionContext) throws ItemStreamException {
		executionContext.putInt(WRITE_COUNT, lineCount);
	}
	
	@Override
	public void close() throws ItemStreamException {
		OpenCsv.closeQuietly(writer);
	}
	
	@Override
	public void write(List<? extends T> items) throws Exception {
		for(T item : items){
			String[] lines =  csvAggregator.aggregate(item);
			writeLine(lines);
		}
	}
	
	public void writeLine(String[] lines){
		lineCount++;
		writer.writeNext(lines);
	}
	
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

	@Override
	public void afterPropertiesSet() throws Exception {
		//non
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
	
	/** 간단 사용법 예제.
	 * 로컬 노트북에서 원격 DB로 작동시 1초에 1M정도 쓰는듯  */
	public static ExecutionContext sqlToCsv(DataSource dataSource,File out,String sql,PreparedStatementSetter preparedStatementSetter) throws Exception{
		int fetchSize = 10000; //조절 가능해야 할지도?
		ExecutionContext ex = new ExecutionContext();
		JdbcCursorItemReader<String[]> reader = new JdbcCursorItemReader<String[]>();
    	CsvItemWriter<String[]> writer = new CsvItemWriter<String[]>();
    	writer.setCsvAggregator(new PassThroughCsvAggregator());
    	writer.setResource(new FileSystemResource(out));
    	writer.afterPropertiesSet();
    	
		reader.setDataSource(dataSource);
		ToStringArrayRowMapper rowMapper = new ToStringArrayRowMapper();
		reader.setRowMapper(rowMapper);
		reader.setFetchSize(fetchSize); 
		reader.setSql(sql);
		if(preparedStatementSetter!=null) reader.setPreparedStatementSetter(preparedStatementSetter);
		reader.afterPropertiesSet();
		
		try{
			reader.open(ex);
			writer.open(ex);
			while(true){
				String[] line = reader.read();
				if(line==null) break;
				writer.writeLine(line);
			}
			reader.update(ex);
			writer.update(ex);
		}finally{
			reader.close();
			writer.close();
		}
		return ex;
	}
	

}