package erwins.util.hadoop.hbase;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Date;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.KeyValue;
import org.apache.hadoop.hbase.client.HBaseAdmin;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.util.Bytes;

import erwins.util.dateTime.JodaUtil;
import erwins.util.lib.FileUtil;
import erwins.util.lib.ReflectionUtil;
import erwins.util.lib.security.MD5s;
import erwins.util.root.exception.IORuntimeException;

/**
 * @author sin
 */
public abstract class HbaseUtil{
	
	public static final byte[] MD5_MIN = fixedValueByte(Bytes.ESTIMATED_HEAP_TAX,0);
	public static final byte[] MD5_MAX = fixedValueByte(Bytes.ESTIMATED_HEAP_TAX,1);
	
	/** 일단 쓸모없음. 타임스탬프를 이런식으로 레인지 조회하면 안됨.  */
	public static final byte[] LONG_MIN = fixedValueByte(Bytes.SIZEOF_LONG,0);
	/** 일단 쓸모없음. 타임스탬프를 이런식으로 레인지 조회하면 안됨.  */
	public static final byte[] LONG_MAX = fixedValueByte(Bytes.SIZEOF_LONG,1);
	
	public static final byte[] TIMESTAMP_START = Bytes.toBytes(JodaUtil.Y.parseDateTime("2999").getMillis()*-1);
	/** -1 때문인가.. 암튼 1975 정도로 해서 앞자리 -가 달라짐. 따라서 이렇게 해야함 */
	public static final byte[] TIMESTAMP_STOP = Bytes.toBytes(JodaUtil.Y.parseDateTime("1975").getMillis()*-1);
	
	/** 고정길이 바이트 생성 */
	public static byte[] fixedValueByte(int arraySize,int arrayValue) {
		byte[] value = new byte[arraySize];
		for(int i=0;i<arraySize;i++) value[i] = (byte) arrayValue;
		return value;
	}
	
	/** 로우키에 타임스탬프를 어펜드한다. */
	public static Scan timestampPadedScan(byte[] indexKey){
		Scan scan = new Scan();
		scan.setStartRow(HbaseUtil.buildRowKey(indexKey,HbaseUtil.TIMESTAMP_START));
		scan.setStopRow(HbaseUtil.buildRowKey(indexKey,HbaseUtil.TIMESTAMP_STOP));
		return scan;
	}
	
	/** long과 String 타입만 된다. 
	 * 문자열은 크기가 얼마든 16으로 해시된다. --> 적절한 분산과 키 사이즈 통일을 위해 사용
	 * long은 시계열 조회를 위해 사용
	 * 역순 정렬(최근 자료가 상단에 배치. 스캔시 start 시점부터 과거 자료를 검색)할거면 마지막 인자(long)에 -1을 곱해서 전달 */
	public static byte[] buildRowKey(int rowKeySize,Iterable<Object> pks) {
		byte[] rowKey = new byte[rowKeySize];
		int offset = 0;
		for(Object pk : pks){
			if(pk instanceof String){
				byte[] key = MD5s.getHash(Bytes.toBytes((String)pk)); //해시한다.
				offset = Bytes.putBytes(rowKey, offset, key, 0, key.length);
			}else if(pk instanceof byte[]){
				byte[] key = (byte[])pk;
				offset = Bytes.putBytes(rowKey, offset, key, 0, key.length);
			}else if(pk instanceof Long){
				byte[] key = Bytes.toBytes((Long)pk);
				offset = Bytes.putBytes(rowKey, offset, key, 0, key.length);
			} else throw new IllegalArgumentException("기본형 타입만 지원됩니다. : " + pk.getClass());
		}
		return rowKey;
	}
	
	public static byte[] hash(String str) {
		return MD5s.getHash(Bytes.toBytes(str));
	}
	
	public static byte[] buildRowKey(int rowKeySize,byte[] ... datas) {
		byte[] rowKey = new byte[rowKeySize];
		int offset = 0;
		for(byte[] data : datas) {
			offset = Bytes.putBytes(rowKey, offset, data, 0, data.length);
		}
		return rowKey;
	}
	
	public static byte[] buildRowKey(byte[] ... datas) {
		int rowKeySize = 0;
		for(byte[] data : datas) rowKeySize += data.length;
		return buildRowKey(rowKeySize,datas);
	}
	
	/** Bytes.toBytes 가 불편해서 하나 만듬  */
	public static byte[] toBytes(Object obj) {
		if(obj instanceof String ) return Bytes.toBytes((String)obj);
		else if(obj instanceof Long ) return Bytes.toBytes((Long)obj);
		else if(obj instanceof Integer ) return Bytes.toBytes((Integer)obj);
		else if(obj instanceof Boolean ) return Bytes.toBytes((Boolean)obj);
		else if(obj instanceof BigDecimal ) return Bytes.toBytes((BigDecimal)obj);
		else if(obj instanceof Date ) return Bytes.toBytes(((Date)obj).getTime());
		else throw new IllegalArgumentException("기본형 타입만 지원됩니다. : " + obj.getClass());
	}
	
	/** 미리 정의된 몇가지만 간단하게 가능 */
	public static boolean isAbleType(Class<?> type) {
		return ReflectionUtil.isAssignableFrom(type, String.class,Boolean.class,Long.class,Integer.class,BigDecimal.class,Date.class);
	}
	
	/** Bytes.toXX 가 불편해서 하나 만듬  */
	public static Object toValue(Class<?> type,byte[] data) {
		Object value = null;
		if(type.isAssignableFrom(String.class)) value = Bytes.toString(data);
		else if(type.isAssignableFrom(Long.class)) value = Bytes.toLong(data);
		else if(type.isAssignableFrom(Integer.class)) value = Bytes.toInt(data);
		else if(type.isAssignableFrom(Boolean.class)) value = Bytes.toBoolean(data);
		else if(type.isAssignableFrom(BigDecimal.class)) value = Bytes.toBigDecimal(data);
		else if(type.isAssignableFrom(Date.class)) value = new Date(Bytes.toLong(data)); 
		else throw new IllegalArgumentException("기본형 타입만 지원됩니다. : " + type);
		return value;
	}
	
	public static String getString(Result result,byte[] familly,byte[] column) {
		KeyValue kv = result.getColumnLatest(familly,column);
		if(kv==null){
			return "";
		}
		return  Bytes.toString(kv.getValue());	
	}
	
	public static boolean createTable(Configuration config,HTableDescriptor table) {
		HBaseAdmin hBaseAdmin = null;
		try {
			hBaseAdmin = new HBaseAdmin(config);
			if (hBaseAdmin.isTableAvailable(table.getName())) return false; //이미 활성화된 테이블
			hBaseAdmin.createTable(table);
			return true;
		} catch (IOException e) {
			throw new IORuntimeException(e);
		}finally{
			FileUtil.closeQuietly(hBaseAdmin);
		}
	}
	
    public static boolean dropTable(Configuration config,String tableName){
		HBaseAdmin hBaseAdmin = null;
		try {
			hBaseAdmin = new HBaseAdmin(config);
			if (!hBaseAdmin.isTableAvailable(tableName)) return false;
			hBaseAdmin.disableTable(tableName);
			hBaseAdmin.deleteTable(tableName);
			return true;
		} catch (IOException e) {
			throw new IORuntimeException(e);
		}finally{
			FileUtil.closeQuietly(hBaseAdmin);
		}
	}
    
}
