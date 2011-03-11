
package erwins.gsample.sdk;

import groovy.lang.GroovyClassLoader;
import groovy.lang.GroovyObject;
import groovy.lang.GroovyShell;

import java.io.File;
import java.math.BigDecimal;

import junit.framework.Assert;

import org.junit.Test;

public class DynamicRunByJava {

    /** 그루비 조각을 스크립트처럼 사용 가능. */
    @Test 
    public void shell() {
        GroovyShell shell = new GroovyShell();
        StringBuilder buff = new StringBuilder();
        buff.append("def mass = 22.3; \n");
        buff.append("def velocity = 10.6; \n");
        buff.append("mass * velocity");
        Object result =  shell.evaluate(buff.toString());
        Assert.assertEquals(result, new BigDecimal("236.38"));
    }
    
    /** 
     * 직접 로딩(DB/File 등)도 가능
     * 타입을 무시하는 sum 메소드를 호출한다.
     * */
    @Test
    public void classLoader() throws Exception{
        GroovyClassLoader gcl = new GroovyClassLoader();
        Class<?> clazz = gcl.parseClass(new File("D:/PROJECT/workspace/mysysbrain/test/erwins/gsample/GBean.groovy"));
        GroovyObject obj = (GroovyObject) clazz.newInstance();
        Object result = obj.invokeMethod("sum",new Object[]{"가격",new BigDecimal("123.34")});
        Assert.assertEquals(result,"가격123.34");
        Object result2 = obj.invokeMethod("sum",new Object[]{new BigDecimal("123.66"),new BigDecimal("123.34")});
        Assert.assertEquals(result2, new BigDecimal("247.00"));
    }
}
