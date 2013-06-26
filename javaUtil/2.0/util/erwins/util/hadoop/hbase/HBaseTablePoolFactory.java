package erwins.util.hadoop.hbase;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.commons.pool.BaseKeyedPoolableObjectFactory;
import org.apache.commons.pool.impl.GenericKeyedObjectPool;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.HTableInterface;
import org.apache.hadoop.hbase.client.HTableInterfaceFactory;
import org.apache.hadoop.hbase.util.Bytes;
import org.springframework.beans.factory.InitializingBean;

import com.google.common.base.Preconditions;

/**
 * 테이블을 닫지 않고, 풀에 저장한다. (스프링이 열닫 호출은 해주지만 풀링을 해주지는 않는다.)
 * 스캐너는 별도로 반드시 close 해주어야 한다.
 * byte[] 는 키값으로 사용하지 못한다. 주의할것!
 */
public class HBaseTablePoolFactory implements InitializingBean,HTableInterfaceFactory{
	
	private final GenericKeyedObjectPool pool = new GenericKeyedObjectPool();
	private Configuration configuration;
	private final AtomicLong create = new AtomicLong();
	private final AtomicLong use = new AtomicLong();
	
	@Override
	public void releaseHTableInterface(HTableInterface htable) throws IOException {
		String tableName = Bytes.toString(htable.getTableName());
		try {
			pool.returnObject(tableName,htable);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public HTableInterface createHTableInterface(Configuration arg0, byte[] arg1) {
		use.incrementAndGet();
		String tableName = Bytes.toString(arg1);
		try {
			HTable table = (HTable)pool.borrowObject(tableName);
			return table;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	public GenericKeyedObjectPool getPool() {
		return pool;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		Preconditions.checkNotNull(configuration, "configuration를 입력해주세요");
		pool.setFactory(new BaseKeyedPoolableObjectFactory() {
			/** 실제 커넥션이 생성될때 호출되며, 현재 장비에서 9초 정도 걸리는거 같다. */
			@Override
			public Object makeObject(Object tableName) throws Exception {
				create.incrementAndGet();
				return new HTable(configuration,(String) tableName);
			}
		});
	}
	
	public void setConfiguration(Configuration configuration) {
		this.configuration = configuration;
	}

	// ============= 위임 메소드 ====================


	public void setMaxActive(int maxActive) {
		pool.setMaxActive(maxActive);
	}


	public void setMaxIdle(int maxIdle) {
		pool.setMaxIdle(maxIdle);
	}


	public void setMaxTotal(int maxTotal) {
		pool.setMaxTotal(maxTotal);
	}

	/** 풀 획득에 락이 걸리며, 이 시간(ms)이 초과되면 예외를 던진다 */
	public void setMaxWait(long maxWait) {
		pool.setMaxWait(maxWait);
	}


	public void setMinEvictableIdleTimeMillis(long minEvictableIdleTimeMillis) {
		pool.setMinEvictableIdleTimeMillis(minEvictableIdleTimeMillis);
	}


	public void setMinIdle(int poolSize) {
		pool.setMinIdle(poolSize);
	}


	public void setNumTestsPerEvictionRun(int numTestsPerEvictionRun) {
		pool.setNumTestsPerEvictionRun(numTestsPerEvictionRun);
	}


	public void setTestOnBorrow(boolean testOnBorrow) {
		pool.setTestOnBorrow(testOnBorrow);
	}


	public void setTestOnReturn(boolean testOnReturn) {
		pool.setTestOnReturn(testOnReturn);
	}


	public void setTestWhileIdle(boolean testWhileIdle) {
		pool.setTestWhileIdle(testWhileIdle);
	}


	public void setTimeBetweenEvictionRunsMillis(
			long timeBetweenEvictionRunsMillis) {
		pool.setTimeBetweenEvictionRunsMillis(timeBetweenEvictionRunsMillis);
	}


	public void setWhenExhaustedAction(byte whenExhaustedAction) {
		pool.setWhenExhaustedAction(whenExhaustedAction);
	}

	public AtomicLong getCreate() {
		return create;
	}

	public AtomicLong getUse() {
		return use;
	}
	
}
