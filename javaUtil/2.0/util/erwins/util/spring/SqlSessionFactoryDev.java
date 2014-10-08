package erwins.util.spring;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import javax.annotation.PreDestroy;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;

import erwins.util.root.exception.PropagatedRuntimeException;
import erwins.util.spring.SpringUtil.AntResourceType;

/** 
 * 개발용 sql.xml 리로더.
 * setMapperLocations로 올리면 jrebel이 인식하지 못해서 대체하기위해 네이버 검색후 작성 
 *  */
public class SqlSessionFactoryDev extends SqlSessionFactoryBean {
    
    private Logger log = LoggerFactory.getLogger(this.getClass());

    private SqlSessionFactory proxy;
    private int interval  = 1000*5;
    private Map<File,Long> locationedSqlFiles;
    private final ReentrantReadWriteLock rwl = new ReentrantReadWriteLock();
    private final Lock readLock = rwl.readLock();
    private final Lock writeLock = rwl.writeLock();
    private Thread reloader;
    /** 스래드를 띄울것인지? */
    private boolean threadAble  = true;
    
    /** 애꺼는 jrebel이 알아서 해준다. */
    public void setConfigLocation(Resource configLocation) {
        super.setConfigLocation(configLocation);
    }
    
    /** ,로 분리해서 넣자. classpath:가 기준이 된다.  */
    public void setMapperLocationsByAnt(String packageAntMatchs) throws IOException {
    	Resource[] mappingLocations = SpringUtil.antToResources(AntResourceType.classpath,packageAntMatchs);
    	setMapperLocations(mappingLocations);
    }
    
    public void setMapperLocations(Resource[] mappingLocations) {
        super.setMapperLocations(mappingLocations);
        if(!threadAble) return;
        List<File> locationSqlFiles = SpringUtil.toFiles(mappingLocations);
        locationedSqlFiles = new ConcurrentHashMap<File,Long>();
        for(File each : locationSqlFiles) locationedSqlFiles.put(each, each.lastModified());
    }
    
    /**
     * iBATIS 설정을 다시 읽어들인다.<br /> SqlSessionFactory 인스턴스 자체를 새로 생성하여 교체한다.
     * @throws Exception
     */
    public void refresh(){
        writeLock.lock();
        try {
            super.afterPropertiesSet();
        } catch (Exception e) {
            throw new PropagatedRuntimeException(e);
        } finally {
            writeLock.unlock();
        }
    }
    
    /**
     * afterPropertiesSet()이 호출될때마다 SqlSessionFactory를 새로 만들어 준다.
     * 단순 어뎁터에 불과하지만 이렇게 해야 내부적으로 인식하는듯 하다.
     */
    public void afterPropertiesSet() throws Exception {
        super.afterPropertiesSet();
        makeProxy();
        if(!threadAble) return;
        log.warn("파일 변경 감지를 위한 스래드가 기동됩니다. 실서버에서는 이 메세지가 보이면 안됨. sqlmapSize : " + locationedSqlFiles.size());
        startRefreshThread();
    }
    
    private void startRefreshThread() {
        Runnable refresh = new Runnable(){
            @Override
            public void run() {
                try {
                    while(true){
                        if(Thread.currentThread().isInterrupted()) break;
                        Thread.sleep(interval);
                        for(Entry<File,Long> entry : locationedSqlFiles.entrySet()){
                            boolean changed = entry.getKey().lastModified() != entry.getValue().longValue();
                            if(changed){
                                log.warn("파일 변경 감지 : {}", entry.getKey().getName());
                                refresh();
                            }
                        }
                        Iterator<File> i = locationedSqlFiles.keySet().iterator();
                        while(i.hasNext()){
                            File each = i.next();
                            locationedSqlFiles.put(each, each.lastModified());
                        }
                    }
                } catch (InterruptedException e) {
                    //아무것도 하지 않는다
                }
                log.warn("파일 변경 감지를 위한 스래드 종료");
            }
        };
        
        reloader = new Thread(refresh);
        reloader.setName("mybatis-sqlmap-reloader");
        reloader.start();
    }

    private void makeProxy() {
        proxy = (SqlSessionFactory) Proxy.newProxyInstance(SqlSessionFactory.class.getClassLoader(), new Class[] { SqlSessionFactory.class, }
        , new InvocationHandler() {
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                return method.invoke(getParentObject(), args);
            }
        });
    }

    private Object getParentObject() throws Exception {
        readLock.lock();
        try {
            return super.getObject();
            
        } finally {
            readLock.unlock();
        }
    }
    
    public SqlSessionFactory getObject() {
        return this.proxy;
    }
    
    public Class<? extends SqlSessionFactory> getObjectType() {
        return (this.proxy != null ? this.proxy.getClass() : SqlSessionFactory.class);
    }
    
    public boolean isSingleton() {
        return true;
    }
    
    public void setCheckInterval(int ms) {
        interval = ms;
    }

    @PreDestroy
    public void destroy(){
        if(reloader!=null) reloader.interrupt();
    }

	public boolean isThreadAble() {
		return threadAble;
	}

	public void setThreadAble(boolean threadAble) {
		this.threadAble = threadAble;
	}
    
    
  
}
