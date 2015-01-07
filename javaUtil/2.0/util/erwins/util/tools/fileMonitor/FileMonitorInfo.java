package erwins.util.tools.fileMonitor;

import java.io.File;
import java.io.FileFilter;

import lombok.Data;

import com.google.common.base.Preconditions;

import erwins.util.collections.AntFileNameFilter;

/** 읽을 파일의 위치화 어떻게 처리할지의 설정파일 */
@Data
public class FileMonitorInfo{
	
	private final File directory;
	private FileFilter fileFilter;
	private FileMonitorCallback fileMonitorCallback;
	
	public FileMonitorInfo(File directory){
		this.directory = directory;
		if(!directory.isDirectory()) directory.mkdirs();
		Preconditions.checkState(directory.isDirectory(),"디렉토리가 올바르지 않습니다." + directory.getAbsolutePath());
	}
	
	/** 
	 * 파일 이름으로만 체크한다. 풀패스 아님
	 * */
	public static FileMonitorInfo createForAntPath(File directory,String antPath){
		FileMonitorInfo vo = new FileMonitorInfo(directory);
		vo.fileFilter = new AntFileNameFilter(antPath).setDirectory(false);
		return vo;
	}

}
