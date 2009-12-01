
package erwins.util.tools;

import java.io.*;

import erwins.util.lib.CharSets;
import erwins.util.reflexive.FolderIterator;
import erwins.util.root.StringCallback;

/**
 * 텍스트 파일을 리더를 래핑한다. 
 * 복잡한 예외처리 등을 할 수 없지만 간단한거 할때 좋다. 
 */
public class TextFileReader {
    
    /** 
     * 이 구문이 오면 다음 구문과 합쳐서 전체를 한줄로 인식한다.
     * SQL등을 한줄로 읽어 파싱할때 사용된다.
     *  */
    private String lineSeperator;
    
    public void setLineSeperator(String lineSeperator) { this.lineSeperator = lineSeperator;}

    /**
     * 디폴트로  UTF_8을 사용한다.
     */
    public void read(File file, StringCallback callback){
        read(file,callback,CharSets.UTF_8);
    }
    
    public void read(File file, StringCallback callback,String encoding) {
        InputStreamReader isr = null;
        BufferedReader br = null;
        try {
            //reader = new BufferedReader(new FileReader(path)); => 이거는 안씀?
            isr = new InputStreamReader(new FileInputStream(file), encoding);
            br = new BufferedReader(isr);
            String line = null;

            while ((line = br.readLine()) != null) {
                if(lineSeperator!=null){
                    line = line.trim();
                    if (line.length() == 0) continue;
                    while (line.endsWith("\\")) {
                        line = line.substring(0, line.length() - 1);
                        line += br.readLine().trim();
                    }
                }
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
    
    /** 
     * 간단한 스캐너 이다. root를 기준으로 모든 파일을 읽어 line을 반환한다.
     * text가 아니면 오류가 날듯..
     * */
    public static void scan(File folder,StringCallback callback){
        FolderIterator i =  new FolderIterator(folder);
        while(i.hasNext()){
            File file = i.next();
            if(!file.isFile()) continue;
            new TextFileReader().read(file,callback);
        }
    }
    
}