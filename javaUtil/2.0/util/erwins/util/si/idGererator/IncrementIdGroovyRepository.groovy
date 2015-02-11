package erwins.util.si.idGererator

import groovy.sql.Sql
import lombok.Data


/** 그루비 SM용 */
@Data
public class IncrementIdGroovyRepository implements IncrementIdRepository{

	private final Sql sql;
	/** ex) "select TOTAL_SEQ.nextval SEQ from dual" */
	private final String selectSql;
	
	@Override
	public long nextval() {
		return sql.rows(selectSql)[0][0];
	}

}

