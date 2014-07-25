package erwins.util.hadoop.hbase;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.util.Bytes;

import com.google.common.collect.Maps;

import erwins.util.lib.ReflectionUtil;

/**
 * 한개 테이블에 다양한 VO를 입력할때 사용된다.
 * @author sin
 */
@Deprecated
public class HbaseVoSerializer<T extends RowKeyAble> implements HbaseSerializer<T>{
	
	/** 기본패밀리 */
	public static final byte[] FAMILLY_NAME_VO = Bytes.toBytes("vo");
    
    private final Class<?> persistentClass;
    private final Map<byte[], Field> fieldMap = Maps.newHashMap();
    
	public HbaseVoSerializer(Class<?> persistentClass) {
        this.persistentClass =  persistentClass;
    	List<Field> fields = ReflectionUtil.getAllDeclaredFields(persistentClass);
    	for(Field field : fields){
    		field.setAccessible(true);
    		Class<?> type = field.getType();
    		if(HbaseUtil.isAbleType(type)){
    			String name = field.getName();
        		byte[] nameByte = Bytes.toBytes(name);
        		fieldMap.put(nameByte, field);
    		}
    	}
    }

	@Override
	public T mapRow(Result result, int index) throws Exception {
		@SuppressWarnings("unchecked")
		T vo = (T) ReflectionUtil.newInstance(persistentClass);
		for(Entry<byte[],Field> entry : fieldMap.entrySet()){
			byte[] qualifier = entry.getKey();
			Field field =  entry.getValue();
			byte[] byteValue = result.getValue(FAMILLY_NAME_VO, qualifier);
			if(byteValue==null) continue;
			Object value = HbaseUtil.toValue(field.getType(), byteValue);
			ReflectionUtil.setField(field, vo, value);
		}
		return vo;
	}
	
	@Override
	public HTableDescriptor createTable(String tableName) {
		HTableDescriptor table = new HTableDescriptor(tableName);
		table.addFamily(new HColumnDescriptor(FAMILLY_NAME_VO));
		return table;
	}

	@Override
	public Put toPut(T vo) {
		Put put  = new Put(vo.getRowKey());
		for(Entry<byte[],Field> entry : fieldMap.entrySet()){
			byte[] qualifier = entry.getKey();
			Field field =  entry.getValue();
			Object obj = ReflectionUtil.getField(field,vo);
			if(obj==null) continue;
			byte[] value =  HbaseUtil.toBytes(obj);
			put.add(FAMILLY_NAME_VO, qualifier, value);
		}
		return put;
	}



}
