package erwins.util.tools.csvLog;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import lombok.Data;

import org.joda.time.DateTime;
import org.joda.time.DateTimeFieldType;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.core.convert.converter.Converter;

import au.com.bytecode.opencsv.CSVParser;
import au.com.bytecode.opencsv.CSVWriter;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;

import erwins.util.lib.FileUtil;
import erwins.util.spring.batch.CsvItemWriter.CsvAggregator;


/** 
 * 하나의 매니저와 1개의 별도 스래드로, 병렬환경에서 다수설정된 CSV로그를 남기기 위해서 사용한다.
 * X초당, X분당 등 자유로운 설정이 가능하다.
 * 일반적인 상황에서의 성능은 문제없다. 로컬노트북 1500만 로우(422M) 쓰기시 1분 47초. 초당 140186
 * ex) 
 * final CsvLogMamager manager = new CsvLogMamager();
		manager.add(new CsvLogInfo<Ad>("ad1",adDir ,agg).setType(DateTimeFieldType.minuteOfDay(), 1, CsvLogMamager.DEFAULT_TIME_PATTERN));
		manager.add(new CsvLogInfo<Ad>("ad2",adDir ,agg).setType(DateTimeFieldType.secondOfDay(), 16, DateTimeFormat.forPattern("yyyy_MMdd_HHmmss")));
		manager.startup();
 *  */
public class CsvLogMamager {

	public static final DateTimeFormatter DEFAULT_DATE_PATTERN = DateTimeFormat.forPattern("yyyy_MMdd");
	public static final DateTimeFormatter DEFAULT_TIME_PATTERN = DateTimeFormat.forPattern("yyyy_MMdd_HHmm");
	
	final Map<String,CsvLogInfo<?>> csvLogInfoMap = Maps.newHashMap();
	BlockingQueue<CsvLog> queue;
	private CsvLogThread thread;
	
	//private int fileLinemaxSize = 1000;
	private int queueCapacity = 50000;
	private boolean csvRead = false;
	/** 현재 파일을 열고 쓰고있는 파일의 확장자 */
	private String writingFileExtention =  "log";
	private String fileExtention =  "csv";
	
	public synchronized void startup(){
		Preconditions.checkState(queue == null);
		Preconditions.checkState(csvLogInfoMap.size() > 0);
		queue = new ArrayBlockingQueue<CsvLogMamager.CsvLog>(queueCapacity);
		thread = new CsvLogThread(this);
		thread.start();
	}
	
	/** 즉시 멈춘다. 큐에 대기중인 자료는 소실된다.  */
	public synchronized void interrupt() throws InterruptedException{
		thread.interrupt();
	}
	
	/** 
	 * 큐에 들어간 자료가 다 소진되기를 기다렸다가 스래드를 종료한다.
	 * 큐에 자료가 들어가는 행위가 먼저 멈춰야 한다. 
	 *  */
	public synchronized void stopAndjoin() throws InterruptedException{
		thread.setStop(true);
		thread.join();
	}
	
