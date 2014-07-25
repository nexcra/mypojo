package erwins.util.collections;

import java.io.File;
import java.io.FileFilter;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import org.springframework.util.AntPathMatcher;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

/** 디렉토리 전체 파일을 가져온다.
 * ex) FileList list = new FileList(new File("C:/DATA/game"),new AntPathMatchFilePathFilter("**\\/*.zip"));  */
public class FileList implements List<File>{
	
	private final List<File> files = Lists.newArrayList();
	
	public static final FileFilter NOT_FILTER = new FileFilter(){
		@Override
		public boolean accept(File pathname) {
			return true;
		}
	};
	
	/** 디렉토리는 모두 true이다 주의! */
	public static class AntPathMatchFilePathFilter implements FileFilter{
		private final AntPathMatcher antPathMatcher = new AntPathMatcher();
		private final String antPath;
		/** 디렉토리 포함여부 */
		private boolean directory  = true;
		
		/** 여기서의 antPath는 루트부터이다. */
		public AntPathMatchFilePathFilter(String antPath) {
			this.antPath = antPath;
			Preconditions.checkArgument(antPathMatcher.isPattern(antPath),"invalid antPath");
		}

		@Override
		public boolean accept(File pathname) {
			if(pathname.isDirectory()) return directory;
			return antPathMatcher.match(antPath, pathname.getAbsolutePath());
		}
		public AntPathMatchFilePathFilter setDirectory(boolean directory) {
			this.directory = directory;
			return this;
		}
		
	}
	
	private FileFilter fileFilter = NOT_FILTER;
	
	public FileList(File dir,FileFilter fileFilter){
		this.fileFilter = fileFilter;
		listFiles(dir,fileFilter,files);
	}
	public FileList(File dir){
		listFiles(dir,fileFilter,files);
	}
	
	public static void listFiles(File dir,FileFilter fileFilter,List<File> files){
		Preconditions.checkArgument(dir.isDirectory());
		File[] list  = dir.listFiles(fileFilter);
		for(File each : list){
			if(each.isFile()) files.add(each);
			else if(each.isDirectory()){
				listFiles(each,fileFilter,files);
			}
		}
	}
	
	// ============== 이하 위임==============
	
	public int size() {
		return files.size();
	}
	public boolean isEmpty() {
		return files.isEmpty();
	}
	public boolean contains(Object o) {
		return files.contains(o);
	}
	public Iterator<File> iterator() {
		return files.iterator();
	}
	public Object[] toArray() {
		return files.toArray();
	}
	public <T> T[] toArray(T[] a) {
		return files.toArray(a);
	}
	public boolean add(File e) {
		return files.add(e);
	}
	public boolean remove(Object o) {
		return files.remove(o);
	}
	public boolean containsAll(Collection<?> c) {
		return files.containsAll(c);
	}
	public boolean addAll(Collection<? extends File> c) {
		return files.addAll(c);
	}
	public boolean addAll(int index, Collection<? extends File> c) {
		return files.addAll(index, c);
	}
	public boolean removeAll(Collection<?> c) {
		return files.removeAll(c);
	}
	public boolean retainAll(Collection<?> c) {
		return files.retainAll(c);
	}
	public void clear() {
		files.clear();
	}
	public boolean equals(Object o) {
		return files.equals(o);
	}
	public int hashCode() {
		return files.hashCode();
	}
	public File get(int index) {
		return files.get(index);
	}
	public File set(int index, File element) {
		return files.set(index, element);
	}
	public void add(int index, File element) {
		files.add(index, element);
	}
	public File remove(int index) {
		return files.remove(index);
	}
	public int indexOf(Object o) {
		return files.indexOf(o);
	}
	public int lastIndexOf(Object o) {
		return files.lastIndexOf(o);
	}
	public ListIterator<File> listIterator() {
		return files.listIterator();
	}
	public ListIterator<File> listIterator(int index) {
		return files.listIterator(index);
	}
	public List<File> subList(int fromIndex, int toIndex) {
		return files.subList(fromIndex, toIndex);
	}
	
}
