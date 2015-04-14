package erwins.jsample.validation;


import org.apache.log4j.Level;
import org.junit.Before;
import org.junit.BeforeClass;
import org.springframework.format.support.FormattingConversionService;
import org.springframework.validation.BindingErrorProcessor;
import org.springframework.validation.BindingResult;
import org.springframework.validation.DataBinder;
import org.springframework.validation.ObjectError;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import erwins.util.nio.ThreadUtil;
import erwins.util.spring.web.conversion.ConversionSet;
import erwins.util.spring.web.conversion.PatternFormatFactory;
import erwins.util.spring.web.conversion.StringFormatFactory;
import erwins.util.validation.ConstraintViolationMessageConverter;
import erwins.util.vender.apache.Log4JConfig;



public abstract class TestRoot{
	
	protected LocalValidatorFactoryBean validator; 
	protected FormattingConversionService conversionService;
	protected BindingErrorProcessor bindingErrorProcessor;
	protected ConstraintViolationMessageConverter constraintViolationMessageConverter;
	
	@BeforeClass
	public static void beforeClass() {
		Log4JConfig.configRootAndConsole(Level.INFO, Log4JConfig.DEFAULT_PATTERTN);
	}
	
	@Before
	public void before() {
		validator = new LocalValidatorFactoryBean(); 
		validator.afterPropertiesSet();
		
		conversionService = new FormattingConversionService();
		conversionService.addConverter(ConversionSet.TO_DATE);
		conversionService.addConverter(ConversionSet.TO_DATETIME);
		conversionService.addFormatterForFieldAnnotation(new StringFormatFactory());
		conversionService.addFormatterForFieldAnnotation(new PatternFormatFactory());
		
		constraintViolationMessageConverter = new ConstraintViolationMessageConverter();
	}
	
	public void print(DataBinder binder){
		
		
		System.out.println("VO : " + binder.getTarget());
		
		BindingResult result = binder.getBindingResult();
		
		ThreadUtil.sleep(5);
		if(result.hasErrors()){
			System.err.println("====== 에러 ====== " + result.getErrorCount());
			for(ObjectError error : result.getAllErrors()){
				String msg = constraintViolationMessageConverter.convert(error);
				System.err.println(msg);
			}
		}else{
			System.out.println("============ 에러 없음 ===========");
		}
		
	}
	

}
