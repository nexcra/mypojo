package erwins.util.vender.spring;


import java.io.IOException;
import java.util.List;

import org.apache.commons.io.output.FileWriterWithEncoding;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemStream;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.ResourceAwareItemWriterItemStream;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.io.Resource;

import au.com.bytecode.opencsv.CSVWriter;
import erwins.util.vender.etc.OpenCsv;

/** 
 * CsvItemReader 와는 다르게 스레드 세이프해질 수 없다~
 * 스레드 세이프하지 않다. 네버   */
public class CsvItemWriter<T> implements ResourceAwareItemWriterItemStream<T>,ItemWriter<T>, ItemStream ,InitializingBean{
	
	private CSVWriter writer;
	private Resource resource;
	private String encoding = "MS949";
	private String[] header;
	private CsvAggregator<T> csvAggregator;
	private int lineCount = 0;
	
	@Override
	public void open(ExecutionContext executionContext) throws ItemStreamException {
		try {
			writer = new CSVWriter(new FileWriterWithEncoding(resource.getFile(),encoding));
			writeLine(header);
		} catch (IOException e) {
			throw new ItemStreamException(e);
		}
	}
	
	@Override
	public void update(ExecutionContext executionContext) throws ItemStreamException {
		executionContext.putInt("totalCsvLineWrite", lineCount);
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
	
	public static interface CsvAggregator<T> {
		public String[] aggregate(T item);
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
	
	
	
	

}