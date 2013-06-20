package erwins.util.vender.hadoop;

import erwins.util.vender.hadoop.RowKeyAble;



/** 단순 키 기반의 인덱스. 범위 검색은 지원하지 않는다. */
public class HbaseIndex implements RowKeyAble{

	public HbaseIndex() {};
	public HbaseIndex(byte[] rowKey) {
		this.rowKey = rowKey;
	}
	private byte[] rowKey;
	
	private byte[] tableRowKey;

	@Override
	public byte[] getRowKey() {
		return rowKey;
	}
	public byte[] getTableRowKey() {
		return tableRowKey;
	}
	public void setTableRowKey(byte[] tableRowKey) {
		this.tableRowKey = tableRowKey;
	}
	

}
