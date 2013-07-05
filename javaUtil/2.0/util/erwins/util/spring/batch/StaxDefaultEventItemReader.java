package erwins.util.spring.batch;

import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemStream;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.batch.item.file.ResourceAwareItemReaderItemStream;
import org.springframework.batch.item.xml.StaxEventItemReader;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.io.Resource;

import erwins.util.spring.SpringUtil;
import erwins.util.spring.batch.StaxDefaultUnmarshaller.XmlStreamEvent;


/** 기본 제공되는 리더를 위임한다.
*/
public class StaxDefaultEventItemReader implements ItemReader<XmlStreamEvent>,ItemStream,InitializingBean,ResourceAwareItemReaderItemStream<XmlStreamEvent>{
    
    private StaxEventItemReader<XmlStreamEvent> reader = new StaxEventItemReader<XmlStreamEvent>();
    
    public StaxDefaultEventItemReader(){
    	reader.setUnmarshaller(new StaxDefaultUnmarshaller());
    }
    
    
    /** 읽을 자료가 없을경우 beforeStep에서 true로 설정하면 즉시 리더가 종료된다
     * XML에 빈값을 넣기 힘들어서 이렇게 처리했다
     * 더 좋은방법이 있는지는 모르겠다. */
    private boolean empty = false;
    
    /** null을 입력할 경우 0개를 read하며 즉시 배치가 종료된다 */
    public void setResource(Resource resource) {
    	if(resource==null){
    		reader.setResource(SpringUtil.getEmptyResource());
    		empty = true;
    	}else reader.setResource(resource); 
    }

    public void close() throws ItemStreamException {
        reader.close();
    }

    public void afterPropertiesSet() throws Exception {
        reader.afterPropertiesSet();
    }

    public boolean equals(Object obj) {
        return reader.equals(obj);
    }

    public int hashCode() {
        return reader.hashCode();
    }

    public boolean isSaveState() {
        return reader.isSaveState();
    }

    public synchronized XmlStreamEvent read() throws Exception, UnexpectedInputException, ParseException {
    	if(empty) return null;
        return reader.read();
    }

    public void setStrict(boolean strict) {
        reader.setStrict(strict);
    }

    public void setCurrentItemCount(int count) {
        reader.setCurrentItemCount(count);
    }



    public void open(ExecutionContext executionContext) throws ItemStreamException {
        reader.open(executionContext);
    }

    public void setFragmentRootElementName(String fragmentRootElementName) {
        reader.setFragmentRootElementName(fragmentRootElementName);
    }

    public void setMaxItemCount(int count) {
        reader.setMaxItemCount(count);
    }

    public void update(ExecutionContext executionContext) throws ItemStreamException {
        reader.update(executionContext);
    }

    public void setName(String name) {
        reader.setName(name);
    }

    public void setSaveState(boolean saveState) {
        reader.setSaveState(saveState);
    }

    public String toString() {
        return reader.toString();
    }
    

}
