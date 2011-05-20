
package erwins.util.vender.apache;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * XSSF를 읽을때 별도의 jar가 마니 필요하다~
 * 나중에 이들을 인터페이스화 하자.
 */
public class PoiReader implements Iterable<PoiSheetReader>{
    
	protected XSSFWorkbook wb ;
    
    public PoiReader(String fileName){
    	load(new File(fileName));
    }
    
    public PoiReader(File file){
    	load(file);
    }

	private void load(File file) {
		FileInputStream stream = null;
    	try {
    		stream = new FileInputStream(file);
            wb = new XSSFWorkbook(stream);
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
	
    public PoiSheetReader getAt(int i) {
    	return new PoiSheetReader(wb.getSheetAt(i));
    }

    /** 시트는 1부터 시작한다. */
	@Override
	public Iterator<PoiSheetReader> iterator() {
		List<PoiSheetReader> sheets = new ArrayList<PoiSheetReader>();
		for(int i=0;i<wb.getNumberOfSheets();i++){
			sheets.add(new PoiSheetReader(wb.getSheetAt(i)));
		}
		return sheets.iterator();
	}
    
}
