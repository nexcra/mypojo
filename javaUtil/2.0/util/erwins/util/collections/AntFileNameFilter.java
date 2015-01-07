package erwins.util.collections;

import java.io.File;
import java.io.FileFilter;

import org.springframework.util.AntPathMatcher;

import com.google.common.base.Preconditions;

/** 
 * 디렉토리는 모두 true이다 주의!
 * 풀패스가 아니라 이름으로 조회한다. 이게 더 나은듯.
 *  */
public class AntFileNameFilter implements FileFilter{
	
	private final AntPathMatcher antPathMatcher = new AntPathMatcher();
	private final String antPath;
	
	/** 디렉토리 포함여부 */
	private boolean directory  = true;
	
	/** 여기서의 antPath는 루트부터이다. */
	public AntFileNameFilter(String antPath) {
		this.antPath = antPath;
		Preconditions.checkArgument(antPathMatcher.isPattern(antPath),"invalid antPath");
	}

	@Override
	public boolean accept(File pathname) {
		if(pathname.isDirectory()) return directory;
		return antPathMatcher.match(antPath, pathname.getName());
	}
	public AntFileNameFilter setDirectory(boolean directory) {
		this.directory = directory;
		return this;
	}
		
	
	
}
