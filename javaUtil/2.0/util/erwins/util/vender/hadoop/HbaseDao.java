package erwins.util.vender.hadoop;

import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import org.apache.hadoop.hbase.client.Delete;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.HTableInterface;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.filter.Filter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.data.hadoop.hbase.HbaseTemplate;
import org.springframework.data.hadoop.hbase.TableCallback;
import org.springframework.transaction.support.TransactionSynchronizationAdapter;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

import erwins.util.vender.hadoop.HbaseIndexProcessor.HbaseIndexNullProcessor;

/**
 * 제작환경이 트랜잭션에 민감하지 못해, 스래드로컬로 통합했다.
 * 현재 스프링의 트랜잭션에 묶이지 않는 경우, 별도의 커밋처리를 해주어야 한다. 
 * !!!! 주의!!!!!   스래드로컬 기반임으로  싱글스래드에서만 동작한다. 병렬프로그래밍의 경우 매 커밋주기마다 실행해주도록 하자. 
 * @author sin
 */
public class HbaseDao<T extends RowKeyAble> implements InitializingBean{
	
	private Logger log = LoggerFactory.getLogger(this.getClass());
	private HbaseTemplate hbaseTemplate;
	private String tableName;
	private HbaseSerializer<T> serializer;
	private HbaseIndexProcessor<T> indexProcessor = new HbaseIndexNullProcessor<T>();
	// === 대체 가능하면 변경하자.
	private int batchSize = 100;
	private int cacheRowSize = 5;
	
	public void put(T vo) {
		HbaseCommitDao dao = getDao();
		Put put = serializer.toPut(vo);
		indexProcessor.putIndex(vo);
		dao.put(tableName,put);
    }
	
	public void delete(T vo) {
		HbaseCommitDao dao = getDao();
		dao.delete(tableName,new Delete(vo.getRowKey()));
		indexProcessor.deleteIndex(vo);
    }
	
	/** 사용후 반드시 닫아주어야 한다. */
	private static final ThreadLocal<HbaseCommitDao> MANUAL_COMMIT = new ThreadLocal<HbaseCommitDao>();
	
