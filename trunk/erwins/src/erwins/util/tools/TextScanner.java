
package erwins.util.tools;

import java.io.File;
import java.text.MessageFormat;
import java.util.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import erwins.util.lib.Strings;

/**
 * TextScanner interface Validator(Command패턴)를 만들려다가.. 실효성이 없어보여서 취소.
 */
public class TextScanner{
    
    private Log log = LogFactory.getLog(this.getClass());

    /**
     * @uml.property  name="r"
     * @uml.associationEnd  
     */
    private TextReader r;
    private int problemCnt;
    private int validateFileCnt;
    private File nowFile;
    private File root;
    private List<String> keys = Collections.emptyList();
    private List<String> result = Collections.emptyList();

    public TextScanner(File root){
        this.root = root;
        init();
    }
    
    public void init(){
        keys = new ArrayList<String>();
        result = new ArrayList<String>();
    }    
    
    /** 
     * 금지된 단어가 있는지 체크한다. 
     * Framework내의 참조 검사.
     * System.out.println 검사.
     * DAO 및 Service가 적절한 위치인지 검사.
     **/
    public TextScanner addKey(String prohibition) {
        keys.add(prohibition);
        return this;
    }
    
    /**
     * 이런 류의 메소드를 추후 추가하자. 
     */
    private void searchKey(String line) {
        for(String each : keys){
            if(Strings.contains(line, each)){
                result.add("\""+each + "\" (" +nowFile.getName() + ") => " + line);
                problemCnt++;
            }
        }
    }

    public List<String> validate(){
        try {
            loop(root);
        }
        catch (Exception e) {
            r.close();
            throw new RuntimeException(e.getMessage(),e);
        }
        
        result.add(MessageFormat.format("==== {0}로 검사한 결과. ====",Strings.join(keys, ",")));
        result.add(MessageFormat.format("==== {0}개의 파일에서 {1}개의 사항이 검색되었습니다. ====", validateFileCnt,problemCnt));
        List<String> temp = result;
        init();
        return temp;
    }
    
    /**
     * 결과를 로그로 출력한다.  isInfo이면 info레벨로 출력한다.
     */
    public void validate(boolean isInfo){
        String result = Strings.join(validate(),"\n") + "\n";
        if(isInfo) log.info(result);
        else log.debug(result);
    }

    private void loop(File parent) {
        String line;
        for(File each:parent.listFiles()){
            nowFile = each;
            if(nowFile.isHidden()) continue;
            if(nowFile.isDirectory()) loop(nowFile);
            String ext = Strings.getExtention(nowFile.getName());
            if(!Strings.isEqualsIgnoreCase(ext, "java","txt")) continue;
            validateFileCnt++;
            r = new TextReader(nowFile);
            while((line = r.next()) != null){
                searchKey(line);
            }
            r.close();
        }
    }

  
    
    
   
    

}