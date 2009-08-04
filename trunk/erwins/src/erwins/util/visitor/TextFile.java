
package erwins.util.visitor;

import java.io.File;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import erwins.util.lib.Strings;
import erwins.util.tools.TextFileReader;
import erwins.util.tools.TextFileReader.LineCallback;
import erwins.util.visitor.Visitor.FileAcceptor;

/** 마음에 들지 않으면 FileAcceptor를 상속해서 새로 만들어 사용하도록 할것. */
public class TextFile extends FileAcceptor {
    public TextFile(File f) {
        super(f);
    }
    
    public void validate(String key) {
        TextFileVisitor v = new TextFileVisitor(key);
        if(!file.isDirectory()) throw new RuntimeException(file+" is Not Directory ");
        this.accept(v);
        v.validate();
    }
    
    public static class TextFileVisitor implements Visitor<File>{
        private Log log = LogFactory.getLog(this.getClass());
        public TextFileVisitor(String key){
            this.key = key;
        }
        private String key;
        private int fileCount;
        private int matchCount;
        private List<String> result = new ArrayList<String>();
        
        public void visit(final File file) {
            if(file.isHidden()) return;
            if(file.isDirectory()){
                for(File each:file.listFiles()){
                    TextFile textFile = new TextFile(each);
                    textFile.accept(this);
                }
            }
            String ext = Strings.getExtention(file.getName());
            if(!Strings.isEqualsIgnoreCase(ext, "java","txt")) return;
            if(Strings.isMatchIgnoreCase(file.getName(), "test")) return;
            fileCount++;
            
            TextFileReader.read(file, new LineCallback(){
                public void process(String line) throws Exception{
                    if(Strings.contains(line, key)){
                        result.add("\""+key + "\" (" +file.getAbsolutePath() + ") => " + line);
                        matchCount++;
                    }
                }
            });
        }
        
        public void validate(){
            for(String str : result) log.debug(str);
            log.debug(MessageFormat.format("==== {0}로 검사한 결과. ====",key));
            log.debug(MessageFormat.format("==== {0}개의 파일에서 {1}개의 사항이 검색되었습니다. ====", fileCount,matchCount));
            if(matchCount!=0) throw new RuntimeException("validate 실패.");
        }
    }
    
    
}