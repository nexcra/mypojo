package erwins.util.hadoop.hbase;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.KeyValue;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.util.Bytes;
import org.springframework.data.hadoop.hbase.RowMapper;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import erwins.util.lib.ReflectionUtil;

/**
 * 만들고보니 뭔가 복잡하다. 안써..
 * @author sin
 */
@Deprecated
public abstract class GenericVoHbaseDao<T extends RowKeyAble> extends GenericHbaseDao<T>{
	
	private Class<T> persistentClass;
	private Set<Field> body = Sets.newHashSet();
	private Map<Field, Set<Field>> familys = Maps.newHashMap();
	
	private static final byte[] BODY = Bytes.toBytes("body");
	
	@Override
	protected HTableDescriptor getDescriptor() {
		HTableDescriptor table = new HTableDescriptor(getTableName());
		table.addFamily(new HColumnDescriptor(BODY));
		for(Field each : familys.keySet()) table.addFamily(new HColumnDescriptor(each.getName()));
		return table;
	}
	
	public GenericVoHbaseDao() {
        this.persistentClass =  ReflectionUtil.genericClass(getClass(),0);
    	List<Field> fields = ReflectionUtil.getAllDeclaredFields(persistentClass);
    	addPrimitiveFieldSet(body,fields,1);
    }

	/** 2뎁스 까지만 지원한다. */
	private void addPrimitiveFieldSet(Set<Field> fieldSet,List<Field> allFields,int depth) {
		for(Field field : allFields){
    		field.setAccessible(true);
    		if(String.class.isAssignableFrom(field.getType())) fieldSet.add(field);
    		else if(Number.class.isAssignableFrom(field.getType())) fieldSet.add(field);
    		else if(Boolean.class.isAssignableFrom(field.getType())) fieldSet.add(field);
    		else if(Collection.class.isAssignableFrom(field.getType())) continue; //아직 지원안함
    		else{
    			if(depth!=1) continue;
    			List<Field> fields = ReflectionUtil.getAllDeclaredFields(field.getType());
    			Set<Field> currentSet = Sets.newHashSet();
    			addPrimitiveFieldSet(currentSet,fields,depth+1);
    			familys.put(field, currentSet);
    		}
    	}
	}
	
	private RowMapper<T> mapper = new RowMapper<T>(){
		@Override
		public T mapRow(Result result, int index) throws Exception {
			T vo = ReflectionUtil.newInstance(persistentClass);
			resultSet(result, BODY, body, vo);
			for(Entry<Field,Set<Field>> entry : familys.entrySet()){
				Field field =  entry.getKey();
				Object subVo = ReflectionUtil.newInstance(field.getType());
				byte[] name = Bytes.toBytes(field.getName());
				resultSet(result, name, entry.getValue(), subVo);
				ReflectionUtil.setField(field, vo, subVo);
			}
			return vo;
		}
	};

	@Override
	protected String getTableName() {
		return persistentClass.getSimpleName();
	}

	@Override
	protected RowMapper<T> getRowMapper() {
		return mapper;
	}

	@Override
	protected Put toPut(T vo) {
		Put put  = new Put(vo.getRowKey());
		putAdd(put,BODY,body,vo);
		for(Entry<Field,Set<Field>> entry : familys.entrySet()){
			Field field =  entry.getKey();
			Object current = ReflectionUtil.getField(field, vo);
			byte[] name = Bytes.toBytes(field.getName());
			putAdd(put,name,entry.getValue(),current);
		}
		return put;
	}
	
	private static <T> void resultSet(Result result,byte[] family,Iterable<Field> fields,Object vo)  {
		for(Field field : fields){
			KeyValue kv = result.getColumnLatest(family,Bytes.toBytes(field.getName()));
			if(kv==null) continue;
			if(String.class.isAssignableFrom(field.getType())) ReflectionUtil.setField(field, vo,Bytes.toString(kv.getValue()));
			else if(Long.class.isAssignableFrom(field.getType())) ReflectionUtil.setField(field, vo,Bytes.toLong(kv.getValue()));
			else if(Integer.class.isAssignableFrom(field.getType())) ReflectionUtil.setField(field, vo,Bytes.toInt(kv.getValue()));
			else if(BigDecimal.class.isAssignableFrom(field.getType())) ReflectionUtil.setField(field, vo,Bytes.toBigDecimal(kv.getValue()));
			else if(Boolean.class.isAssignableFrom(field.getType())) ReflectionUtil.setField(field, vo,Bytes.toBoolean(kv.getValue()));
		}
		
		
	}

	private static <T> void putAdd(Put put,byte[] family,Iterable<Field> fields,Object vo)  {
		for(Field field : fields){
			Object value = ReflectionUtil.getField(field, vo);
			if(value==null) continue;
			if(String.class.isAssignableFrom(value.getClass())) put.add(family,Bytes.toBytes(field.getName()),Bytes.toBytes((String)value));
			else if(Long.class.isAssignableFrom(value.getClass())) put.add(family,Bytes.toBytes(field.getName()),Bytes.toBytes((Long)value));
			else if(Integer.class.isAssignableFrom(value.getClass())) put.add(family,Bytes.toBytes(field.getName()),Bytes.toBytes((Integer)value));
			else if(BigDecimal.class.isAssignableFrom(value.getClass())) put.add(family,Bytes.toBytes(field.getName()),Bytes.toBytes((BigDecimal)value));
			else if(Boolean.class.isAssignableFrom(value.getClass())) put.add(family,Bytes.toBytes(field.getName()),Bytes.toBytes((Boolean)value));
		}
	}


}
