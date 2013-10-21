package erwins.util.lib;

import java.io.File;
import java.io.FileFilter;
import java.util.Iterator;
import java.util.List;

import org.springframework.util.AntPathMatcher;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

/** 디렉토리 전체 파일을 가져온다.
 * ex) FileList list = new FileList(new File("C:/DATA/game"),new AntPathMatchFilePathFilter("**\\/*.zip"));  */
public class FileList implements Iterable<File>{
	
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
		
		private AntPathMatchFilePathFilter(String antPath) {
			this.antPath = antPath;
			Preconditions.checkArgument(antPathMatcher.isPattern(antPath),"invalid antPath");
		}

		@Override
		public boolean accept(File pathname) {
			if(pathname.isDirectory()) return true;
			return antPathMatcher.match(antPath, pathname.getAbsolutePath());
		}
	}
	
	/** 편의상 공개함. */
	public final List<File> files = Lists.newArrayList();
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
	
	@Override
	public Iterator<File> iterator() {
		return files.iterator();
	}
	
	
}
