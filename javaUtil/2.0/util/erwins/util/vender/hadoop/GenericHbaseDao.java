package erwins.util.vender.hadoop;

import java.io.IOException;
import java.util.List;

import javax.annotation.Resource;

import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.client.Delete;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.HBaseAdmin;
import org.apache.hadoop.hbase.client.HTableInterface;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.util.Bytes;
import org.springframework.data.hadoop.hbase.HbaseTemplate;
import org.springframework.data.hadoop.hbase.RowMapper;
import org.springframework.data.hadoop.hbase.TableCallback;

import com.google.common.collect.Lists;

/**
 * 일단 RowMapper를 사용한다.
 * update는 따로 없다. insert가 한번 더 되면 타임스탬프가 새로 들어간 로우로 대체된다.
 * @author sin
 */
@Deprecated
public abstract class GenericHbaseDao<T extends RowKeyAble>{
	
	@Resource protected HbaseTemplate hbaseTemplate;
	
	protected abstract String getTableName();
	
	protected abstract RowMapper<T> getRowMapper();
	
	protected abstract Put toPut(T vo);
	
	/** 테이블 스키마 확인용 */
	protected abstract HTableDescriptor getDescriptor();
	
    public void insert(final T vo) {
    	hbaseTemplate.execute(getTableName(), new TableCallback<T>() {
			@Override
			public T doInTable(HTableInterface htable) throws Throwable {
				htable.put(toPut(vo));
				return null;
			}
		});
    }
    public void insert(final List<T> vos) {
    	final List<Put> puts = Lists.newArrayList();
    	for(T vo : vos) puts.add(toPut(vo));
    	hbaseTemplate.execute(getTableName(), new TableCallback<T>() {
			@Override
			public T doInTable(HTableInterface htable) throws Throwable {
				htable.put(puts);
				return null;
			}
		});
    }
    
    public void delete(final T vo) {
    	hbaseTemplate.execute(getTableName(), new TableCallback<T>() {
			@Override
			public T doInTable(HTableInterface htable) throws Throwable {
				htable.delete(new Delete(vo.getRowKey()));
				return null;
			}
		});
    }
    public void delete(final List<T> vos) {
    	final List<Delete> deletes = Lists.newArrayList();
    	for(T vo : vos) deletes.add(new Delete(vo.getRowKey()));
    	hbaseTemplate.execute(getTableName(), new TableCallback<T>() {
			@Override
			public T doInTable(HTableInterface htable) throws Throwable {
				htable.delete(deletes);
				return null;
			}
		});
    }
    
    /** 뭔지 모르겠음.. PK가 문자열만 되는건지? */
    public T selectByPk(String pk) {
    	return hbaseTemplate.get(getTableName(), pk, getRowMapper());
    }
    
    public T selectByPk(Long pk) {
    	return selectByPk(Bytes.toBytes(pk));
    }
    
    public T selectByPk(byte[] pk) {
    	Scan scan = new Scan(new Get(pk));
    	List<T> result =  selectAll(scan);
    	if(result.size()==0) return null;
    	return result.get(0);
    }
    
    /** 
     * 스트리밍 처리하지않고 메모리에 다 올려서 리턴한다.
     * Scan의 startRow는 스캔 대상에 포함되고 stopRow는 스캔 대상에서 제외된다. */
    public List<T> selectAll(Scan scan) {
    	return hbaseTemplate.find(getTableName(), scan, getRowMapper());
    }
    
    public boolean createTable(){
    	HTableDescriptor desc = getDescriptor();
		HBaseAdmin hBaseAdmin = null;
		try {
			hBaseAdmin = new HBaseAdmin(hbaseTemplate.getConfiguration());
			if (hBaseAdmin.isTableAvailable(desc.getName())) return false; //이미 활성화된 테이블
			hBaseAdmin.createTable(desc);
			return true;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}finally{
			if(hBaseAdmin!=null) {
				try {
					hBaseAdmin.close();
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			}
		}
	}
    
    public boolean dropTable(){
		HBaseAdmin hBaseAdmin = null;
		try {
			hBaseAdmin = new HBaseAdmin(hbaseTemplate.getConfiguration());
			if (!hBaseAdmin.isTableAvailable(getTableName())) return false;
			hBaseAdmin.disableTable(getTableName());
			hBaseAdmin.deleteTable(getTableName());
			return true;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}finally{
			if(hBaseAdmin!=null) {
				try {
					hBaseAdmin.close();
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			}
		}
	}

}
