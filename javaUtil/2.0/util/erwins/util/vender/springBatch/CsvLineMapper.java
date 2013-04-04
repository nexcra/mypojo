package erwins.util.vender.springBatch;

import org.springframework.batch.item.file.LineMapper;

import erwins.util.lib.StringUtil;


public abstract class CsvLineMapper<T> implements LineMapper<T>{

    @Override
    public T mapLine(String line, int lineNumber) throws Exception {
        if(line==null) return null;
        String[] cols = StringUtil.splitPreserveAllTokens(line, ',');
        return mapLineCsv(cols,lineNumber);
    }
    
    public abstract T mapLineCsv(String[] cols, int lineNumber) throws Exception;

}
