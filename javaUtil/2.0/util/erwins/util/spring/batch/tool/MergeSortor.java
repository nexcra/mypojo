package erwins.util.spring.batch.tool;

import java.io.File;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

import lombok.Data;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.file.ResourceAwareItemReaderItemStream;
import org.springframework.batch.item.file.ResourceAwareItemWriterItemStream;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

import erwins.util.lib.FileUtil;
import erwins.util.nio.ThreadUtil;
import erwins.util.root.exception.PropagatedRuntimeException;
import erwins.util.text.StringUtil;

/**  
 * 이름이 머지소터지만 사실 머지소팅하지 않는다능...
 * 잘됨. 검증완료
 * 2.9G 파일 SSD 노트북 기준 3분 41초. 네이티와 크게 차이나지 않는다.
 *  */
public class MergeSortor<T> {
	
	private final Logger log = LoggerFactory.getLogger(this.getClass());
	private Comparator<T> comparator;
	private File tempDir;
	private String prefix = this.getClass().getSimpleName();
	private List<ExecutionContext> executionContextList = Lists.newCopyOnWriteArrayList();
	private ItemReaderFactory<T> itemReaderFactory;
	private ItemWriterFactory<T> itemWriterFactory;
	private ThreadPoolTaskExecutor pool;
	/** 한번에 메모리에 담을 수 있는 최대 양. 최초 파일을 나눌때 사용된다. */
	private int maxLineCount = 30000; //로그기준 대략 30MB정도?
	/** 한번에 버퍼에 쓰는 라인의 크기.  이거 * corePoolSize 만큼 메모리의 여유가 있어야 한다. */
	private int commitInterval = 1000;
	private int corePoolSize = 16;
	
	private File readDir;
	private File writeDir;
	
	private int currentStepCount = 0;
	private int currentFileCount = 0;
	
	public void init(File tempDir,Comparator<T> comparator,ItemReaderFactory<T> itemReaderFactory,ItemWriterFactory<T> itemWriterFactory){
		this.tempDir = tempDir;
		this.comparator = comparator;
		this.itemReaderFactory = itemReaderFactory;
		this.itemWriterFactory = itemWriterFactory;
	}
	
	/** INPUT은 외부 리소스가 올 수 있지만, 결과는 항상 File로 떨어진다. */
	public void sort(Resource resource,File outputFile){
		step1_splitFile(resource);
		
		pool = new ThreadPoolTaskExecutor();
		pool.setCorePoolSize(corePoolSize);
		pool.setThreadNamePrefix(prefix+"-thread-");
		pool.afterPropertiesSet();
		
		while(true){
			changeNextDir();
			File[] files = readDir.listFiles();
			Preconditions.checkState(files.length >= 1);
			if(files.length==1){
				FileUtil.renameTo(files[0], outputFile);
				break;
			}else{
				List<File[]> pairFiles = toPairFoles(files);
				
				List<Future<Integer>> futures = Lists.newArrayList();
				for(File[] each : pairFiles){
					if(each[1]==null){
						//사실 카피할 필요는 없다. 일단 테스트로 봐야하니까 넣자.
						//each[0].renameTo(getNextFile());
						FileUtil.copy(each[0], getNextFile());
					}else{
						Future<Integer> future = pool.submit(new Read2FileAndMerge(each,getNextFile())); 
						futures.add(future);
					}
				}
				try {
					Long sum = ThreadUtil.sum(futures);
					log.debug("스탭 {} 처리가 완료되었습니다. 총 커밋수 : {}",currentStepCount,sum);
				} catch (Exception e) {
					throw new PropagatedRuntimeException(e);
				}
			}	
		}
		
		
	}

	/** 파일들을 2개씩 짝지어 준다. */
	protected List<File[]> toPairFoles(File[] files) {
		List<File[]> pairFiles = Lists.newArrayList();
		for(int i=0;i<files.length;i+=2){
			File[] pair = new File[2];
			pair[0] = files[i];
			if((i+1) < files.length) pair[1] = files[i+1];
			pairFiles.add(pair);
		}
		return pairFiles;
	}
	
	public ExecutionContext addAndGetContext(){
		ExecutionContext ec = new ExecutionContext();
		executionContextList.add(ec);
		return ec;
	}
	
