package erwins.util.root.exception;

import java.sql.SQLException;


/**
 * SQLException을 래핑하는 런타임 예외
 * 필요할때만 catch해서 사용학기 위해 만들었다. 
 * LIB 류에서는 SQLException예외를 이걸로 래핑해서 던지자.
 */
@SuppressWarnings("serial")
public class SQLRuntimeException extends RuntimeException{

	public SQLRuntimeException(String message, SQLException cause) {
		super(message, cause);
	}
	
	public SQLRuntimeException(SQLException cause) {
		super(cause);
	}
	
	@Override
	public synchronized SQLException getCause() {
		return (SQLException) super.getCause();
	}

	
	
}