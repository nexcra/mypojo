package erwins.util.hadoop.hbase;

import java.util.List;

import org.apache.hadoop.hbase.filter.CompareFilter.CompareOp;
import org.apache.hadoop.hbase.filter.Filter;
import org.apache.hadoop.hbase.filter.FilterList;
import org.apache.hadoop.hbase.filter.FilterList.Operator;
import org.apache.hadoop.hbase.filter.SingleColumnValueFilter;
import org.apache.hadoop.hbase.util.Bytes;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;


/**
 * 컬럼의 value 기준(SingleColumnValueFilter)으로 판단한다.
 * @author sin
 */
public class HbaseFilter{
	
	private List<Filter> rowFilters = Lists.newArrayList();
	
	private byte[] familly;
	
	public static HbaseFilter create(byte[] familly){
		HbaseFilter filter = new HbaseFilter();
		filter.familly = familly;
		return filter;
	}
	
	public HbaseFilter and(byte[] qualifier,byte[] value){
		rowFilters.add(new SingleColumnValueFilter(familly, qualifier,CompareOp.EQUAL,value));
		return this;
	}
	
	/** values 들중 하나만 매칭되도 된다. */
	public HbaseFilter andOr(byte[] qualifier,byte[] ... values){
		SingleColumnValueFilter[] filters = new SingleColumnValueFilter[values.length];
		for(int i=0;i<values.length;i++) filters[i] = new SingleColumnValueFilter(familly, qualifier,CompareOp.EQUAL,values[i]);
		rowFilters.add(new FilterList(Operator.MUST_PASS_ONE, filters));
		return this;
	}
	
	/** 타입을 구분하기 힘들때 사용한다.
	 * String과  Long만 일단 지원한다.  */
	public HbaseFilter andInputString(byte[] qualifier,String param){
		if(Strings.isNullOrEmpty(param)) return this;
		byte[] value = Bytes.toBytes(param);
		rowFilters.add(new SingleColumnValueFilter(familly, qualifier,CompareOp.EQUAL,value));
		return this;
	}
	
	/** 다수의 필터가 세팅되었을경우 전체가 다 일치(AND조건)해야 한다. */
	public Filter getFilter(){
		if(rowFilters.size() > 1) return new FilterList(Operator.MUST_PASS_ALL, rowFilters);
		else if(rowFilters.size() == 1) return rowFilters.get(0);
		else return null;
	}
	
	
}
