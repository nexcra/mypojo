
package erwins.util.reflexive;

import java.io.File;
import java.util.*;

import erwins.util.exception.NotSupportedMethodException;


/** 전체 트리구조를 따라 File을 리턴한다. */
public class FolderIterator implements Iterator<File>{
    
    private int index = 0;
    private final int max; 
    private List<File> files = new ArrayList<File>();

    public FolderIterator(File folder){
        reflexiveVisit(folder);
        max = files.size();
    }
    
    public FolderIterator(String folder){
        reflexiveVisit(new File(folder));
        max = files.size();
    }
    
    private void reflexiveVisit(final File file) {
        if(file.isHidden()) return;
        files.add(file);
        if(file.isDirectory()){
            for(File each:file.listFiles()){
                reflexiveVisit(each);
            }
        }
    }

    public boolean hasNext() {
        return index != max;
    }

    public File next() {
        return files.get(index++);
    }

    public void remove() {
        throw new NotSupportedMethodException();
    }

}   
