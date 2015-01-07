package erwins.util.tools.fileMonitor;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.junit.Test;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.AfterStep;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.PassThroughLineMapper;

import erwins.util.spring.batch.BatchContext;
import erwins.util.spring.batch.component.CsvItemReader;
import erwins.util.spring.batch.component.CsvItemReader.PassThroughCsvMapper;
import erwins.util.text.StringUtil;

public class FileMonitorTest{

	@Test
	public void test() throws IOException, InterruptedException, ExecutionException {
		
		File logHome = new File("C:/DATA/download/로그파일");
		
		FlatFileItemReader<String> itemReader1 = new FlatFileItemReader<String>();
		itemReader1.setLineMapper(new PassThroughLineMapper());
		itemReader1.setEncoding("UTF-8");
		
		CsvItemReader<String[]> itemReader2 = new CsvItemReader<String[]>();
		itemReader2.setCsvMapper(new PassThroughCsvMapper());
		itemReader2.setEncoding("MS949");
		
		FileMonitorBatchCallback<String> callback1 = new FileMonitorBatchCallback<String>();
		callback1.setItemReader(itemReader1);
		callback1.setItemWriter(new ItemWriter<String>() {
			@Override
			public void write(List<? extends String> items) throws Exception {
				for(String item : items){
					System.out.println("=== " + item);
				}
			}
		});
		
		FileMonitorBatchCallback<String[]> callback2 = new FileMonitorBatchCallback<String[]>();
		callback2.setItemReader(itemReader2);
		callback2.setItemWriter(new ItemWriter<String[]>() {
			@Override
			public void write(List<? extends String[]> items) throws Exception {
				for(String[] item : items){
					System.out.println("=== " + StringUtil.join(item,","));
				}
			}
			@AfterStep
			public void afterStep(StepExecution executionContext) throws Exception {
				System.out.println("===============");
				BatchContext bc = new BatchContext(executionContext);
				System.out.println(bc.isCompletedStep());
			}
		});
		
		FileMonitorInfo m1 = FileMonitorInfo.createForAntPath(logHome,"*.log");
		m1.setFileMonitorCallback(callback1);
		
		
		FileMonitorInfo m2 = FileMonitorInfo.createForAntPath(logHome,"*.csv");
		m2.setFileMonitorCallback(callback2);
		
		FileMonitor monitor = new FileMonitor();
		monitor.addMonitor(m1);
		monitor.addMonitor(m2);
		monitor.start();
		Thread.sleep(1000*1000);
		
		
	}

}
