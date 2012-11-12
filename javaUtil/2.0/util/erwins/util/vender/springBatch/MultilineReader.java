package erwins.util.vender.springBatch;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

import org.apache.commons.io.IOUtils;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemStream;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;

import erwins.util.lib.CharEncodeUtil;

/** 스프링배치에서 지원하지 않아서(정확히 확인안해봄) 만듬.
 * 여러 줄의 데이터가 하나의 item을 구성할때 사용한다. (이렇게 만드는것은 매우 비추!)
 * ex) ebay의 옥션이 이런식으로 데이터를 전송  */ 
public abstract class MultilineReader<T> implements ItemReader<T>,ItemStream{
    
    private BufferedReader br;
    private Charset charset = CharEncodeUtil.C_EUC_KR; 
    private int bufferSize = 4096;
    private MultilineLineToItem<T> multilineLineToItem;
    
    public static interface MultilineLineToItem<T>{
        public T lineToItem(String line);
    }
    

    /** 전체를 동기화 해서 읽는다. */
    @Override
    public synchronized T read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
        String line = null;
        while ((line = br.readLine()) != null) {
            T vo = multilineLineToItem.lineToItem(line);
            if(vo!=null) return vo;
        }
        return null;
    }

    /** 초기화시 반드시 호출하도록 하자.
     * 직접 구현시 Open()에 구현하고, 외부 파일로 작업시 별도로 호출해주자 */
    public void openResource(InputStream in) {
        InputStreamReader isr =  new InputStreamReader(in,charset);
        br = new BufferedReader(isr,bufferSize);
    }

    @Override
    public void update(ExecutionContext executionContext) throws ItemStreamException {
        doUpdate(executionContext);
    }
    
    /** 기본적으로 아무것도 하지 않는다. 필요하면 오버라이드 하자 */
    protected  void doUpdate(ExecutionContext executionContext){
      //아무것도 하지 않는다
    }

    @Override
    public void close() throws ItemStreamException {
        IOUtils.closeQuietly(br);
    }

    public void setCharset(Charset charset) {
        this.charset = charset;
    }

    public void setBufferSize(int bufferSize) {
        this.bufferSize = bufferSize;
    }

	public void setMultilineLineToItem(MultilineLineToItem<T> multilineLineToItem) {
		this.multilineLineToItem = multilineLineToItem;
	}

    

}
