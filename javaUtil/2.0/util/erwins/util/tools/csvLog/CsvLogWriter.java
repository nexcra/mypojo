package erwins.util.tools.csvLog;

import java.util.concurrent.BlockingQueue;

import lombok.Data;
import erwins.util.spring.batch.CsvItemWriter.CsvAggregator;
import erwins.util.tools.csvLog.CsvLogMamager.CsvLog;



@Data
public class CsvLogWriter<T> {

	private final String name;
	private final CsvAggregator<T> csvAggregator;
	private final BlockingQueue<CsvLog> queue;
	
	public void writeLog(T item) throws InterruptedException{
		String[] data = csvAggregator.aggregate(item);
		queue.put(new CsvLog(name, data));
	}

}
