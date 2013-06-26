package erwins.util.hadoop.hbase;

import java.util.List;

import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.util.Bytes;
import org.springframework.data.hadoop.hbase.HbaseTemplate;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;



/**
 * @author sin
 */
public class HbaseIndexManager<T extends RowKeyAble> implements HbaseIndexProcessor<T>{
	
	private List<HbaseDao<HbaseIndex>> indexs = Lists.newArrayList();
	
	protected static final int TIMESTAMP_START = Bytes.ESTIMATED_HEAP_TAX * 2;
	protected static final int TIMESTAMP_END = Bytes.ESTIMATED_HEAP_TAX * 2 + Bytes.SIZEOF_LONG * 1;
	
	public void addIndex(HbaseTemplate hbaseTemplate,String IndexTableName) {
		HbaseDao<HbaseIndex> dao = new HbaseDao<HbaseIndex>();
		dao.setTableName(IndexTableName);
		dao.setHbaseTemplate(hbaseTemplate);
		dao.setSerializer(new HbaseIndexSerializer());
		dao.afterPropertiesSet();
		indexs.add(dao);
	}
	
	@Override
	public void putIndex(T vo) {
		if(vo instanceof HbaseIndexAble){
			byte[] tableRowKey = vo.getRowKey();
			HbaseIndexAble indexAbleVo = (HbaseIndexAble) vo;
			
			int size = indexs.size();
			for(int i=0;i<size;i++){ //혹시나 해서 10까지만
				byte[] indexKey = indexAbleVo.getIndexKey(i);
				if(indexKey==null) break;
				
				HbaseIndex index = new HbaseIndex(indexKey);
				index.setTableRowKey(tableRowKey);
				
				HbaseDao<HbaseIndex> dao = indexs.get(i);
				dao.put(index);
			}
		}
	}

	@Override
	public void deleteIndex(T vo) {
		if(vo instanceof HbaseIndexAble){
			byte[] tableRowKey = vo.getRowKey();
			HbaseIndexAble indexAbleVo = (HbaseIndexAble) vo;
			
			int size = indexs.size();
			for(int i=0;i<size;i++){ //혹시나 해서 10까지만
				byte[] indexKey = indexAbleVo.getIndexKey(i);
				if(indexKey==null) break;
				
				HbaseIndex index = new HbaseIndex(indexKey);
				index.setTableRowKey(tableRowKey);
				
				HbaseDao<HbaseIndex> dao = indexs.get(i);
				dao.delete(index);
			}
		}
	}

	@Override
	public List<Get> findIndex(Integer indexNumber, Scan scan) {
		Preconditions.checkArgument(indexNumber < indexs.size(), indexNumber + " 는 지원하지 않는 인덱스 범위입니다.");
		
		HbaseDao<HbaseIndex> index = indexs.get(indexNumber);
		
		List<HbaseIndex> list = index.newScanDao().find(scan).getList();
		
		final List<Get> gets = Lists.newArrayList();
		for(HbaseIndex each : list) gets.add(new Get(each.getTableRowKey()));
		return gets;
	}

	@Override
	public void createIndex(Integer indexNumber) {
		if(indexNumber==null) for(HbaseDao<HbaseIndex> each : indexs) each.createTable();
		else{
			Preconditions.checkArgument(indexNumber < indexs.size(), indexNumber + " 는 지원하지 않는 인덱스 범위입니다.");
			indexs.get(indexNumber).createTable();
		}
	}

	@Override
	public void dropIndex(Integer indexNumber) {
		if(indexNumber==null) for(HbaseDao<HbaseIndex> each : indexs) each.dropTable();
		else{
			Preconditions.checkArgument(indexNumber < indexs.size(), indexNumber + " 는 지원하지 않는 인덱스 범위입니다.");
			indexs.get(indexNumber).dropTable();
		}
	}

	@Override
	public int indexSize() {
		return indexs.size();
	}
	

}
