
package erwins.util.tools;

import java.net.MalformedURLException;
import java.text.MessageFormat;

import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.remote.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 사용 전에 설정할게 좀 많다.
 *  <bean id="registry" class="org.springframework.remoting.rmi.RmiRegistryFactoryBean">
        <property name="port" value="1234"/>
    </bean>

    <!-- 현재 JVM에 MBean Server로 연결시켜주는 커넥터를 생성하고 RMI 레지스트리에 등록한다. -->
    <bean id="serverConnector" class="org.springframework.jmx.support.ConnectorServerFactoryBean">
        <property name="objectName" value="connector:name=rmi"/>
        <property name="serviceUrl" value="service:jmx:rmi://localhost/jndi/rmi://localhost:1234/wcs"/>
    </bean>
 *??? 이거 정상되는거였나? ㅋㅋ
 */
public class JMXHelper {
    
    private Log log = LogFactory.getLog(this.getClass()); 
    JMXServiceURL jmxUrl;
    
    public JMXHelper(String ip,String port,String name) throws MalformedURLException{
        String url = MessageFormat.format("service:jmx:rmi://{0}/jndi/rmi://{0}:{1}/{2}", ip,port,name);
        jmxUrl = new JMXServiceURL(url);
        log.info("=== JMX init "+url+" ===");
    }
    
    /**
     * Object 이름은 @ManagedResource를 달때 지정해주던가.. 아니면 디폴트 이름(콘솔에서 확인) 사용.
     * 환경에서 비밀번호?를 입력 안해줄려면 같은 계정에서만 되는듯.. (같은 유닉스의 ID/Pass)
     * 즉 로컬에서 실서버로 소스를 사용해서는 연결 안됨.
     */
    public Object invok(String objectName,String methodName,Object ... params) throws Exception{        
        JMXConnector jmxc = JMXConnectorFactory.connect(jmxUrl, null);
        MBeanServerConnection mbsc = jmxc.getMBeanServerConnection();
        ObjectName mbeanName = new ObjectName(objectName);
        if(params.length==0) params = null;
        Object result = mbsc.invoke(mbeanName, methodName, params, null);
        jmxc.close();
        log.info("=== JMX end ===");
        return result;
    }


}
