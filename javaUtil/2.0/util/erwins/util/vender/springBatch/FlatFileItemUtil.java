package erwins.util.vender.springBatch;

import java.io.File;
import java.nio.charset.Charset;
import java.util.List;

import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.mapping.PassThroughLineMapper;
import org.springframework.batch.item.file.transform.PassThroughLineAggregator;
import org.springframework.core.io.FileSystemResource;

import com.google.common.collect.Lists;

import erwins.util.lib.CharEncodeUtil;



/**
 * @author sin
 */
public class FlatFileItemUtil{
	
	private  Charset encoding = CharEncodeUtil.C_UTF_8;
	private int commitInterval = 1000;
    
	/** 대용량 텍스트 파일 합치기. 검증 완료 */
	public void merge(File out,Iterable<File> list){
		FlatFileItemWriter<String> writer = new FlatFileItemWriter<String>();
        writer.setResource(new FileSystemResource(out));
        writer.setLineAggregator(new  PassThroughLineAggregator<String>());
        writer.setEncoding(encoding.name());
        writer.open(new ExecutionContext());
        try{
        	for(File each : list){
        		FlatFileItemReader<String> reader = new FlatFileItemReader<String>();
        		reader.setResource(new FileSystemResource(each));
        		reader.setLineMapper(new PassThroughLineMapper());
        		reader.setEncoding(encoding.name());
        		reader.open(new ExecutionContext());
        		String line = null;
        		List<String> lines = Lists.newArrayList();
        		try {
					while((line=reader.read()) != null){
						lines.add(line);
						if(lines.size() >= commitInterval){
							writer.write(lines);
							lines = Lists.newArrayList();
						}
					} 
					if(lines.size()!=0)  writer.write(lines);
				}finally{
					reader.close();
				}
        	}
        }catch(Exception e){
        	throw new RuntimeException(e);
        }finally{
        	writer.close();	
        }
        
	}

}
