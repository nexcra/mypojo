
package erwins.util.vender.apache;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;

/**
 * 포이가 무거워서 만들었다. 이것도 좀 후잡하긴 함.
 */
public class PoiReaderJXls implements Iterable<PoiSheetReaderJXls>{
    
	private Workbook workbook;
    
    public PoiReaderJXls(String fileName){
    	load(new File(fileName));
    }
    
    public PoiReaderJXls(File file){
    	load(file);
    }

	private void load(File file) {
		try {
			workbook = Workbook.getWorkbook(file);
		} catch (BiffException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
    public PoiSheetReaderJXls getAt(int i) {
    	return new PoiSheetReaderJXls(workbook.getSheet(i));
    }

    /** 시트는 1부터 시작한다. */
	@Override
	public Iterator<PoiSheetReaderJXls> iterator() {
		List<PoiSheetReaderJXls> sheets = new ArrayList<PoiSheetReaderJXls>();
		for(Sheet each : workbook.getSheets()){
			sheets.add(new PoiSheetReaderJXls(each));
		}
		return sheets.iterator();
	}
    
}
