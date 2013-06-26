package erwins.util.hadoop.hbase;

import java.util.Collection;
import java.util.Map.Entry;

import org.apache.hadoop.hbase.client.Delete;
import org.apache.hadoop.hbase.client.HTableInterface;
import org.apache.hadoop.hbase.client.Put;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.hadoop.hbase.HbaseTemplate;
import org.springframework.data.hadoop.hbase.TableCallback;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;

import erwins.util.spring.TransactionSynchCommit.AfterCompletionAble;

/**
 * 해당 DB의 트랜잭션이 정상적으로 커밋되면 HBase에도 입력한다. 
 * 로그성 데이터도 많음으로 readOnly 트랜잭션이라도 무시하고 입력해야 한다.
 * @author sin
 */
public class HbaseCommitDao implements AfterCompletionAble{
	
	private final HbaseTemplate hbaseTemplate;
	private Multimap<String,Put> puts = HashMultimap.create();
	/** 삭제할일은 거의 없다. */
	private Multimap<String,Delete> deletes = HashMultimap.create();
	private Logger log = LoggerFactory.getLogger(this.getClass());
	
	/** DB트랜잭션에 묶여있지 않다면 자동으로 커밋되지 않는다. */
	public <T extends RowKeyAble> HbaseCommitDao(HbaseTemplate hbaseTemplate) {
		this.hbaseTemplate = hbaseTemplate;
	}
    
	public void put(String tableName,Put put){
    	puts.put(tableName, put);
    }
	
    public void delete(String tableName,Delete delete){
    	deletes.put(tableName,delete);
    }

    /** Hbase에는 중요한 로직이 없다고 가정한다.
     * 예외 발생시 DB는 커밋되었음으로 다시 예외를 던지지 않고 로그만 찍는다. */
	@Override
	public void afterCompletionCommit() {
		try{
			for(final Entry<String,Collection<Put>> entry : puts.asMap().entrySet()){
				hbaseTemplate.execute(entry.getKey(), new TableCallback<RowKeyAble>() {
					@Override
					public RowKeyAble doInTable(HTableInterface htable) throws Throwable {
						htable.put(Lists.newArrayList(entry.getValue()));
						return null;
					}
				});
			}
			for(final Entry<String,Collection<Delete>> entry : deletes.asMap().entrySet()){
				hbaseTemplate.execute(entry.getKey(), new TableCallback<RowKeyAble>() {
					@Override
					public RowKeyAble doInTable(HTableInterface htable) throws Throwable {
						htable.delete(Lists.newArrayList(entry.getValue()));
						return null;
					}
				});
			}
		}catch(Exception e){
			log.error("트랜잭션이 커밋되어 Hbase가 입력되는 도중 예외발생. 이 예외는 로그만 남고 무시됩니다.",e);
		}finally{
			puts.clear();
			deletes.clear();	
		}
	}

	@Override
	public void afterCompletionRollback() {
		if(puts.size()==0 && deletes.size()==0) return;
		log.warn("트랜잭션이 롤백되어 Hbase 입력{}건 삭제 {}건을 취소합니다.",puts.size(),deletes.size());
		puts.clear();
		deletes.clear();
	}

}
