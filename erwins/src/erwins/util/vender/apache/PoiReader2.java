
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
 */
public class PoiReader2 implements Iterable<PoiSheetReader2>{
    
	protected XSSFWorkbook wb ;
    
    public PoiReader2(String fileName){
    	load(new File(fileName));
    }
    
    public PoiReader2(File file){
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

    public PoiSheetReader2 get(int i) {
    	return new PoiSheetReader2(wb.getSheetAt(i));
    }

    /** 시트는 1부터 시작한다. */
	@Override
	public Iterator<PoiSheetReader2> iterator() {
		List<PoiSheetReader2> sheets = new ArrayList<PoiSheetReader2>();
		for(int i=0;i<wb.getNumberOfSheets();i++){
			sheets.add(new PoiSheetReader2(wb.getSheetAt(i)));
		}
		return sheets.iterator();
	}
    
}
