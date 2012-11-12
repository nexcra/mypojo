package erwins.util.vender.spring;

import java.util.Iterator;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.apache.ibatis.session.ResultContext;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.SqlSession;

import erwins.util.nio.ThreadUtil;

/** 
 * 웬만하면 자체 제공해주는걸 사용할것!!
 * Mybatis의 SQL Stream을  SpringBatch에서 사용하기 위한  Iterator이다
 * 즉 callback스타일을 Iterator로 변경해준다.
 * 스프링 배치가 아니라면 이런 뻘짓을 할 필요는 없다.
 * XML에 페치사이즈를 커밋주기와 맞춰주자
 * Iterator를 읽을때는 반드시 동기화를 하자
 * 
 * 테스트용 클래스임.  실무 사용 금지
 *  
 *  */
@Deprecated
public class MybatisIterator<T> extends Thread implements Iterator<T>{
    
    private final BlockingQueue<T> queue = new LinkedBlockingQueue<T>();
    private final SqlSession sqlSession;
    private final String sqlId;
    private final Object param;
    
    /** queue에 들어가는 최대 자료 수. 메모리 여유에 따라 조절하자 */
    private int limit = 10000;
    
    public MybatisIterator(SqlSession sqlSession,String sqlId,Object param){
        this.sqlSession = sqlSession;
        this.sqlId = sqlId;
        this.param = param;
        setDaemon(true);
        setName(this.getClass().getSimpleName());
    }
    
    private ResultHandler handler = new ResultHandler(){
        @SuppressWarnings("unchecked")
        @Override
        public void handleResult(ResultContext arg0) {
            queue.add((T) arg0.getResultObject());
            if(queue.size() >= limit) ThreadUtil.sleep(500);
        }
    };

    @Override
    public boolean hasNext() {
        boolean end = !isAlive() && queue.size()==0; 
        return !end;
    }

    /** 싱글스래드에서 동작한다. */
    @Override
    public void run() {
        sqlSession.select(sqlId, param, handler);
    }
    
    /** 더이상의 자료가 없으면 null을 리턴한다.
     * 이놈은 스래드 세이프하게 호출되어야 한다. */
    public T getItem() {
        if(isAlive())
            try {
                return queue.poll(20, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                return null;
            }
        return  queue.poll();
    }
    
    @Override
    public T next() {
        try {
            return queue.poll(20, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            return null;
        }
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }
    
    
}
