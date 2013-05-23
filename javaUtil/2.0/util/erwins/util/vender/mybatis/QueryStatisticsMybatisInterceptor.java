package erwins.util.vender.mybatis;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.apache.ibatis.executor.statement.BaseStatementHandler;
import org.apache.ibatis.executor.statement.PreparedStatementHandler;
import org.apache.ibatis.executor.statement.RoutingStatementHandler;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.session.ResultHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * 쿼리 통계를 내기 위한 인터셉터.
 * Mybatis는 Hibernate처럼 자동으로 통계생성을 해주지 않는다.
 * 향후 실행대비 시간이 오래걸리는 쿼리를 찾아서 인덱싱을 하면 된다.
 * 덤으로 SQL예외 발생시 로그도 찍어준다
	<plugins>
        <plugin interceptor="erwins.util.vender.mybatis.QueryStatisticsMybatisInterceptor"/>
    </plugins>
 * @author sin
 */
@Intercepts({@Signature(type=StatementHandler.class, method="query", args={Statement.class,ResultHandler.class})})
public class QueryStatisticsMybatisInterceptor implements Interceptor{
    
	private final Field mappedStatement;
    private final Field delegate;
	
    /** 사실 id만 있으면 SQL을 얼마든지 가지고 올 수 있다 */
    public static class QueryState implements Comparable<QueryState>{
        private long queryCount = 0;
        private Long totalTime = 0L;
        public final String id;
        public final String sql;
        public QueryState(String id,String sql){
            this.id = id;
            this.sql = sql;
        }
        public void addQuery(long time){
            queryCount++;
            totalTime += time;
        }
        public long getQueryCount() {
            return queryCount;
        }
        public long getTotalTime() {
            return totalTime;
        }
        @Override
        public int compareTo(QueryState o) {
            return this.id.compareTo(o.id);
        }
    }
    
    /** ToStringStyle을 입력해 준다 */
    public static String toString(Object obj,ToStringStyle stype) {
        if(obj==null) return "";
        return ToStringBuilder.reflectionToString(obj, stype);  
    }
    
    /** 어쩔 수 없이 static으로 만듬 */
    public static final Map<String,QueryState> MAP = new ConcurrentHashMap<String,QueryState>();
    private Logger log = LoggerFactory.getLogger(this.getClass());
    
    
    public QueryStatisticsMybatisInterceptor(){
    	try {
			delegate = RoutingStatementHandler.class.getDeclaredField("delegate");
			delegate.setAccessible(true);
			mappedStatement = BaseStatementHandler.class.getDeclaredField("mappedStatement");
	    	mappedStatement.setAccessible(true);
		} catch (SecurityException e) {
			throw new RuntimeException(e);
		} catch (NoSuchFieldException e) {
			throw new RuntimeException(e);
		}
    }
    
    @Override
    public Object intercept(Invocation invocation) throws Throwable {
    	RoutingStatementHandler handler = (RoutingStatementHandler)invocation.getTarget();
    	MappedStatement ms = getMappedStatement(handler);
    	BoundSql boundSql = handler.getBoundSql();
        String sql = boundSql.getSql();
        long startTime = System.nanoTime();
        Object result;
        try {
            result = invocation.proceed();
        } catch (InvocationTargetException e) {
            boolean isLock = isLockSql(e);
            if(!isLock) log.error(sql); //향후 수정
            throw e;
        } catch (Exception e) {
            log.error(sql); //향후 수정
            log.error(toString(handler.getBoundSql().getParameterObject(), ToStringStyle.SIMPLE_STYLE));
            throw e;
        }
        long endTime = System.nanoTime();
        
        synchronized (this) {
            QueryState state = MAP.get(ms.getId());
            if(state==null){
                state = new QueryState(ms.getId(),sql);
                MAP.put(ms.getId(), state);
            }
            state.addQuery(endTime - startTime);
        }
        return result;
    }

    /** 리플렉션으로 감춰진 MappedStatement를 꺼내온다 */
	private MappedStatement getMappedStatement(RoutingStatementHandler handler) throws IllegalAccessException {
		PreparedStatementHandler obj = (PreparedStatementHandler) delegate.get(handler);
    	MappedStatement ms = (MappedStatement) mappedStatement.get(obj);
		return ms;
	}

    /** 이는 로직에 사용됨으로 로그 무시. */
    private boolean isLockSql(InvocationTargetException e) {
        Throwable cause = e.getCause();
        if(cause instanceof SQLException){
            SQLException ex = (SQLException)cause;
            if(isLockSqlException(ex)) return true;
        }
        return false;
    }

    /** 현재 ORACLE의 에러코드를 보고 판단.  향후 확장할것 */
	protected boolean isLockSqlException(SQLException ex) {
		if(ex.getSQLState().equals("61000")) return true;
		return false;
	}
    
    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }
    
    @Override
    public void setProperties(Properties properties) {
        //무시
    }

}
