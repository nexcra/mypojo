package erwins.util.vender.springBatch;

import erwins.util.guava.FunctionSet;
import groovy.sql.GroovyRowResult;

import org.apache.commons.io.IOUtils;
import org.springframework.batch.item.file.transform.LineAggregator;

import com.google.common.base.Joiner;
import com.google.common.collect.FluentIterable;


/** 그루비용. SQL을 CSV로 변환할때 사용한다 */
public class LineAggregatorGroovySql implements LineAggregator<GroovyRowResult>{
    
    private Joiner joiner = Joiner.on(',').useForNull("");
    private boolean first = true;
    
    @SuppressWarnings("unchecked")
    @Override
    public String aggregate(GroovyRowResult item) {
        if(first){
            first = false;
            return joiner.join(item.keySet()) + IOUtils.LINE_SEPARATOR + joiner.join(FluentIterable.from(item.values()).transform(FunctionSet.SQL_TO_STRING));
        }
        return joiner.join(FluentIterable.from(item.values()).transform(FunctionSet.SQL_TO_STRING));
    }
    
    

}
