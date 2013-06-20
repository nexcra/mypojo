package erwins.util.vender.hadoop;

import java.util.List;

import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.filter.CompareFilter.CompareOp;
import org.apache.hadoop.hbase.filter.Filter;
import org.apache.hadoop.hbase.filter.FilterList;
import org.apache.hadoop.hbase.filter.FilterList.Operator;
import org.apache.hadoop.hbase.filter.SingleColumnValueFilter;
import org.apache.hadoop.hbase.util.Bytes;

import com.google.common.collect.Lists;


/**
 * 하나의 테이블에 여러개의 클래스를 입력하기 위해서 만들어진 클래스이다.
 * JSON으로 입력하는 이유는 Collection이나 기타 복잡한 매핑들을 단순화 하기 위함이다.
 * 주로 사용자 이력을 남기기 위해 사용된다.
 * @author sin
 */
@Deprecated
public class HbaseJsonSerializer<T extends RowKeyAble> implements HbaseSerializer<T>{
	
	/** 기본패밀리 */
    protected static final byte[] FAMILLY_NAME_JSON = Bytes.toBytes("json");
    /** 기본퀄리파이어 - JSON */
    private static final byte[] QUALIFIER_NAME_JSON = Bytes.toBytes("j");
    /** 기본퀄리파이어 - 클래스명 */
    private static final byte[] QUALIFIER_NAME_CLASS_NAME = Bytes.toBytes("cn");
	
	/** 키값과 사용자 정의 컬럼은  */
	private DefaultHbaseJsonSerializer serializer = new DefaultHbaseJsonSerializer();
	
	public static SingleColumnValueFilter byClassName(Class<?> clazz){
		return new SingleColumnValueFilter(FAMILLY_NAME_JSON,QUALIFIER_NAME_CLASS_NAME,CompareOp.EQUAL,Bytes.toBytes(clazz.getName()));
	}
	
	public static FilterList byClassNames(Iterable<Class<?>>  clazzes){
		List<Filter> filters = Lists.newArrayList();
		for(Class<?> each : clazzes) filters.add(HbaseJsonSerializer.byClassName(each));
		return new FilterList(Operator.MUST_PASS_ONE,filters);
	}
    
	/** 이 테이블은 버전관리하지 않는다. */
    public HTableDescriptor createTable(String tableName){
    	HTableDescriptor table = new HTableDescriptor(tableName);
    	HColumnDescriptor columndescriptor = new HColumnDescriptor(FAMILLY_NAME_JSON);
    	columndescriptor.setMaxVersions(1);
		table.addFamily(columndescriptor);
		return table;
	}
    
    public Put toPut(T vo) {
    	Put put  = new Put(vo.getRowKey());
    	String json = serializer.serialize(vo);
    	put.add(FAMILLY_NAME_JSON,QUALIFIER_NAME_JSON,Bytes.toBytes(json));
    	put.add(FAMILLY_NAME_JSON,QUALIFIER_NAME_CLASS_NAME,Bytes.toBytes(vo.getClass().getName()));
    	return put;
    }
    
	@Override
	public T mapRow(Result result, int arg1) throws Exception {
		String json = HbaseUtil.getString(result, FAMILLY_NAME_JSON, QUALIFIER_NAME_JSON);
		String className = HbaseUtil.getString(result, FAMILLY_NAME_JSON, QUALIFIER_NAME_CLASS_NAME);
		T vo = serializer.deserialize(className, json);
		return vo;
	}
	
	// =================================== getter / setter ===================================================
	public void setSerializer(DefaultHbaseJsonSerializer serializer) {
		this.serializer = serializer;
	}


	
	

}
