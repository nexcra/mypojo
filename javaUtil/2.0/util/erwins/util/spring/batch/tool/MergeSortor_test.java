package erwins.util.spring.batch.tool;

import java.io.File;
import java.util.Comparator;

import org.junit.Test;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.mapping.PassThroughLineMapper;
import org.springframework.batch.item.file.transform.PassThroughLineAggregator;
import org.springframework.core.io.FileSystemResource;

import erwins.util.dateTime.TimeString;
import erwins.util.lib.CompareUtil.StringComparator;

public class MergeSortor_test {
	
	private File ROOT = new File("c:/data/download/temp1");

	@Test
	public void main() throws Exception {

		TimeString ts = new TimeString();
		MergeSortor<String> sorter = new MergeSortor<String>();
		
		Comparator<String> comparator = new StringComparator<String>();
		
		ItemReaderFactory<String> itemReaderFactory = new ItemReaderFactory<String>(){
			@Override
			public  ItemReader<String> readerInstance() throws Exception {
				FlatFileItemReader<String> itemReader = new FlatFileItemReader<String>();
				itemReader.setLineMapper(new PassThroughLineMapper());
				itemReader.setEncoding("UTF-8");
				itemReader.afterPropertiesSet();
				return itemReader;
			}
		};
		
		ItemWriterFactory<String> itemWriterFactory = new ItemWriterFactory<String>(){
			@Override
			public  ItemWriter<String> writerInstance() throws Exception {
				FlatFileItemWriter<String> itemWriter = new FlatFileItemWriter<String>();
				itemWriter.setLineAggregator(new PassThroughLineAggregator<String>());
				itemWriter.setEncoding("UTF-8");
				itemWriter.afterPropertiesSet();
				return itemWriter;
			}
		};
		
		sorter.init(ROOT,comparator,itemReaderFactory, itemWriterFactory);
		try {
			sorter.sort(new FileSystemResource(new File(ROOT,"d1w1_imp.log_2014-04-08")),new File(ROOT,"d1w1_imp.log_2014-04-08_sorted"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		System.out.println("== 종료 ==");
		System.out.println(ts);
		
		//검증작업
		FlatFileItemReader<String> itemReader = new FlatFileItemReader<String>();
		itemReader.setLineMapper(new PassThroughLineMapper());
		itemReader.setEncoding("UTF-8");
		itemReader.setResource(new FileSystemResource(new File(ROOT,"d1w1_imp.log_2014-04-08_sorted")));
		itemReader.afterPropertiesSet();
		
		itemReader.open(new ExecutionContext());
		String bef = "";
		while(true){
			String line = itemReader.read();
			if(line==null) break;
			int comp = bef.compareTo(line);
			if(comp >= 0){
				System.out.println(line);
			}
			bef = line;
		}
		itemReader.close();
		
	}
	

}
