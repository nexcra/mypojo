package erwins.util.hadoop.hbase;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicLong;

import lombok.experimental.Delegate;

import org.apache.commons.pool2.BaseKeyedPooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.GenericKeyedObjectPool;
import org.apache.commons.pool2.impl.GenericKeyedObjectPoolConfig;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.HTableInterface;
import org.apache.hadoop.hbase.client.HTableInterfaceFactory;
import org.apache.hadoop.hbase.util.Bytes;
import org.springframework.beans.factory.InitializingBean;

import com.google.common.base.Preconditions;

import erwins.util.root.exception.PropagatedRuntimeException;

/**
 * 테이블을 닫지 않고, 풀에 저장한다. (스프링이 열닫 호출은 해주지만 풀링을 해주지는 않는다.)
 * 스캐너는 별도로 반드시 close 해주어야 한다.
 * byte[] 는 키값으로 사용하지 못한다. 주의할것!
 * 
 * ==>  대충 고쳤는데 모르겠다. 써봐야함 먼저 구글링할것!!
 */
public class HBaseTablePoolFactory implements InitializingBean,HTableInterfaceFactory{
	
	@Delegate
	private final GenericKeyedObjectPoolConfig config = new GenericKeyedObjectPoolConfig();
	private GenericKeyedObjectPool<String,HTableInterface> pool;
	private Configuration configuration;
	private final AtomicLong create = new AtomicLong();
	private final AtomicLong use = new AtomicLong();
	
	@Override
	public void releaseHTableInterface(HTableInterface htable) throws IOException {
		String tableName = Bytes.toString(htable.getTableName());
		try {
			pool.returnObject(tableName,htable);
		} catch (Exception e) {
			throw new PropagatedRuntimeException(e);
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
			throw new PropagatedRuntimeException(e);
		}
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		Preconditions.checkNotNull(configuration, "configuration를 입력해주세요");
		//기존 BaseKeyedPoolableObjectFactory
		BaseKeyedPooledObjectFactory<String,HTableInterface> factory = new BaseKeyedPooledObjectFactory<String, HTableInterface>() {
			@Override
			public HTableInterface create(String tableName) throws Exception {
				create.incrementAndGet();
				return new HTable(configuration,(String) tableName);
			}
			@Override
			public PooledObject<HTableInterface> wrap(HTableInterface arg0) {
				System.out.println("모르겠다");
				return null;
			}
		};
		pool = new GenericKeyedObjectPool<String,HTableInterface>(factory,config);
	}
	
	public void setConfiguration(Configuration configuration) {
		this.configuration = configuration;
	}
	public GenericKeyedObjectPool<String,HTableInterface> getPool() {
		return pool;
	}

	public AtomicLong getCreate() {
		return create;
	}

	public AtomicLong getUse() {
		return use;
	}
	
}
