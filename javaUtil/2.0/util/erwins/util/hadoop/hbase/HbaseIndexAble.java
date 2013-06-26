package erwins.util.hadoop.hbase;



/**
 * @author sin
 */
public interface HbaseIndexAble{
	
	/** indexNumber는 0부터 시작한다. 
	 * 내부적으로 null을 리턴할때 까지 인덱싱을 반복한다. */
	public byte[] getIndexKey(int indexNumber);

}
