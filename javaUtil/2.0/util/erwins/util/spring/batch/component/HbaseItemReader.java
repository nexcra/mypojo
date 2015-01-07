package erwins.util.spring.batch.component;


import java.io.IOException;

import javax.annotation.concurrent.ThreadSafe;

import lombok.Data;

import org.apache.hadoop.hbase.client.HTableInterface;
import org.apache.hadoop.hbase.client.HTableInterfaceFactory;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.util.Bytes;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemStream;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.beans.factory.InitializingBean;

import com.google.common.base.Preconditions;

import erwins.util.hadoop.hbase.HbaseSerializer;
import erwins.util.hadoop.hbase.RowKeyAble;

/** 
 * HBase용 리더. 
 * Hadoop으로 처리하기 귀찮은 애들 처리용
 * */
@ThreadSafe
@Data
public class HbaseItemReader<T extends RowKeyAble> implements ItemReader<T>, ItemStream,InitializingBean{
	
	public static final String READ_COUNT = "read.count";
	
	private HTableInterfaceFactory factory;
	private String tableName;
	private Scan scan;
	private HbaseSerializer<T> hbaseSerializer;
	private Integer maxItemCount;
	
	@Override
	public void afterPropertiesSet(){
		Preconditions.checkNotNull(factory, "factory is required");
		Preconditions.checkNotNull(tableName, "tableName is required");
		Preconditions.checkNotNull(scan, "scan is required");
		Preconditions.checkNotNull(hbaseSerializer, "hbaseSerializer is required");
	}
	
	private HTableInterface table;
	private ResultScanner scanner;
	int lineCount = 0;
	
	
	@Override
	public void close() throws ItemStreamException {
		scanner.close();
		try {
			factory.releaseHTableInterface(table);
		} catch (IOException e) {
			throw new ItemStreamException(e);
		}
	}

	@Override
	public void open(ExecutionContext arg0) throws ItemStreamException {
		table =  factory.createHTableInterface(null,Bytes.toBytes(tableName));
		lineCount = 0;
		scan.setMaxVersions(1); //1개만 읽는다.
		try {
			scanner = table.getScanner(scan);
		} catch (IOException e) {
			throw new ItemStreamException(e);
		}
	}

	@Override
	public void update(ExecutionContext ec) throws ItemStreamException {
		ec.putInt(READ_COUNT, lineCount);
	}

	/** synchronized 성능보장못함 알아서 쓰기 */
	@Override
	public synchronized T read() throws Exception {
		Result result = doRead();
		if(result==null) return null;
		T vo = hbaseSerializer.mapRow(result, lineCount);
		return vo;
	}

	private Result doRead() throws IOException {
		if(maxItemCount!=null && lineCount >= maxItemCount) return null;
		Result result = scanner.next();
		if(result==null) return null;
		lineCount++;
		return result;
	}
	

}