
package erwins.util.tools;

import java.io.*;

import erwins.util.lib.CharSets;
import erwins.util.lib.Encoders;

/**
 * BufferedReader 래핑.. 걍 참고만 할것
 */
public class TextReader {
    
    protected InputStreamReader isr;
    protected BufferedReader br;
    
    /**
     * 반드시 인코딩 타입(EUC_KR.UTF-8)을 지정해주어야 한다.
     */
    public TextReader(File file){
        try {
            isr = new InputStreamReader(new FileInputStream(file), CharSets.UTF_8);
        }
        catch (Exception e) {
            Encoders.stackTrace(e);
            throw new RuntimeException(e.getMessage(),e);
        }
        br = new BufferedReader(isr);
    }
    
    /**
     * 더이상 없으면 null을 리턴한다. 
     */
    public String next(){
        try {
            return br.readLine();
        }
        catch (IOException e) {
            Encoders.stackTrace(e);
            throw new RuntimeException(e.getMessage(),e);
        }
    }
    
    public void close(){
        if (isr != null) try {
            isr.close();
        }
        catch (IOException e) {
            Encoders.stackTrace(e);
            throw new RuntimeException(e.getMessage(),e);
        }
    }
    

}