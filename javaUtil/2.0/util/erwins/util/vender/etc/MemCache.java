package erwins.util.vender.etc;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import net.spy.memcached.ConnectionFactory;
import net.spy.memcached.ConnectionFactoryBuilder;
import net.spy.memcached.MemcachedClient;
import net.spy.memcached.internal.OperationFuture;



/**
 * MemcachedClient를 감싼다.
 * 레퍼런스 확인용
 * @author sin
 */
public class MemCache<T>{
    
    private final MemcachedClient client;
    
    public MemCache (int port,String ... ips) throws IOException{
        ConnectionFactoryBuilder cfb = new ConnectionFactoryBuilder();
        cfb.setTimeoutExceptionThreshold((int) TimeUnit.SECONDS.toMillis(2));
        ConnectionFactory cf = cfb.build();

        List<InetSocketAddress> addrs = new ArrayList<InetSocketAddress>();
        for(String ip : ips){
        	addrs.add(new InetSocketAddress(ip, port));	
        }
        client = new  MemcachedClient(cf,addrs);
    }
    
    @SuppressWarnings("unchecked")
	public T get(String key){
        return (T) client.get(key);
    }
    
    /** 벌크 입력 후 정상 입력을 확인한다. */
	protected void removeBuffer(LinkedList<OperationFuture<Boolean>> operationBuffer) throws InterruptedException, TimeoutException, ExecutionException {
		while(operationBuffer.size() > 0) operationBuffer.removeFirst().get(2, TimeUnit.SECONDS);
	}
   

}
