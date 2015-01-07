package erwins.util.tools.fileMonitor;

import java.io.File;

/** 예외가 발생하지않게 잘 조절할것 */
public interface FileMonitorCallback{
	
	public void doFileCallback(File file);

}
