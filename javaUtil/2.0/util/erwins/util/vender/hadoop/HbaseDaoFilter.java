package erwins.util.vender.hadoop;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.springframework.web.filter.GenericFilterBean;

/**
 * 필터 위치가 적절해야 한다. 
 * 남은 Hbase 데이터를 입력하거나 롤백한다.
 * DB트랜잭션에 이미 걸려있을경우 작동하지 않는다.
 * @author sin
 */
public class HbaseDaoFilter extends GenericFilterBean{

	@Override
	public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws IOException, ServletException {
		
		try {
			chain.doFilter(req, resp);
			HbaseDao.afterCompletionCommit();
		} catch (ServletException e) {
			HbaseDao.afterCompletionRollback();
			throw e;
		} catch (IOException e) {
			HbaseDao.afterCompletionRollback();
			throw e;			
		}finally{
			HbaseDao.remove();
		}
		
	}

}
