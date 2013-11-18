package erwins.util.vender.mybatis;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import lombok.Data;

import org.apache.ibatis.executor.statement.BaseStatementHandler;
import org.apache.ibatis.executor.statement.PreparedStatementHandler;
import org.apache.ibatis.executor.statement.RoutingStatementHandler;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.session.ResultHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;

import erwins.util.dateTime.TimeString;
import erwins.util.lib.ReflectionUtil.Fields;
import erwins.util.spring.SpringUtil;
import erwins.util.tools.StringAppender;


/**
 * 쿼리 통계를 내기 위한 인터셉터.
 * Mybatis는 Hibernate처럼 자동으로 통계생성을 해주지 않는다.
 * 향후 실행대비 시간이 오래걸리는 쿼리를 찾아서 인덱싱을 하면 된다.
 * 덤으로 SQL예외 발생시 로그도 찍어준다
	<plugins>
        <plugin interceptor="erwins.util.vender.mybatis.QueryStatisticsMybatisInterceptor"/>
    </plugins>
    이하 설정은 http://mybatis.github.io/mybatis-3/ko/configuration.html#plugins / F4(구현체보기) 참조.
    Executor 로 설정하면 MappedStatement를 인자로 받기 때문에 간단히 알 수 있다. 하지만 이미 이렇게 했으니 수정하지 않고 쓴다.
 * @author sin
 */
@Intercepts({
    @Signature(type=StatementHandler.class, method="update", args={Statement.class})
    , @Signature(type=StatementHandler.class, method="query", args={Statement.class, ResultHandler.class})
})
public class QueryStatisticsMybatisInterceptor implements Interceptor{
    

	/** 어쩔 수 없이 static으로 만듬 */
    public static final Map<String,QueryState> MAP = new ConcurrentHashMap<String,QueryState>();
    private Logger log = LoggerFactory.getLogger(this.getClass());
    
    @Data
    public static class QueryState implements Comparable<QueryState>{
        private long queryCount = 0;
        private Long totalTime = 0L;
        private final String sqlId;
        private final String sql;
        public QueryState(String sqlId,String sql){
            this.sqlId = sqlId;
            this.sql = sql;
        }
        public void addQuery(long time){
            queryCount++;
            totalTime += time;
        }
        @Override
        public int compareTo(QueryState o) {
            return this.totalTime.compareTo(o.totalTime);
        }
    }
    
    /** 이 둘은 논란의 여지가 있다. 버전이 바뀌면 수정해주자. 현재. mybatis 3.2.3  */
    private static final Fields F1 = new Fields(RoutingStatementHandler.class,"delegate");
    private static final Fields F2 = new Fields(BaseStatementHandler.class,"mappedStatement");
    
    /** InvocationTargetException는 로직에서 잡아서 처리하는 경우가 있음으로 그냥 던져준다. */
    @Override
    public Object intercept(Invocation invocation) throws Throwable {
    	invocation.getArgs();
        StatementHandler handler = (StatementHandler)invocation.getTarget();
        BoundSql boundSql = handler.getBoundSql();
        String mappedSql = boundSql.getSql();
        
        //mybatis 소소를 깊게 보기 싫어서 그냥 쓴다.
        PreparedStatementHandler delegate = (PreparedStatementHandler) F1.get(handler);
    	MappedStatement  mappedStatement = (MappedStatement) F2.get(delegate);
    	String sqlId = mappedStatement.getId();
        
        if(log.isDebugEnabled()){
        	mappedSql = replaceFirstSql(handler);
        	log.debug(toPrettySql(sqlId, mappedSql));
        }
        
        long startTime = System.currentTimeMillis();
        long endTime;
        Object result;
        try {
            result = invocation.proceed();
            endTime = System.currentTimeMillis();
            if(log.isDebugEnabled()){
            	if(result instanceof List){
            		List<?> rows = (List<?>) result;
            		log.debug("RESULT ROW : {} , 걸린시간 : {}",rows.size(),new TimeString(endTime - startTime));
            	}
            }
        } catch (InvocationTargetException e) {
            boolean isLock = isLockSql(e);
            if(!isLock) log.error(toPrettySql(sqlId, mappedSql)); //향후 수정
            throw e;
        } catch (Exception e) {
            log.error(toPrettySql(sqlId, mappedSql)); //향후 수정
            throw e;
        }
        
        synchronized (this) {
        	String sql = boundSql.getSql();
            QueryState state = MAP.get(sql);
            if(state==null){
                state = new QueryState(sqlId,sql);
                MAP.put(sql, state);
            }
            state.addQuery(endTime - startTime);
        }
        return result;
    }


	private String toPrettySql(String sqlId, String mappedSql) {
		Iterable<String> sqlList = Splitter.on('\n').trimResults().omitEmptyStrings().split(mappedSql);
		String sqlLog = Joiner.on('\n').join(sqlList);
		StringAppender b = new StringAppender();
		b.appendLine("");
		b.appendLine("================================================== SQL ID : " + sqlId + " =============================================================");
		b.appendLine(sqlLog);
		b.append("====================================================================================================================================================");
		return b.toString();
	}


    /** SQL에 파라메터를 매핑한다. 
     * 이 SQL은 복사-붙여넣기로 바로 실행 가능해야 한다.
     * 물론 매퍼에 따라 매핑이 상이함으로 기본 설정으로만 변경된다 */
    @SuppressWarnings("rawtypes")
	protected String replaceFirstSql(StatementHandler handler) throws NoSuchFieldException, IllegalAccessException {
    	BoundSql boundSql = handler.getBoundSql();
    	String sql = boundSql.getSql();
    	Object param = handler.getParameterHandler().getParameterObject();
    	if(param == null) return sql.replaceFirst("\\?", "''");
    	if(param instanceof Number) return sql.replaceFirst("\\?", param.toString());
		if(param instanceof String) return sql.replaceFirst("\\?", "'" + param + "'"); 
		
		List<ParameterMapping> paramMapping = boundSql.getParameterMappings();	
		if(param instanceof Map){
			for(ParameterMapping mapping : paramMapping){
				String propValue = mapping.getProperty();
				Object value = ((Map) param).get(propValue);
				if(value==null) sql = sql.replaceFirst("\\?", "''");
				if(param instanceof Number) sql = sql.replaceFirst("\\?", value.toString());
				sql = sql.replaceFirst("\\?", "'" + value + "'");
			}
		}else{
			ExpressionParser parser = new SpelExpressionParser();
			for(ParameterMapping mapping : paramMapping){
				String propValue = mapping.getProperty();
				Object value = SpringUtil.elValue(parser, propValue, param);
				if(value==null) sql = sql.replaceFirst("\\?", "''");
				if(param instanceof Number) sql = sql.replaceFirst("\\?", value.toString());
				sql = sql.replaceFirst("\\?", "'" + value + "'");
			}
		}
    	
    	return sql;
    }

    /** 이는 로직에 사용됨으로 로그 무시 */
    private boolean isLockSql(InvocationTargetException e) {
        Throwable cause = e.getCause();
        if(cause instanceof SQLException){
            SQLException ex = (SQLException)cause;
            if(ex.getSQLState().equals("61000")) return true;
        }
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
