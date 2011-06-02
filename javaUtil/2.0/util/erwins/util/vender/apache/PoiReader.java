
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
 * Groovy가 주가되니깐 인터페이스화 안해도 될듯.
 * 메모리 1G 기준  10M만 되도 읽기 힘들다.. ㅅㅂ  30M는 못읽음. ->  jxls이 그나마 조금 더 가볍다.
 * ---> 대용량은 자동생성이 아닌 이상 txt나 csv로 변환해서 읽자.
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
    		//wb = new XSSFWorkbook(OPCPackage.open(stream));
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
