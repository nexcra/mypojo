package erwins.util.tools.csvLog;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.junit.Test;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.PassThroughLineMapper;

import erwins.util.tools.csvLog.FileMonitor.CsvLogMonitorInfo;

public class FileMonitorTest{

	@Test
	public void test() throws IOException, InterruptedException, ExecutionException {
		
		FlatFileItemReader<String> itemReader1 = new FlatFileItemReader<String>();
		itemReader1.setLineMapper(new PassThroughLineMapper());
		itemReader1.setEncoding("UTF-8");
		
		FlatFileItemReader<String> itemReader2 = new FlatFileItemReader<String>();
		itemReader2.setLineMapper(new PassThroughLineMapper());
		itemReader2.setEncoding("EUC-KR");
		
		ItemWriter<String> itemWriter = new ItemWriter<String>() {
			
			@Override
			public void write(List<? extends String> items) throws Exception {
				System.out.println(items);
			}
		};
		
		FileMonitor monitor = new FileMonitor();
		monitor.addMonitor(new CsvLogMonitorInfo(new File("C:/DATA/download/로그파일"))
			.setRenameDir(new File("C:/DATA/download/로그파일2")).setAntPath("**/*.log").setSpringBatch(itemReader1, itemWriter, 2));
		
		monitor.addMonitor(new CsvLogMonitorInfo(new File("C:/DATA/download/로그파일"))
		.setRenameDir(new File("C:/DATA/download/로그파일2")).setAntPath("**/*.txt").setSpringBatch(itemReader2, itemWriter, 2));
		
		monitor.start();
		
		Thread.sleep(1000*1000);
		
		
	}

}
