
package erwins.test.runAndTakeResult;

import java.io.File;

import org.junit.Test;

import erwins.util.tools.DuplicatedFileFilter;
import erwins.util.tools.LocalFileControll;

/** 로컬 파일을 다룬다. */
public class File_Local{
	
	/** 중복 파일 삭제. */
	@Test
    public void deleteDuplicatedFile() throws Exception {
		DuplicatedFileFilter item = new DuplicatedFileFilter();
		//item.add("D:/DATA/이미지/코스사진").add("D:/_temp/이미지test/코스사진").remove();
		item.add("C:/KorMent/workspace/korment-www/www/common").log(new File("D:/중복.txt"));
    }
	
	public void moveFile() throws Exception {
		LocalFileControll item = new LocalFileControll();
		item.add("D:/_temp/제안서").add("D:/_temp/제안요약서").moveToSeparatedDirectory("D:/_temp/sourceCode");
	}
    
}