package erwins.util.tools.csvLog;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
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

import erwins.util.lib.FileUtil;
import erwins.util.root.exception.IORuntimeException;
import erwins.util.spring.batch.component.CsvItemWriter.CsvAggregator;

/** 
 * 로그 설정파일. 
 * 기본설정은 하루1개 로그파일
 * 
 *  편의상 static으로 만들어서 사용할것 
 *  ex) new CsvLogInfo<Ad>("ad1",adDir ,agg).setType(DateTimeFieldType.minuteOfDay(), 1, CsvLogMamager.DEFAULT_TIME_PATTERN);
 *  ex) new CsvLogInfo<Ad>("ad2",adDir ,agg).setType(DateTimeFieldType.secondOfDay(), 16, DateTimeFormat.forPattern("yyyy_MMdd_HHmmss");
 *  */
@Data
public class CsvLogInfo<T> {
	
	public static final DateTimeFormatter DEFAULT_DATE_PATTERN = DateTimeFormat.forPattern("yyyy_MMdd");
	public static final DateTimeFormatter DEFAULT_TIME_PATTERN = DateTimeFormat.forPattern("yyyy_MMdd_HHmm");

	/** 로그 이름 */
	private final String name;
	private final File dir;
	private final CsvAggregator<T> csvAggregator;
	
	/** 현재 파일을 열고 쓰고있는 파일의 확장자 */
	private String writingFileExtention =  "log";
	/** 파일 쓰기가 종료된, 롤링 파일의 확장자 */
	private String closedFileExtention =  "csv";
	/** 헤더가 필요하다면.. */
	private String[] header;
	/** 이스케이핑 관련 */
	private boolean csvRead = false;
	
	//===============  롤링 정보 ======================
	private DateTimeFieldType dateTimeFieldType = DateTimeFieldType.dayOfYear();
	/** 위 필드타입 기준 간격. */
	private int interval = 1;
	/** 파일이름 1순위 설정 */
	private Converter<DateTime,String> fileNameConverter;
	/** 파일이름 2순위 설정 */
	private DateTimeFormatter timePattern = DEFAULT_DATE_PATTERN;
	
	//==============  매니저에서 최기화되는 정보 =================
	private CSVWriter writer;
	private File writerFile;
	private long nextInterval;
	
	private CsvLogger<T> csvLogWriter;
	private BlockingQueue<CsvLog> queue;
	
	/** ex)  */
	public CsvLogger<T> getLogger(){
		return new CsvLogger<T>(this);
	}
	
	public CsvLogInfo<T> setType(DateTimeFieldType dateTimeFieldType,int interval,DateTimeFormatter timePattern){
		
		return this;
	}
	
	/** 
	 * 현재시각 기준으로 라이터를 교체한다.
	 * AA01.log로 저장되다가 타임이 지나면 AA02.log가 새로 생기고 기존 파일은 AA01.csv로 파일명이 변경된다. 
	 *  ==> 이거 걍 로그4j랑 동일하게 수정하자.
	 *  */
	public void reloadWriter() throws IOException {
		
		DateTime from = new DateTime().property(dateTimeFieldType).roundFloorCopy();
		
		if(writer!=null && writerFile!=null){
			close(from);
		}
		
		DateTime next = from.withFieldAdded(dateTimeFieldType.getDurationType(),interval);
		nextInterval = next.getMillis();
		
		File file = new File(dir,name + "."+writingFileExtention);  //로그4j 처럼 쓰는 로그파일은 고정이다. 중단된 파일이 있다면 이어쓴다.
		FileUtil.mkdirOrThrowException(file.getParentFile());
		
		FileOutputStream os = new FileOutputStream(file,true);
		OutputStreamWriter w = new OutputStreamWriter(os,csvRead ? "UTF-8" : "MS949");
		//BufferedWriter ww = new BufferedWriter(w,1024); //강종을 고려한다면 더 짧게 잡아야 할것이다.
		char escaper = csvRead ? CSVParser.DEFAULT_ESCAPE_CHARACTER : CSVWriter.DEFAULT_ESCAPE_CHARACTER;
		writer = new CSVWriter(w,CSVWriter.DEFAULT_SEPARATOR,CSVWriter.DEFAULT_QUOTE_CHARACTER,escaper);
		writerFile = file; 
		
		if(header!=null){
			writer.writeNext(header);
		}
	}
	
	/** 해당 시간대의 로그 파일을 리턴.. 쓰는데는 없다 */
	public File getLogFile(DateTime dateTime){
		DateTime from = dateTime.property(dateTimeFieldType).roundFloorCopy();
		File file = new File(dir,fileNameConverter.convert(from) + "."+closedFileExtention);
		return file;
	}
	
	/** 기존 라이터를 닫고 리네임해준다. */
	public void close(DateTime from) throws IOException {
		String fileName = fileNameConverter.convert(from); //파일 이름은 from 기준이다.  2014-03-11 파일은 2014-03-11 부터 쓰기 시작한 파일이다.
		File dest = new File(dir,fileName + "."+closedFileExtention);
		
		//재기동 등으로 인해서 여러 파일이 생길 수 있다.
		int index = 0;
		while(dest.exists()){
			dest = new File(writerFile.getParentFile(),fileName+"_"+ ++index +"."+closedFileExtention);
		}

		writer.close();
		FileUtil.renameTo(writerFile, dest);
	}
	
	public void init(){
		Preconditions.checkState(writer==null);
		Preconditions.checkState(writerFile==null);
		if(fileNameConverter==null) fileNameConverter = getDefaultDateConverter();
		try {
			reloadWriter();
		} catch (IOException e) {
			throw new IORuntimeException(e);
		}
	}
	
	
	/** 기본 컨버터. */
	public Converter<DateTime,String> getDefaultDateConverter(){
		return new Converter<DateTime,String>(){
			@Override
			public String convert(DateTime source) {
				return name+"_"+timePattern.print(source);
			}
		};
	}
	

	/** 샘플1 X일당 로그1개 */
	public static <T> CsvLogInfo<T> createDay(String name,File dir,CsvAggregator<T> csvAggregator,int interval){
		CsvLogInfo<T> info = new CsvLogInfo<T>(name,dir,csvAggregator);
		info.dateTimeFieldType = DateTimeFieldType.dayOfYear();
		info.interval = interval;
		info.timePattern = DEFAULT_DATE_PATTERN;
		return info;
	}
	
	/** 샘플2 X시간당 로그1개 */
	public static <T> CsvLogInfo<T> createHour(String name,File dir,CsvAggregator<T> csvAggregator,int interval){
		CsvLogInfo<T> info = new CsvLogInfo<T>(name,dir,csvAggregator);
		info.dateTimeFieldType = DateTimeFieldType.hourOfDay();
		info.interval = interval;
		info.timePattern = DEFAULT_TIME_PATTERN;
		return info;
	}
	
	/** 샘플3 X분당 로그1개 */
	public static <T> CsvLogInfo<T> createMin(String name,File dir,CsvAggregator<T> csvAggregator,int interval){
		CsvLogInfo<T> info = new CsvLogInfo<T>(name,dir,csvAggregator);
		info.dateTimeFieldType = DateTimeFieldType.minuteOfDay();
		info.interval = interval;
		info.timePattern = DEFAULT_TIME_PATTERN;
		return info;
	}
	

}
