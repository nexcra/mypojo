package erwins.util.hadoop.hbase;

import java.util.List;
import java.util.Map;

import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.util.Bytes;

import com.google.common.collect.Maps;

import erwins.util.hadoop.hbase.RowKeyAble.HbaseSerializeException;
import erwins.util.lib.ReflectionUtil;

/**
 * 한개 테이블에 다양한 VO를 입력할때 사용된다.
 * @author sin
 */
public class HbaseVoMapSerializer<T extends RowKeyAble> implements HbaseSerializer<T>{
	
	/** 기본패밀리 */
	public static final byte[] FAMILLY_NAME_VO = Bytes.toBytes("vo");
	
	/** 기본퀄리파이어 - 클래스명 */
	public static final byte[] QUALIFIER_NAME_CLASS_NAME = Bytes.toBytes("cn");
	
    private final Map<Class<?>, HbaseVoSerializer<T>> serialMap = Maps.newHashMap();
    
    public HbaseVoMapSerializer(){};
    
	public HbaseVoMapSerializer(List<Class<?>> persistentClasses) {
        for(Class<?> each : persistentClasses){
        	serialMap.put(each, new HbaseVoSerializer<T>(each));
        }
    }

	@Override
	public T mapRow(Result result, int index) throws Exception,HbaseSerializeException {
		try {
			byte[] qName = result.getValue(FAMILLY_NAME_VO, QUALIFIER_NAME_CLASS_NAME);
			if(qName==null) throw new HbaseSerializeException(result,index).setMsg("HBase에 클래스명이 존재하지 않습니다.");
			Class<?> type =  ReflectionUtil.forName(Bytes.toString(qName));
			HbaseVoSerializer<T> serializer = serialMap.get(type); 
			return serializer.mapRow(result, index);
		} catch (Exception e) {
			throw new HbaseSerializeException(result,index).setException(e);
		}
	}
	
	@Override
	public HTableDescriptor createTable(String tableName) {
		HTableDescriptor table = new HTableDescriptor(tableName);
		table.addFamily(new HColumnDescriptor(FAMILLY_NAME_VO));
		return table;
	}

	@Override
	public Put toPut(T vo) {
		Class<?> type =  vo.getClass();
		HbaseVoSerializer<T> serializer = serialMap.get(type);
		Put put  = serializer.toPut(vo);
		byte[] qName = Bytes.toBytes(type.getName());
		put.add(FAMILLY_NAME_VO, QUALIFIER_NAME_CLASS_NAME, qName);
		return put;
	}



}
