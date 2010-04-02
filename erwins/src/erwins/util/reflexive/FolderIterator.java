
package erwins.util.reflexive;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


/** 전체 트리구조를 따라 File을 리턴한다. Files와 다른 점은 중간 디렉토리도 포함한다는 점이다. =>필요가 거의 없는듯. */
//@Deprecated
public class FolderIterator implements Iterator<File>{
	
	private static final FileFilter ALL_PASS = new FileFilter(){
		@Override
		public boolean accept(File pathname) {
			return true;
		}
	};
    
    private int index = 0;
    private final int max; 
    private List<File> files = new ArrayList<File>();

    public FolderIterator(File folder){
        reflexiveVisit(folder,ALL_PASS);
        max = files.size();
    }
    
    public FolderIterator(String folder){
        reflexiveVisit(new File(folder),ALL_PASS);
        max = files.size();
    }
    
    public FolderIterator(File folder,FileFilter filter){
    	reflexiveVisit(folder,filter);
        max = files.size();
    }
    
    private void reflexiveVisit(final File file,FileFilter filter) {
        if(file.isHidden()) return;
        files.add(file);
        if(file.isDirectory()){
            for(File each:file.listFiles(filter)){
                reflexiveVisit(each,filter);
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
        throw new UnsupportedOperationException();
    }

}   
