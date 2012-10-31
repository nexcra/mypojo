package erwins.util.vender.springBatch;

import org.springframework.batch.item.file.LineMapper;


public class StringLineMapper implements LineMapper<String>{
    
    @Override
    public String mapLine(String line, int lineNumber) throws Exception {
        return line;
    }

}
