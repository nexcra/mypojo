package erwins.util.vender.etc;

import java.util.Iterator;
import java.util.List;

/** Spring Batch 등에서 전체 아이템의 수를 신경쓰지 않고 item단위별 처리시 사용한다.
 * 일반적인 페이징처리 대신 index를 타는 페이징 조건이 필요할때 사용한다.
 * 미리 사용될 파라메터를 구해놓아야 한다. 
 * P : 파라메터
 * 스프링배치에서 멀티스래드로 사용할려면 스래드 세이프 해야한다.
 * 별도의 검중작업은 하지 않음.. 수정해서 사용하자.
 * 주의 : findData() 호출은 멀티스래드 적용이 되지 않는다. 
 * 
 * -> 나중에 스트리밍으로 읽을 수도 없는 상황이 있으면 응용?해서 짜보자
 *  */
@Deprecated
public abstract class PagingSqlIterator<T,P> implements Iterator<T>{
    
    private final Iterator<P> queryParameter;
    //private final FindDataCallback<T,P> callback;
    
    private Iterator<T> currentIterator;
    
    public PagingSqlIterator(List<P> params){
        if(params.size()==0) throw  new RuntimeException("parameter size 0");
        queryParameter = params.iterator();
        changeCurrent();
        getDataWhileAble(); //최초로드시 데이터가 없어도 무시한다.
    }

    /** queryParameter에 읽을게 떨어지면 false */
    @Override
    public synchronized boolean hasNext() {
        boolean able = currentIterator.hasNext();
        if(able) return true;
        return getDataWhileAble();
    }
    
    /** hasNext()가 아닐때 이것이 호출되면 안된다. */
    @Override
    public synchronized T next() {
        return currentIterator.next();
    }

    /** 읽을 데이터가 존재할때까지 findData를 시도한다. */
    private boolean getDataWhileAble() {
        while(!currentIterator.hasNext()){
            if(!queryParameter.hasNext()) return false;
            changeCurrent();
        }
        return true;
    }

    private void changeCurrent() {
        P param = queryParameter.next();
        currentIterator = findData(param).iterator();
    }
    
    protected abstract List<T> findData(P param);

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }
    /*
    public static interface FindDataCallback<T,P>{
        public List<T> findData(P param);
    }*/
    
    
}
