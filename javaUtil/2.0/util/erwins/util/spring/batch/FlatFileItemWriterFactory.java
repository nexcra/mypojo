package erwins.util.spring.batch;

import groovy.sql.GroovyRowResult;

import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.transform.PassThroughLineAggregator;
import org.springframework.core.io.FileSystemResource;



/**
 * 간이 CSV라이터 등 만들때 사용
 * 배치로직을 따르고, LineAggregator를 재활용 하기위해  FlatFileItemWriter를 사용을 추천
 * @author sin
 */
public abstract class FlatFileItemWriterFactory{
    
	/** 나중에 클로저를 받도록 수정~ */
	public static FlatFileItemWriter<GroovyRowResult> groovyWriter(String filepath){
		FlatFileItemWriter<GroovyRowResult> w = new FlatFileItemWriter<GroovyRowResult>();
        w.setResource(new FileSystemResource(filepath));
        w.setLineAggregator(new LineAggregatorGroovySql());
        w.setEncoding("MS949");  //EUC-KR이 아니라 949임 주의
        w.open(new ExecutionContext());
        return w;
	}
	
	public static FlatFileItemWriter<String> simpleWriter(String filepath){
		FlatFileItemWriter<String> w = new FlatFileItemWriter<String>();
        w.setResource(new FileSystemResource(filepath));
        w.setLineAggregator(new  PassThroughLineAggregator<String>());
        w.setEncoding("MS949");  //EUC-KR이 아니라 949임 주의
        w.open(new ExecutionContext());
        return w;
	}

}
