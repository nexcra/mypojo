package erwins.util.vender.hadoop;

import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.util.Bytes;

import erwins.util.vender.hadoop.HbaseSerializer;



/**
 * 키값 + 타임스탬프로 간단 키를 생성한다.
 * @author sin
 */
public class HbaseIndexSerializer implements HbaseSerializer<HbaseIndex>{

	/** 기본패밀리 */
	private static final byte[] FAMILLY_NAME_INDEX = Bytes.toBytes("idx");
	/** 기본퀄리파이어 - 실제 테이블의 로우키 */
    private static final byte[] QUALIFIER_NAME_ROW_KEY = Bytes.toBytes("k");
	
	@Override
	public HbaseIndex mapRow(Result arg0, int arg1) throws Exception {
		HbaseIndex index = new HbaseIndex();
		index.setTableRowKey(arg0.getValue(FAMILLY_NAME_INDEX, QUALIFIER_NAME_ROW_KEY));
		return index;
	}

	@Override
	public Put toPut(HbaseIndex vo) {
		Put put  = new Put(vo.getRowKey());
    	put.add(FAMILLY_NAME_INDEX,QUALIFIER_NAME_ROW_KEY,vo.getTableRowKey());
    	return put;
	}

	@Override
	public HTableDescriptor createTable(String tableName) {
		HTableDescriptor table = new HTableDescriptor(tableName);
    	HColumnDescriptor columndescriptor = new HColumnDescriptor(FAMILLY_NAME_INDEX);
    	columndescriptor.setMaxVersions(1);
		table.addFamily(columndescriptor);
		return table;
	}

}
