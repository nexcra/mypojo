package erwins.util.hadoop.hbase;

import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.client.Put;
import org.springframework.data.hadoop.hbase.RowMapper;


/**
 * VO와 Hbase간의 데이터 타입 변경을 정의한다.
 * @author sin
 */
public interface HbaseSerializer<T extends RowKeyAble> extends RowMapper<T>{
	
	public Put toPut(T vo);	
	
	public HTableDescriptor createTable(String tableName);

}
