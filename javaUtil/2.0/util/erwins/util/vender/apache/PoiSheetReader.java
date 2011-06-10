
package erwins.util.vender.apache;

import java.util.Iterator;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;

/**
 * POI 패키지의 HSSF를 편리하게.. 헤더칸은 1칸 이라고 일단 고정 사각 박스를 예쁘게 채울려면 반드시 null에 ""를 채워 주자~
 * 모든 셀이 빈공간이라면 여백으로 간주하고 스킵한다.
 * @author  erwins(my.pojo@gmail.com)
 */
public class PoiSheetReader extends PoiSheetReaderRoot{
    
	protected XSSFSheet sheet;
    
    public PoiSheetReader(XSSFSheet sheet){
    	this.sheet = sheet;
    }
    
    /** 
     * 시트 이름이 XSSFSheet객체에 있는게 아니라 WB에 있다. ㅅㅂ.
     * */
    public String getSheetName(){
    	return sheet.getSheetName();
    }
	
    /** 쓸모없다 */
    public void read(StringArrayPoiCallback callback){
    	Iterator<Row> rows = sheet.iterator();
    	readEach(callback, rows);
    }
    
	@Override
	public Iterator<String[]> iterator() {
		final Iterator<Row> rows = sheet.iterator();
		Iterator<String[]> iterator = new Iterator<String[]>() {
			@Override
			public boolean hasNext() {
				return rows.hasNext();
			}
			@Override
			public String[] next() {
				Row eachRow = rows.next();
	    		String[] line = new String[eachRow.getLastCellNum()]; 
	    		Iterator<Cell> cells = eachRow.iterator();
	    		while(cells.hasNext()){
	    			Cell eachCell = cells.next();
	    			line[eachCell.getColumnIndex()] = cellToString(eachCell);
	    		}
				return line;
			}
			@Override
			public void remove() {
				throw new UnsupportedOperationException();
			}
		};
		return iterator;
	}    

    /** Groovy의 each {} 를 쓰기위한 메소드.
     * 1000개씩 끊어읽어보자.  */
    /*
	@Override
	public Iterator<String[]> iterator() {
		Iterator<String[]> i = new Iterator<String[]>() {
			List<String[]> list = new LinkedList<String[]>();
			Iterator<String[]> iterator;
			public void re() {
				list.clear();
				read(new StringArrayPoiCallback(){
					@Override
					public void readRow(String[] line) {
						list.add(line);
					}
				});
			}
			@Override
			public boolean hasNext() {
				// TODO Auto-generated method stub
				return false;
			}
			@Override
			public String[] next() {
				
				// TODO Auto-generated method stub
				return null;
			}
			@Override
			public void remove() {
				throw new UnsupportedOperationException();
			}
		};
		
		return i;
		//return list.iterator();
	}*/
    
}