	@Data
	public class Read2FileAndMerge implements Callable<Integer>{

		private final File[] from;
		private final File to;

		@Override
		public Integer call() throws Exception {
			int commitCount = 0;
			ResourceAwareItemReaderItemStream<T> itemReader1 = null;
			ResourceAwareItemReaderItemStream<T> itemReader2 = null;
			ResourceAwareItemWriterItemStream<T> itemWriter = null;
			try {
				itemReader1 = (ResourceAwareItemReaderItemStream<T>) itemReaderFactory.readerInstance();
				itemReader2 = (ResourceAwareItemReaderItemStream<T>) itemReaderFactory.readerInstance();
				itemWriter = (ResourceAwareItemWriterItemStream<T>) itemWriterFactory.writerInstance();
				
				itemReader1.setResource(new FileSystemResource(from[0]));
				itemReader1.open(addAndGetContext());
				
				itemReader2.setResource(new FileSystemResource(from[1]));
				itemReader2.open(addAndGetContext());
				
				itemWriter.setResource(new FileSystemResource(to));
				itemWriter.open(addAndGetContext());
				
				List<T> list = Lists.newArrayList();
				
				//첫 아이템은 무조건 있다고 간주한다.
				T item1 =  itemReader1.read();
				T item2 =  itemReader2.read();
				
				while(true){
					
					if(item1==null && item2==null) break;
					
					//1번이 이기는게 win			
					boolean win;
					if(item1==null) win = false;
					else if(item2==null) win = true; 
					else{
						win = comparator.compare(item1, item2) <= 0; //0이면 앞에께 먼저 온다. 			
					}
					
					if(win){
						list.add(item1);
						item1 =  itemReader1.read();
					}else{
						list.add(item2);
						item2 =  itemReader2.read();
					}
					
					if(list.size() >= commitInterval){
						itemWriter.write(list);
						commitCount++;
						itemWriter.update(addAndGetContext());
						list = Lists.newArrayList();
					}
				}
				itemWriter.write(list);
				commitCount++;
			} catch (Exception e) {
				throw new PropagatedRuntimeException(e);
			}finally{
				if(itemReader1!=null) itemReader1.close();
				if(itemReader2!=null) itemReader2.close();
				if(itemWriter!=null) itemWriter.close();			
			}
			return commitCount;
		}
		
	}

	protected void step1_splitFile(Resource resource) {
		changeNextDir();
		ResourceAwareItemReaderItemStream<T> itemReader = null;
		ResourceAwareItemWriterItemStream<T> itemWriter = null;
		try {
			itemReader = (ResourceAwareItemReaderItemStream<T>) itemReaderFactory.readerInstance();
			itemWriter = (ResourceAwareItemWriterItemStream<T>) itemWriterFactory.writerInstance();
			itemReader.setResource(resource);
			itemReader.open(addAndGetContext());
			
			itemWriter.setResource(new FileSystemResource(getNextFile()));
			itemWriter.open(addAndGetContext());
			
			List<T> list = Lists.newArrayList();
			while(true){
				T item =  itemReader.read();
				if(item==null) break;
				list.add(item);
				if(list.size() >= maxLineCount){
					Collections.sort(list, comparator); //이부분이 일반 배치와 다르다.
					itemWriter.write(list);
					itemWriter.close(); //update는 하지 않는다.
					itemWriter.setResource(new FileSystemResource(getNextFile()));
					itemWriter.open(addAndGetContext());
					list = Lists.newArrayList();
				}
			}
			itemWriter.write(list);
		} catch (Exception e) {
			throw new PropagatedRuntimeException(e);
		}finally{
			if(itemReader!=null) itemReader.close();
			if(itemWriter!=null) itemWriter.close();
		}
	}

	protected void changeNextDir() {
		readDir = writeDir;
		writeDir = new File(tempDir,"STEP_"+StringUtil.leftPad(++currentStepCount, 2));
		if(!writeDir.isDirectory()) writeDir.mkdirs();
		log.debug("다음 {} 스탭을 진행합니다",currentStepCount);
		currentFileCount = 0;
	}
	
	protected File getNextFile() {
		return new File(writeDir,prefix + "_" + StringUtil.leftPad(++currentFileCount, 4));
	}
	

}
