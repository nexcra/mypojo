
package erwins.util.vender.apache;

import java.util.Iterator;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.ss.usermodel.Row;

/**
 * POI 패키지의 HSSF를 편리하게.. 헤더칸은 1칸 이라고 일단 고정 사각 박스를 예쁘게 채울려면 반드시 null에 ""를 채워 주자~
 * @author  erwins(my.pojo@gmail.com)
 */
public class PoiSheetReader2002 extends PoiSheetReaderRoot{
    
	protected HSSFSheet sheet;
    
    public PoiSheetReader2002(HSSFSheet sheet){
    	this.sheet = sheet;
    }
    
    public String getSheetName(){
    	return sheet.getSheetName();
    }
	
    /** 
     * 시트 이름이 HSSFSheet객체에 있는게 아니라 WB에 있다. ㅅㅂ.
     * 모든 셀이 빈공간이라면 여백으로 간주하고 스킵한다.
     * */
    public void read(StringArrayPoiCallback callback){
    	Iterator<Row> rows = sheet.iterator();
    	readEach(callback, rows);
    }
    
}
