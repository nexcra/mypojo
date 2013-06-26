package erwins.util.hadoop.hbase;

import java.util.List;

import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.Scan;

import erwins.util.hadoop.hbase.RowKeyAble;



/**
 * @author sin
 */
public interface HbaseIndexProcessor<T extends RowKeyAble>{
	
	public void putIndex(T vo);
	public void deleteIndex(T vo);
	/**  final Integer limit,final Integer skipRow 같은 옵션은 해당 규현체를 따른다. */
	public List<Get> findIndex(Integer indexNumber,Scan scan);
	public void createIndex(Integer indexNumber);
	public void dropIndex(Integer indexNumber);
	public int indexSize();
	
	public static class HbaseIndexNullProcessor<T extends RowKeyAble> implements HbaseIndexProcessor<T>{

		@Override
		public List<Get> findIndex(Integer indexNumber, Scan scan) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void createIndex(Integer indexNumber) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void dropIndex(Integer indexNumber) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void putIndex(T vo) {
		}

		@Override
		public void deleteIndex(T vo) {
		}

		@Override
		public int indexSize() {
			return 0;
		}
		
	}

}
