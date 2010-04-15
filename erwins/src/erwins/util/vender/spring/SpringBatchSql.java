package erwins.util.vender.spring;

import javax.sql.DataSource;

import org.springframework.batch.item.database.PagingQueryProvider;
import org.springframework.batch.item.database.support.AbstractSqlPagingQueryProvider;
import org.springframework.batch.item.database.support.Db2PagingQueryProvider;
import org.springframework.batch.item.database.support.MySqlPagingQueryProvider;
import org.springframework.batch.item.database.support.OraclePagingQueryProvider;
import org.springframework.batch.item.database.support.PostgresPagingQueryProvider;
import org.springframework.batch.item.database.support.SqlServerPagingQueryProvider;
import org.springframework.batch.item.database.support.SybasePagingQueryProvider;

/**
 * 스프링 배치가 필요하다.! 각종 DB의 페이징 쿼리를 생산한다. 
 * 오라클은 order by하면 당연히 작동 안한다 ㅋㅋ 주의!
 * 이 클래스는 단순 참조용, 하드코딩 방지용이다.. 근데 완전 쓸모없음.
 **/
public enum SpringBatchSql implements PagingQueryProvider{
	
	ORACLE(new OraclePagingQueryProvider()),
	MS_SQL(new SqlServerPagingQueryProvider()),
	MY_SQL(new MySqlPagingQueryProvider()),
	POSTGRESS(new PostgresPagingQueryProvider()),
	DB2(new Db2PagingQueryProvider()),
	SYBASE(new SybasePagingQueryProvider()),
	;
	
	/** public이다!! ㅋ  */
	public AbstractSqlPagingQueryProvider provider;
	
    private SpringBatchSql(AbstractSqlPagingQueryProvider provider){
    	this.provider = provider;
    }

	@Override
	public String generateFirstPageQuery(int arg0) {
		return provider.generateFirstPageQuery(arg0);
	}

	@Override
	public String generateJumpToItemQuery(int arg0, int arg1) {
		return provider.generateJumpToItemQuery(arg0, arg1);
	}

	@Override
	public String generateRemainingPagesQuery(int arg0) {
		return provider.generateRemainingPagesQuery(arg0);
	}

	@Override
	public int getParameterCount() {
		return provider.getParameterCount();
	}

	@Override
	public void init(DataSource arg0) throws Exception {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isUsingNamedParameters() {
		throw new UnsupportedOperationException();
	}
}
