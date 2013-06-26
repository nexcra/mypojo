package erwins.util.hadoop.hbase;

import org.apache.hadoop.hbase.client.Result;



/**
 * Hbase의 RowKey를 설계할때 사용된다.
 * @author sin
 */
public interface RowKeyAble{
	
	public byte[] getRowKey() ;
	
	
	/** 마이그레이션 등에 사용하자 */
	public static class HbaseSerializeException extends RuntimeException{
		
		private static final long serialVersionUID = 1L;
		
		public final Result result;
		public final int index;
		private Throwable exception;
		private String msg;
		
		public HbaseSerializeException(Result result, int index) {
			this.result = result;
			this.index = index;
		}
		
		public Throwable getException() {
			return exception;
		}

		public HbaseSerializeException setException(Throwable exception) {
			this.exception = exception;
			return this;
		}

		public String getMsg() {
			return msg;
		}

		public HbaseSerializeException setMsg(String msg) {
			this.msg = msg;
			return this;
		}
		
		
	}

}
