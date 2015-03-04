package erwins.util.vender.mybatis;

import java.io.Serializable;

import lombok.Data;

@SuppressWarnings("serial")
@Data
public class QueryState implements Comparable<QueryState>,Serializable{
    
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
