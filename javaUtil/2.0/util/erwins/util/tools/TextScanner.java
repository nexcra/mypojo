
package erwins.util.tools;

import java.io.File;
import java.io.FileFilter;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import erwins.util.lib.StringUtil;
import erwins.util.reflexive.FolderIterator;
import erwins.util.root.StringCallback;

/** 
 * 간이!! 스캐너 이다. 
 * 프로그램 내부로직에 사용하지 말것!
 * -> 병맛난다.. Groovy가 있으니 필요없음
 * */
public class TextScanner{
    private Log log = LogFactory.getLog(this.getClass());
    public TextScanner(String key){
        this.key = key;
    }
    private FileFilter filter;
    private String key;
    private int fileCount;
    private int matchCount;
    private List<String> result = new ArrayList<String>();
    
    public TextScanner visit(String file) {
        return visit(new File(file));
    }
    
    public TextScanner visit(File folder) {
        fileCount = 0;
        matchCount = 0;
        
        FolderIterator i;
        if(filter==null) i= new FolderIterator(folder);
        else i= new FolderIterator(folder,filter);
        while(i.hasNext()){
            final File file = i.next();
            if(!file.isFile()) continue;
            
            final String ext = StringUtil.getExtention(file.getName());
            if(!StringUtil.isEqualsIgnoreCase(ext, "java")) continue;
            if(StringUtil.isMatchIgnoreCase(file.getName(),"test")) continue;
            
            fileCount++;
            
            new TextFileReader().read(file, new StringCallback(){
                public void process(String line){
                    if(StringUtil.contains(line, key)){
                        result.add("\""+key + "\" (" +file.getAbsolutePath() + ") => " + line);
                        matchCount++;
                    }
                }
            });
        }
        return this;
    }

    
    public void printConsole(){
        for(String str : result) log.info(str);
        log.info(MessageFormat.format("==== {0}로 검사한 결과. ====",key));
        log.info(MessageFormat.format("==== {0}개의 파일에서 {1}개의 사항이 검색되었습니다. ====", fileCount,matchCount));
    }
    
    public void validate(){
        for(String str : result) log.debug(str);
        log.debug(MessageFormat.format("==== {0}로 검사한 결과. ====",key));
        log.debug(MessageFormat.format("==== {0}개의 파일에서 {1}개의 사항이 검색되었습니다. ====", fileCount,matchCount));
        if(matchCount!=0) throw new RuntimeException("validate 실패.");
    }

	public TextScanner setFilter(FileFilter filter) {
		this.filter = filter;
		return this;
	}
    
}