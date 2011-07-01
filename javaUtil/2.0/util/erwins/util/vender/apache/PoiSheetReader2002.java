
package erwins.util.vender.apache;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.ss.usermodel.Row;

/**
 * POI 패키지의 HSSF를 편리하게.. 헤더칸은 1칸 이라고 일단 고정 사각 박스를 예쁘게 채울려면 반드시 null에 ""를 채워 주자~
 */
public class PoiSheetReader2002 extends PoiSheetReaderRoot{
    
	protected HSSFSheet sheet;
    
    public PoiSheetReader2002(HSSFSheet sheet){
    	this.sheet = sheet;
    }
    
    public String getSheetName(){
    	return sheet.getSheetName();
    }
    public void read(StringArrayPoiCallback callback){
    	Iterator<Row> rows = sheet.iterator();
    	readEach(callback, rows);
    }
    
    /** Groovy의 each {} 를 쓰기위한 메소드.  */
	@Override
	public Iterator<String[]> iterator() {
		final List<String[]> list = new ArrayList<String[]>();
		read(new StringArrayPoiCallback(){
			@Override
			public void readRow(String[] line) {
				list.add(line);
			}
		});
		return list.iterator();
	}
    
}
