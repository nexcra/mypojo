
package erwins.util.vender.apache;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

/**
 * POI 패키지의 HSSF를 편리하게.. 헤더칸은 1칸 이라고 일단 고정 사각 박스를 예쁘게 채울려면 반드시 null에 ""를 채워 주자~
 */
public class PoiReader2002 implements Iterable<PoiSheetReader2002>{
    
	protected HSSFWorkbook wb ;
    
    public PoiReader2002(String fileName){
    	load(new File(fileName));
    }
    
    public PoiReader2002(File file){
    	load(file);
    }

	private void load(File file) {
		FileInputStream stream = null;
    	try {
    		stream = new FileInputStream(file);
            POIFSFileSystem filesystem = new POIFSFileSystem(stream);        
            wb = new HSSFWorkbook(filesystem);
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }finally{
        	try {
				if(stream!=null) stream.close();
			} catch (IOException e) {
			}
        }
	}

    public PoiSheetReader2002 getAt(int i) {
    	return new PoiSheetReader2002(wb.getSheetAt(i));
    }

    /** 시트는 1부터 시작한다. */
	@Override
	public Iterator<PoiSheetReader2002> iterator() {
		List<PoiSheetReader2002> sheets = new ArrayList<PoiSheetReader2002>();
		for(int i=0;i<wb.getNumberOfSheets();i++){
			sheets.add(new PoiSheetReader2002(wb.getSheetAt(i)));
		}
		return sheets.iterator();
	}
    
}
