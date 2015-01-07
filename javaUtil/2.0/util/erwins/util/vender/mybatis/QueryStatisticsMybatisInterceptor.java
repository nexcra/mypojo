package erwins.util.vender.mybatis;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.MessageFormat;
import java.util.Date;
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
import org.springframework.expression.spel.SpelEvaluationException;
import org.springframework.expression.spel.standard.SpelExpressionParser;

import com.google.common.base.CharMatcher;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.primitives.Ints;

import erwins.util.dateTime.JodaUtil;
import erwins.util.dateTime.TimeString;
import erwins.util.lib.ReflectionUtil;
import erwins.util.lib.ReflectionUtil.Fields;
import erwins.util.spring.SpringUtil;
import erwins.util.text.StringAppender;


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
    
	/** 어쩔 수 없이 static으로 만듬.  ==> 나중에 멀티맵으로 수정하자 */
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
        
        //mybatis 소소를 깊게 보기 싫어서 그냥 쓴다.
        PreparedStatementHandler delegate = (PreparedStatementHandler) F1.get(handler);
    	MappedStatement  mappedStatement = (MappedStatement) F2.get(delegate);
    	String sqlId = mappedStatement.getId();
        
        Object result;
        try {
        	long startTime = System.currentTimeMillis();
            result = invocation.proceed();
            long endTime = System.currentTimeMillis();
            if(log.isDebugEnabled()){
            	List<?> rows;
            	if(result instanceof Number) rows = Lists.newArrayList(result); //update의 경우 int 값으로 결과가 넘어온다
            	else if(result instanceof List) rows = (List<?>) result;
            	else throw new IllegalStateException("result는 List만 가능합니다. QueryStatisticsMybatisInterceptor를 디버깅 해주세요. " + result.getClass());
        		String resultLog = toLogSuccessText(sqlId,handler,rows,startTime,endTime);
        		log.debug(resultLog);
            }
            
            //정상적으로 처리된 SQL만 저장한다.
            synchronized (this) {
            	String sql = boundSql.getSql(); //ID 기준이 아닌 바인딩된 SQL 기준이다.  다이나믹 쿼리때문에 ID가 아닌 SQL기준으로 함
                QueryState state = MAP.get(sql);
                if(state==null){
                    state = new QueryState(sqlId,sql);
                    MAP.put(sql, state);
                }
                state.addQuery(endTime - startTime);
            }
            
        } catch (InvocationTargetException e) {
            boolean isLock = isLockSql(e);
            if(!isLock) {
            	log.error(toLogFailText(sqlId, handler));
            }
            throw e;
        } catch (Exception e) {
        	log.error(toLogFailText(sqlId, handler));
            throw e;
        }
        
        return result;
    }

    private static final String LINE = "===================================================================================================================================================================";
    private static final String SEP  = "-------------------------------------------------------------------------------------------------------------------------------------------------------------------";
	
    private static int MAX_RESULT_LOG_COUNT = 5;
    
	/** SQL이 정상적으로 실행되었다면 SQL과 결과를 로그로 찍어준다.
	 * 프로젝트마다 적절히 변경해서 사용해주자 */
	protected String toLogSuccessText(String sqlId,StatementHandler handler, List<?> rows,long startTime,long endTime) {
		String replacedSql = findReplacedSql(handler);
		StringAppender b = new StringAppender();
		b.appendLine("");
		b.appendLine(LINE + " SQL ID : " + sqlId);
		b.appendLine(trim(replacedSql));
		b.appendLine(SEP);
		
		for(int i=0;i<rows.size();i++){
			if(i >= MAX_RESULT_LOG_COUNT) break;
			Object row = rows.get(i);
			b.appendLine("ROW " + (i+1) + " : " + ReflectionUtil.toStringByLombok(row));
		}
		int remains = Ints.max(0,rows.size() - MAX_RESULT_LOG_COUNT);
		if(remains!=0) b.appendLine(MessageFormat.format(".... 외 {0}건",remains));
		b.appendLine(MessageFormat.format("RESULT ROW : {0} , 걸린시간 : {1}", rows.size(),new TimeString(endTime - startTime)));
		b.append(LINE);
		return b.toString();
	}
	
	/** 에러가 났을때는 SQL만 보여준다. */
	protected String toLogFailText(String sqlId,StatementHandler handler) {
		String replacedSql = findReplacedSql(handler);
		StringAppender b = new StringAppender();
		b.appendLine("");
		b.appendLine(LINE+" SQL FAIL : " + sqlId);
		b.appendLine(trim(replacedSql));
		b.append(LINE);
		return b.toString();
	}


    /** SQL에 파라메터를 매핑한다. 
     * 이 SQL은 복사-붙여넣기로 바로 실행 가능해야 한다.
     * 물론 매퍼에 따라 매핑이 상이함으로 기본 설정으로만 변경된다 */
    @SuppressWarnings("rawtypes")
	protected String findReplacedSql(StatementHandler handler){
    	BoundSql boundSql = handler.getBoundSql();
    	String sql = boundSql.getSql();
    	
    	Object param = handler.getParameterHandler().getParameterObject();
		if(param == null) return sql.replaceFirst("\\?", "''");
		if(param instanceof Number) return sql.replaceFirst("\\?", param.toString());
		if(param instanceof String) return sql.replaceFirst("\\?", "'" + param + "'");
    	
    	try {
			List<ParameterMapping> paramMapping = boundSql.getParameterMappings();	
			if(param instanceof Map){
				for(ParameterMapping mapping : paramMapping){
					String propValue = mapping.getProperty();
					Object value = ((Map) param).get(propValue);
					sql = replaceSql(sql, value);
				}
			}else{
				ExpressionParser parser = new SpelExpressionParser();
				for(ParameterMapping mapping : paramMapping){
					String propValue = mapping.getProperty();
					Object value = null;
					try {
						value = SpringUtil.elValue(parser, propValue, param);
					} catch (SpelEvaluationException e) {
						//XML 안에서 EACH - IN 구문으로 쓴 경우 propValue가 변형되어 나타남으로 여기서 찾아야 한다.
						value = boundSql.getAdditionalParameter(propValue);
					}
					sql = replaceSql(sql, value);
				}
			}
		} catch (Exception e) {
			//예외를 던지지 않는다.
			log.error("QueryStatisticsMybatisInterceptor.findReplacedSql() 도중 예외발생. 디버깅해주세요. 일단 replace되지 않은 SQL이 리턴됩니다" + e.getMessage());
		}
		return sql;
    }
    
    /** 향후 타입에 따라 추가해주도록 하자
     * 여기서는 오라클 (TO_TIMESTAMP)  */
	protected String replaceSql(String sql, Object value) {
		if(value==null) return sql.replaceFirst("\\?", "''");
		else if(value instanceof Number) return sql.replaceFirst("\\?", value.toString());
		else if(value instanceof Date){
			Date date = (Date) value;
			String dateString =  JodaUtil.YMDHMSS.print(date.getTime());
			//TO_DATE은 밀리리초까지 표현할 수 없다?? 따라서 TO_TIMESTAMP를 사용한다.
			String oracleFunctionType = String.format("TO_TIMESTAMP('%s','YYYYMMDDHH24MISSFF3')", dateString);
			return sql.replaceFirst("\\?", oracleFunctionType);
		}
		return sql.replaceFirst("\\?", "'" + value + "'");
	}

    /** XML에 있는 SQL을 보기좋게 해준다. 역시 플젝마다 틀리다. */
	protected String trim(String sql) {
    	return Joiner.on('\n').join(Splitter.on('\n').trimResults(CharMatcher.is('\t').or(CharMatcher.is(' '))).omitEmptyStrings().split(sql));
	}

    /** 이는 로직에 사용됨으로 로그 무시 */
    private boolean isLockSql(InvocationTargetException e) {
        Throwable cause = e.getCause();
        if(cause==null) return false; //이런경우야 없겠지만 혹시나.
        if(cause instanceof SQLException){
            SQLException ex = (SQLException)cause;
            if(ex.getSQLState()==null) return false; //큐브리드의 경유 상태값이 없다... ㄷㄷ
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
