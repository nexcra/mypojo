package erwins.util.vender.springBatch;

import org.springframework.batch.item.file.LineMapper;

import erwins.util.lib.StringUtil;


/** 전용 CSV리더로 읽어야 한다. */
@Deprecated
public abstract class CsvLineMapper<T> implements LineMapper<T>{

    @Override
    public T mapLine(String line, int lineNumber) throws Exception {
        if(line==null) return null;
        String[] cols = StringUtil.splitPreserveAllTokens(line, ',');
        return mapLineCsv(cols,lineNumber);
    }
    
    public abstract T mapLineCsv(String[] cols, int lineNumber) throws Exception;

}
