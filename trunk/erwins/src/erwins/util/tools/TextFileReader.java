
package erwins.util.tools;

import java.io.*;

import erwins.util.lib.CharSets;

/**
 * 텍스트 파일을 리더를 래핑한다. 
 * 복잡한 예외처리 등을 할 수 없지만 간단한거 할때 좋다. 
 */
public class TextFileReader {

    /**
     * 한줄씩 작업을 처리하자. 
     */
    public interface LineCallback {
        public void process(String line) throws Exception;
    }
    
    /**
     * 디폴트로  UTF_8을 사용한다.
     */
    public static void read(File file, LineCallback callback){
        read(file,callback,CharSets.UTF_8);
    }
    
    public static void read(File file, LineCallback callback,String encoding) {
        InputStreamReader isr = null;
        BufferedReader br = null;
        try {
            //reader = new BufferedReader(new FileReader(path)); => 이거는 안씀?
            isr = new InputStreamReader(new FileInputStream(file), encoding);
            br = new BufferedReader(isr);
            String line = null;

            while ((line = br.readLine()) != null) {
                callback.process(line);
            }
        }
        catch (Exception ex) {
            throw new RuntimeException(ex);
        }
        finally {
            if (isr != null) {
                try {
                    isr.close();
                }
                catch (IOException ex) {
                    throw new RuntimeException(ex);        
                }
            }
        }
    }
}