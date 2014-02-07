package erwins.util.spring.batch.component;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.poi.ss.formula.functions.T;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SingleColumnRowMapper;


public class ToStringRowMapper implements RowMapper<String>,Serializable{

	@Override
	public String mapRow(ResultSet arg0, int arg1) throws SQLException {
		// TODO Auto-generated method stub
		SingleColumnRowMapper asd;
		return null;
	}

    

}
