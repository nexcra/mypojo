package erwins.util.counter;

import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicLong;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.google.common.collect.Table.Cell;

/**
 * 테이블로 숫자세기. 옵션을 2종으로 나눌 수 있을때 유용하다.  
 * MultiSet이 int 단위밖에 안되서 대체 사용한다 (로그 카운트용)
 * @author sin
 */
public class TableCounter implements Serializable,Iterable<Entry<Object, Map<Object, AtomicLong>>>{
    
	private static final long serialVersionUID = -7099543615031178498L;
	
	private Table<Object,Object,AtomicLong> map = HashBasedTable.create();
	
	/** 다 더하는 위임메소드.  Table의 putAll과는 완전히 틀리다. 오버라이드가 아니라 값을 더한다. */
	public void putAll(TableCounter other){
		for(Cell<Object, Object, AtomicLong> cell : other.map.cellSet()){
			this.addAndGet(cell.getRowKey(), cell.getColumnKey(), cell.getValue().longValue());
		}
	}

    public long incrementAndGet(Object row,Object col){
        AtomicLong value = get(row,col);
        return value.incrementAndGet();
    }
    
    public long addAndGet(Object row,Object col,long delta){
    	AtomicLong value = get(row,col);
        return value.addAndGet(delta);
    }

    /** null을 리턴하지 않는다. */
    public synchronized AtomicLong get(Object row,Object col) {
    	AtomicLong value = map.get(row, col);
    	if(value==null){
    		value = new AtomicLong();
            map.put(row,col, value);
    	}
    	return value;
    }
    
    public long getValue(Object row,Object col) {
    	AtomicLong value = get(row,col);
        return value.get();
    }
    
    public synchronized long  totalRowCount(Object row) {
        return totalCount(map.row(row).values());
    }
    
    public synchronized long totalColCount(Object col) {
        return totalCount(map.column(col).values());
    }
    
    public synchronized long totalCount() {
        return totalCount(map.values());
    }
    
    public static long totalCount(Collection<AtomicLong> datas){
    	long sum = 0;
        for(AtomicLong each : datas) sum += each.get();
        return sum;
    }
    
    public synchronized void clear() {
    	map.clear();
    }
    
    public Table<Object,Object,AtomicLong> getTable() {
    	return map;
    }

	@Override
	public Iterator<Entry<Object, Map<Object, AtomicLong>>> iterator() {
		return map.rowMap().entrySet().iterator();
	}
	
	@Override
	public String toString() {
		return map.toString();
	}
    
    
}