	public synchronized <T> void add(CsvLogInfo<T> info){
		Preconditions.checkState(queue == null);
		Preconditions.checkState(info.writer==null);
		Preconditions.checkState(info.writerFile==null);
		
		Preconditions.checkArgument(!csvLogInfoMap.containsKey(info.name));
		if(info.fileNameConverter==null) info.fileNameConverter = getDefaultDateConverter(info.name,info.timePattern);
		
		try {
			reloadWriter(info);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		
		csvLogInfoMap.put(info.name,info);
	}

	/** 현재시각 기준으로 라이터를 교체한다.
	 * AA01.log로 저장되다가 타임이 지나면 AA02.log가 새로 생기고 기존 파일은 AA01.csv로 파일명이 변경된다. 
	 *  ==> 이거 걍 로그4j랑 동일하게 수정하자.
	 *  */
	public <T> void reloadWriter(CsvLogInfo<T> info) throws IOException {
		
		DateTime from = new DateTime().property(info.dateTimeFieldType).roundFloorCopy();
		
		if(info.writer!=null && info.writerFile!=null){
			close(info,from);
		}
		
		DateTime next = from.withFieldAdded(info.dateTimeFieldType.getDurationType(),info.interval);
		info.nextInterval = next.getMillis();
		
		File file = new File(info.dir,info.name + "."+writingFileExtention);  //로그4j 처럼 쓰는 로그파일은 고정이다. 중단된 파일이 있다면 이어쓴다.
		FileUtil.mkdirOrThrowException(file.getParentFile());
		
		FileOutputStream os = new FileOutputStream(file,true);
		OutputStreamWriter w = new OutputStreamWriter(os,csvRead ? "UTF-8" : "MS949");
		//BufferedWriter ww = new BufferedWriter(w,1024); //강종을 고려한다면 더 짧게 잡아야 할것이다.
		char escaper = csvRead ? CSVParser.DEFAULT_ESCAPE_CHARACTER : CSVWriter.DEFAULT_ESCAPE_CHARACTER;
		info.writer = new CSVWriter(w,CSVWriter.DEFAULT_SEPARATOR,CSVWriter.DEFAULT_QUOTE_CHARACTER,escaper);
		info.writerFile = file; 
		
		if(info.header!=null){
			info.writer.writeNext(info.header);
		}
	}

	public <T> void close(CsvLogInfo<T> info,DateTime from) throws IOException {
		//String fileName = Files.getNameWithoutExtension(info.writerFile.getName());
		//File dest = new File(info.writerFile.getParentFile(),fileName+"."+fileExtention);
		
		String fileName = info.fileNameConverter.convert(from); //파일 이름은 from 기준이다.  2014-03-11 파일은 2014-03-11 부터 쓰기 시작한 파일이다.
		File dest = new File(info.dir,fileName + "."+fileExtention);
		
		//재기동 등으로 인해서 여러 파일이 생길 수 있다.
		int index = 0;
		while(dest.exists()){
			dest = new File(info.writerFile.getParentFile(),fileName+"_"+ ++index +"."+fileExtention);
		}

		info.writer.close();
		FileUtil.renameTo(info.writerFile, dest);
	}
	
	@SuppressWarnings({ "unchecked"})
	public <T> CsvLogWriter<T> getLogger(String name){
		Preconditions.checkNotNull(queue);
		CsvLogInfo<T> info = (CsvLogInfo<T>) csvLogInfoMap.get(name);
		Preconditions.checkNotNull(info);
		CsvLogWriter<T> writer = new CsvLogWriter<T>(name,info.csvAggregator,queue);
		return writer;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes"})
	public File getLogFile(String name,DateTime dateTime){
		Preconditions.checkNotNull(queue);
		CsvLogInfo info = (CsvLogInfo) csvLogInfoMap.get(name);
		Preconditions.checkNotNull(info);
		DateTime from = dateTime.property(info.dateTimeFieldType).roundFloorCopy();
		File file = new File(info.dir,info.fileNameConverter.convert(from) + "."+fileExtention);
		return file;
	}
	
	@Data
	public static class CsvLog{
		final private String name;
		final private String[] data;
		/** 강제 플러싱 */
		private boolean flush = false;
	}
	
	/** 로그 설정파일. 
	 * 기본설정은 하루1개 로그파일 */
	@Data
	public static class CsvLogInfo<T>{
	
		final private String name;
		final private File dir;
		final private CsvAggregator<T> csvAggregator;
		
		private String[] header;
		private DateTimeFieldType dateTimeFieldType = DateTimeFieldType.dayOfYear();
		private int interval = 1;
		
		/** 파일이름 1순위 설정 */
		private Converter<DateTime,String> fileNameConverter;
		/** 파일이름 2순위 설정 */
		private DateTimeFormatter timePattern = DEFAULT_DATE_PATTERN;
		
		private CSVWriter writer;
		private File writerFile;
		private long nextInterval;
		
		public CsvLogInfo<T> setType(DateTimeFieldType dateTimeFieldType,int interval,DateTimeFormatter timePattern){
			this.dateTimeFieldType = dateTimeFieldType;
			this.interval = interval;
			this.timePattern = timePattern;
			return this;
		}
		public CsvLogInfo<T> setHeader(String[] header){
			this.header = header;
			return this;
		}
	}
	
	public static Converter<DateTime,String> getDefaultDateConverter(final String name,final DateTimeFormatter pattern){
		return new Converter<DateTime,String>(){
			@Override
			public String convert(DateTime source) {
				return name+"_"+pattern.print(source);
			}
		};
	}

}
