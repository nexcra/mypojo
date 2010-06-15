
package erwins.util.tools;

import java.io.File;
import java.util.Iterator;

import erwins.util.lib.Files;
import erwins.util.root.StringCallback;

/**
 * 머 필요한거 있으면 추가하자.
 */
public class TextFileUtil{
    
	/** directory내의 텍스트 내용물을 합친다. */
    public static void gather(File directory,File out,String encode){
		Iterator<File> i = Files.iterateFiles(directory);
        gather(i,out,encode);
    }
    public static void gather(Iterator<File> i,File out,String encode){
    	final StringBuilder b = new StringBuilder(); 
    	while(i.hasNext()){
    		File each = i.next();
    		new TextFileReader().read(each,new StringCallback() {
    			@Override
    			public void process(String line) {
    				b.append(line);
    				b.append("\r\n");
    			}
    		},encode);
    	}
    	Files.writeStr(b,out);
    }
    
    
    
}