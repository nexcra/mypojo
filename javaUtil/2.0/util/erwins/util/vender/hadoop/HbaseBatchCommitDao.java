package erwins.util.vender.hadoop;

import org.apache.hadoop.hbase.client.Delete;
import org.apache.hadoop.hbase.client.Put;

/**
 * 배치용 수동 커밋
 * @author sin
 */
@Deprecated
public class HbaseBatchCommitDao<T extends RowKeyAble>{
	
	private final HbaseCommitDao hbaseCommitDao;
	private final HbaseSerializer<T> serializer;
	private final String tableName;
	
	public HbaseBatchCommitDao(HbaseCommitDao hbaseCommitDao, HbaseSerializer<T> serializer,String tableName) {
		super();
		this.hbaseCommitDao = hbaseCommitDao;
		this.serializer = serializer;
		this.tableName = tableName;
	}
	
	public void put(T vo) {
		Put put = serializer.toPut(vo);
		hbaseCommitDao.put(tableName,put);
    }
	
	public void delete(T vo) {
		hbaseCommitDao.delete(tableName,new Delete(vo.getRowKey()));
    }

	public HbaseCommitDao getHbaseCommitDao() {
		return hbaseCommitDao;
	}

	public HbaseSerializer<T> getSerializer() {
		return serializer;
	}

	public void commit() {
		hbaseCommitDao.afterCompletionCommit();
	}

}
