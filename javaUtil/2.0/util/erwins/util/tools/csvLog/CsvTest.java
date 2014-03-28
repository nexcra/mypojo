package erwins.util.tools.csvLog;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.joda.time.DateTimeFieldType;
import org.joda.time.format.DateTimeFormat;
import org.junit.Test;

import erwins.util.dateTime.TimeString;
import erwins.util.lib.FileUtil;
import erwins.util.nio.ThreadUtil;
import erwins.util.spring.batch.Batch;
import erwins.util.spring.batch.CsvItemWriter.CsvAggregator;
import erwins.util.spring.batch.tool.SpringBatchMock;
import erwins.util.text.CharEncodeUtil;
import erwins.util.text.RandomStringUtil;
import erwins.util.tools.csvLog.CsvLogMamager.CsvLogInfo;

public class CsvTest{

	@Test
	public void test() throws IOException, InterruptedException, ExecutionException {
		
		File adDir = new File("C:/DATA/download/ad");
		FileUtil.deleteDirectory(adDir);
		CsvAggregator<Batch> agg = new CsvAggregator<Batch>() {
			@Override
			public String[] aggregate(Batch item) {
				return new String[]{item.getJobName(),String.valueOf(item.getJobExecutionId())};
			}
		};
		
		final CsvLogMamager manager = new CsvLogMamager();
		manager.add(new CsvLogInfo<Batch>("ad1",adDir ,agg).setType(DateTimeFieldType.minuteOfDay(), 1, CsvLogMamager.DEFAULT_TIME_PATTERN));
		manager.add(new CsvLogInfo<Batch>("ad2",adDir ,agg).setType(DateTimeFieldType.secondOfDay(), 16, DateTimeFormat.forPattern("yyyy_MMdd_HHmmss")));
		manager.startup();
		
		List<Future<Long>> calls = ThreadUtil.call(30, new Callable<Long>() {
			@Override
			public Long call() throws Exception {
				CsvLogWriter<Batch> logger1 = manager.getLogger("ad1");
				CsvLogWriter<Batch> logger2 = manager.getLogger("ad2");
				Random r = new Random();
				long sum = 0;
				for(int i=0;i<500000;i++){
					Batch item = new Batch();
					item.setJobName(RandomStringUtil.getRandomSring(5));
					item.setJobExecutionId((long) r.nextInt(5000));
					logger1.writeLog(item);
					logger2.writeLog(item);
					sum += item.getJobExecutionId();
					//Thread.sleep(r.nextInt(50));
				}
				return sum;
			}
			
		});
		TimeString ts1 = new TimeString();
		Long sum = ThreadUtil.sum(calls);
		System.out.println("sum " + sum + " " + ts1);
		
		TimeString ts2 = new TimeString();
		manager.stopAndjoin();
		System.out.println("테스트 종료 " + ts2);
		
		long sum2 = 0;
		File[] files = adDir.listFiles();
		for(File file : files){
			sum2 += SpringBatchMock.readCsvAndSum(CharEncodeUtil.C_UTF_8, file, 1);
		}
		sum2 = sum2 / 2;
		
		System.out.println("sum2 " + sum2);
		//37486632819
		//37486632819
	}

}
