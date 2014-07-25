package erwins.util.jdbc;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

/**
 * 그루비 등을 사용해서 JdbcUtil의 업데이트 기능을 간단히 사용할때 쓴다.
 *  
 * */
public class ObjectArrayRowMapper implements RowMapper<Object[]> {
	
	/** 없는거 추가하자 */
	@Override
	public Object[] mapRow(ResultSet rs, int arg1) throws SQLException {
		ResultSetMetaData meta =  rs.getMetaData();
		Object[] array = new Object[meta.getColumnCount()];
		for(int columnIndex=1;columnIndex<meta.getColumnCount()+1;columnIndex++){
			array[columnIndex-1] = rs.getObject(columnIndex); 
		}
		return array;
	}
    

}