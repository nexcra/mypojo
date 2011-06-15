
package erwins.test.runAndTakeResult;

import erwins.util.tools.LocalFileControll;

/** 로컬 파일을 다룬다. */
public class File_Local{
	
	public void moveFile() throws Exception {
		LocalFileControll item = new LocalFileControll();
		item.add("D:/_temp/제안서").add("D:/_temp/제안요약서").moveToSeparatedDirectory("D:/_temp/sourceCode");
	}
    
}