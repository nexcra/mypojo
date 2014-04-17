package erwins.util.spring.batch.component;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SingleColumnRowMapper;

/** ?????? */
@Deprecated
public class ToStringRowMapper implements RowMapper<String>,Serializable{

	@Override
	public String mapRow(ResultSet arg0, int arg1) throws SQLException {
		// TODO Auto-generated method stub
		SingleColumnRowMapper asd;
		return null;
	}

    

}
