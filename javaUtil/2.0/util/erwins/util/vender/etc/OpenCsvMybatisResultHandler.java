package erwins.util.vender.etc;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;

import org.apache.ibatis.session.ResultContext;
import org.apache.ibatis.session.ResultHandler;

import au.com.bytecode.opencsv.CSVWriter;
import erwins.util.lib.CharEncodeUtil;

/** 스트리밍으로 write하는 mybatis용 핸들러 
 * 사용후 반드시 닫아주자. */
public abstract class OpenCsvMybatisResultHandler implements ResultHandler{
	
	private CSVWriter writer;
    /** 기본값 UTF-8 , MS-OFFICE로 읽을 경우 EUC-KR로 해야 한글이 깨지지 않는다.  */
    private Charset encoding = CharEncodeUtil.C_UTF_8;
    /** 기본 100kb  */
    private int bufferSize = 1024*100;
    public void open(OutputStream out){
        OutputStreamWriter osw = new OutputStreamWriter(out,encoding);
        BufferedWriter bw = new BufferedWriter(osw,bufferSize);
        writer =  new CSVWriter(bw);
    }
    /** null인경우 skip으로 처리 */
    @Override
    public void handleResult(ResultContext arg0) {
        String[] csv = resultContexttoCsv(arg0);
        if(csv==null) return;
        writer.writeNext(csv);
    }
    
    public abstract String[] resultContexttoCsv(ResultContext arg0);
    
    public void close(){
        if(writer==null) throw new RuntimeException("open is required"); 
        try {
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public Charset getEncoding() {
        return encoding;
    }
    public void setEncoding(Charset encoding) {
        this.encoding = encoding;
    }
    public int getBufferSize() {
        return bufferSize;
    }
    public void setBufferSize(int bufferSize) {
        this.bufferSize = bufferSize;
    }
    
    
}