	/**
	 * 동일 스래드의 다른 DAO에서 호출하더라도 동일한   HbaseCommitDao 가 리턴된다. */
	private HbaseCommitDao getDao() {
		HbaseCommitDao dao = MANUAL_COMMIT.get();
		if(dao==null) {
			final HbaseCommitDao newDao = new HbaseCommitDao(hbaseTemplate);
			MANUAL_COMMIT.set(newDao);
			if(TransactionSynchronizationManager.isSynchronizationActive()){
				TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronizationAdapter(){
					@Override
					public void afterCompletion(int status) {
						if(!TransactionSynchronizationManager.isSynchronizationActive()) throw new IllegalStateException("transaction is not Avtive"); 
						try{
							log.info("TransactionSynchronizationManager와 연결되어 Hbase 가 {}됩니다.",STATUS_COMMITTED == status?"커밋":"롤백");
							if(STATUS_COMMITTED == status) newDao.afterCompletionCommit();
							else newDao.afterCompletionRollback();
						}finally{
							MANUAL_COMMIT.remove();
						}
					}
				});
			}
			dao = newDao;
		}
		return dao;
	}
	
	public static void afterCompletionCommit() {
		HbaseCommitDao dao = MANUAL_COMMIT.get();
		if(dao!=null) dao.afterCompletionCommit(); //내부적으로 예외를 던지지 않는다
	}

	public static void afterCompletionRollback() {
		HbaseCommitDao dao = MANUAL_COMMIT.get();
		if(dao!=null) dao.afterCompletionRollback(); //내부적으로 예외를 던지지 않는다
	}
	
	/** filter 등에서 잡아주어야 한다. */
	public static void remove() {
		MANUAL_COMMIT.remove();
	}
	
	
    // ========================================================================================================================
    // ===================================================== 특이한 상황 ======================================================
    // ========================================================================================================================	
	
	/** 강제 단건 입력 */
	public void putForce(T vo) {
		final Put put = serializer.toPut(vo);
		hbaseTemplate.execute(tableName, new TableCallback<RowKeyAble>() {
			@Override
			public RowKeyAble doInTable(HTableInterface htable) throws Throwable {
				htable.put(put);
				return null;
			}
		});
    }
	
	@Override
	public void afterPropertiesSet() {
		Preconditions.checkNotNull(tableName, "tableName은 필수입력항목입니다.");
		Preconditions.checkNotNull(hbaseTemplate, "hbaseTemplate은 필수입력항목입니다.");
		Preconditions.checkNotNull(serializer, "serializer은 필수입력항목입니다.");
	}
    
    public boolean createTable(){
		return HbaseUtil.createTable(hbaseTemplate.getConfiguration(), serializer.createTable(tableName));
	}
    
    public boolean dropTable(){
    	return HbaseUtil.dropTable(hbaseTemplate.getConfiguration(), tableName);
	}
    
    
    // ========================================================================================================================
    // ======================================================== 조회 ==========================================================
    // ========================================================================================================================
    
    public T get(final byte[] rowKey){
    	return hbaseTemplate.execute(tableName, new TableCallback<T>() {
			@Override
			public T doInTable(HTableInterface htable) throws Throwable {
				Result result = htable.get(new Get(rowKey));
				return serializer.mapRow(result, 0);
			}
		});
    }
    
    public ScanDao newScanDao(){
    	return new ScanDao();
    }
    
    
    /** 상세 검색시 사용 */
    public class ScanDao implements Iterable<T>{
    	private Integer limit = 1000; //기본설정
    	private Integer skipRow = 0;
    	private List<Get> indexes;
    	private List<T> list = Lists.newArrayList();
    	//private List<HbaseSerializeException> exceptions = Lists.newArrayList();
    	
    	public ScanDao findIndex(final Integer indexNumber,Scan scan){
    		indexes = indexProcessor.findIndex(indexNumber, scan);
    		return this;
    	}
    	
    	/** 필터는 인덱스를 구할때가 아니라, 구해진 인덱스로 실제 테이블을 조회할때 사용된다. */
    	public ScanDao findByIndex(Filter filter){
    		Preconditions.checkNotNull(indexes, "index를 먼저 구해야 합니다");
    		if(indexes.size() == 0 ) return this;
    		for(Get each : indexes) each.setFilter(filter);
    		hbaseTemplate.execute(getTableName(), new TableCallback<Result[]>() {
    			@Override
    			public Result[] doInTable(HTableInterface htable) throws Throwable {
    				int i = 0; //각각의 스캔마다 카운트를 센다.
    				Result[] results = htable.get(indexes);
    				for(Result result : results){
    					if(result.isEmpty()) continue; // Get이 filter를 통과하지 못하면 empty가 리턴된다.
    					T vo = serializer.mapRow(result, i++);
    					list.add(vo);
    				}		
    				return null;
    			}
    		});
    		return this;
    	}
    	
    	/** 인덱스 스킵 스캔 사용시 사용 */
    	public List<T> sort(Comparator<T> comparator){
    		Collections.sort(list,comparator);
    		return list;
    	}

        /** limit로 페이징 처리한다.
         * 직전 페이징에서 얻은 마지막 스캔본을 start로 입력하면 된다.
         * limit를 채울때까지 돌림으로, 검색값이 없다면 풀스캔한다. 따라서  setStopRow를 반드시 지정해야 한다.  */
        public ScanDao find(final Scan scan){
        	Preconditions.checkState(skipRow >= 0, "skipRow는 0보다 크거나 같아야 합니다");
        	if(scan.getBatch()==0) scan.setBatch(batchSize);
        	if(scan.getCaching()==0) scan.setCaching(cacheRowSize);

        	hbaseTemplate.execute(tableName, new TableCallback<T>() {
    			@Override
    			public T doInTable(HTableInterface htable) throws Throwable {
    				int count = 0; //각각의 스캔마다 카운트를 센다.
    				ResultScanner rs = htable.getScanner(scan); 
    				Iterator<Result> i = rs.iterator();
    				int skipCount = skipRow;
    				while(i.hasNext()){
    					if(limit!=null && limit <= count) break;
    					Result result =  i.next();
    					if(skipCount!=0){
    						skipCount--;
    						continue;
    					}
    					T vo = serializer.mapRow(result, count++);
    					list.add(vo);
    				}
    				return null;
    			}
    		});
        	return this;
        }
    	
    	
		public Integer getLimit() {
			return limit;
		}
		public ScanDao setLimit(Integer limit) {
			this.limit = limit;
			return this;
		}
		public Integer getSkipRow() {
			return skipRow;
		}
		public ScanDao setSkipRow(Integer skipRow) {
			this.skipRow = skipRow;
			return this;
		}
		public List<Get> getGets() {
			return indexes;
		}
		public ScanDao setGets(List<Get> gets) {
			this.indexes = gets;
			return this;
		}
		public List<T> getList() {
			return list;
		}
		public ScanDao setList(List<T> list) {
			this.list = list;
			return this;
		}
		public List<Get> getIndexes() {
			return indexes;
		}
		@Override
		public Iterator<T> iterator() {
			return list.iterator();
		}
    }
	
    // ========================================================================================================================
    // =================================================== getter / setter ====================================================
    // ========================================================================================================================	
    
	public void setHbaseTemplate(HbaseTemplate hbaseTemplate) {
		this.hbaseTemplate = hbaseTemplate;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
	public String getTableName() {
		return tableName;
	}

	public void setBatchSize(int batchSize) {
		this.batchSize = batchSize;
	}

	public void setCacheRowSize(int cacheRowSize) {
		this.cacheRowSize = cacheRowSize;
	}

	public void setSerializer(HbaseSerializer<T> serializer) {
		this.serializer = serializer;
	}

	public void setIndexProcessor(HbaseIndexProcessor<T> indexProcessor) {
		this.indexProcessor = indexProcessor;
	}

	public HbaseIndexProcessor<T> getIndexProcessor() {
		return indexProcessor;
	}
	

}
