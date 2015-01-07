package erwins.util.spring.batch.tool;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.sql.Driver;
import java.util.Collection;
import java.util.List;

import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.AfterStep;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemStream;
import org.springframework.beans.factory.InitializingBean;

import erwins.util.lib.ReflectionUtil;
import erwins.util.root.exception.PropagatedRuntimeException;


/**
 * 간단 유틸 or 예제 모음
 */
public abstract class SpringBatchUtil{
	
	/** ex) oracle.jdbc.OracleDriver */
	public static <T extends Driver> BasicDataSource createDataSource(Class<T> driver,String url,String id,String password){
		BasicDataSource from = new BasicDataSource();
    	from.setUsername(id);
    	from.setPassword(password);
    	from.setDriverClassName(driver.getName());
    	from.setUrl(url);
		return from;
	}
	
	public static void openIfAble(Object itemSreeam,ExecutionContext executionContext){
		if(itemSreeam instanceof ItemStream) ((ItemStream)itemSreeam).open(executionContext);
	}
	public static void updateIfAble(Object itemSreeam,ExecutionContext executionContext){
		if(itemSreeam instanceof ItemStream) ((ItemStream)itemSreeam).update(executionContext);
	}
	public static void closeIfAble(Object itemSreeam){
		if(itemSreeam instanceof ItemStream) ((ItemStream)itemSreeam).close();
	}
	public static void afterPropertiesSetIfAble(Object delegate) throws Exception{
		if(delegate instanceof InitializingBean){
			((InitializingBean)delegate).afterPropertiesSet();
		}
	}

    /** Step의 ExitCode접두어가 하나라도 일치한다면 Job종료코드를 변경한다. */
    public static void changeJobExitCodeByStepPrefix(JobExecution je,String prefix,ExitStatus status) {
        Collection<StepExecution> steps = je.getStepExecutions();
        for(StepExecution stepEx : steps){
            String code = stepEx.getExitStatus().getExitCode();
            if(code.startsWith(prefix)){
                je.setExitStatus(status);
                break;
            }
        }
    }
	
	/** 비포가 있다면 실행해둔다... 별걸 다 만들게 되넹 ㅠㅠ */
	public static void beforeStepIfAble(Object batchObject,StepExecution executionContext){
		stepIfAble(batchObject, executionContext,BeforeStep.class);
	}
	public static void afterStepIfAble(Object batchObject,StepExecution executionContext){
		stepIfAble(batchObject, executionContext,AfterStep.class);
	}

	private static void stepIfAble(Object batchObject,StepExecution executionContext,Class<? extends Annotation> clazz) {
		if(batchObject==null) return;
		
		List<Method> beforeSteps = ReflectionUtil.findMethod(batchObject.getClass(),clazz);
		if(beforeSteps.size()==0) return;
		if(beforeSteps.size() > 1) throw new IllegalArgumentException(clazz.getSimpleName()+ " 는 1개만 허용됩니다");
		
		Method beforeStep = beforeSteps.get(0);
		Class<?>[] parameterTypes = beforeStep.getParameterTypes();
		if(parameterTypes==null || parameterTypes.length != 1) throw new IllegalArgumentException(clazz.getSimpleName()+ " 적합한 파라메터가 아닙니다");
		if(parameterTypes[0] != executionContext.getClass()) throw new IllegalArgumentException(clazz.getSimpleName()+ " 적합한 파라메터가 아닙니다");
		
		try {
			beforeStep.setAccessible(true);
			beforeStep.invoke(batchObject, executionContext);
		} catch (Exception e) {
			throw new PropagatedRuntimeException(e);
		}
	}
	
    
}
