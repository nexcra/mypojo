
package erwins.util.vender.apache;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFSheet;

/**
 * POI 패키지의 HSSF를 편리하게.. 헤더칸은 1칸 이라고 일단 고정 사각 박스를 예쁘게 채울려면 반드시 null에 ""를 채워 주자~
 * 모든 셀이 빈공간이라면 여백으로 간주하고 스킵한다.
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
	
    /** 테스트용 
     * Cell c = pr[0].getCell(4,7)
		println c.getStringCellValue()
		CellStyle s = c.getCellStyle() 
		println s.getFillBackgroundColor()*/
    public Cell getCell(int r,int c){
    	return sheet.getRow(r).getCell(c);
    }
    
    /** 머지한 부분은 최초 좌상단 데이터 말고는 다 널로 들어간다.
     * 따라서 이 메소드로 머지한부분에도 값을 채워준다.*/
    public List<String[]> readAsMerge(){
    	Iterator<String[]> rows = iterator();
    	List<String[]> list = new ArrayList<String[]>();
    	while(rows.hasNext()) list.add(rows.next());
    	int length = sheet.getNumMergedRegions();
    	for(int i=0;i<length;i++){
    		CellRangeAddress ad =  sheet.getMergedRegion(i);
    		String data = null;
    		for(int colNum=ad.getFirstColumn(); colNum<ad.getLastColumn()+1; colNum++){
        		for(int rowNum=ad.getFirstRow(); rowNum<ad.getLastRow()+1; rowNum++){
        			if(data==null) data =  list.get(rowNum)[colNum];
        			else{
        				list.get(rowNum)[colNum] = data;
        			}
        		}
    		}
    	}
    	return list;
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
				int cellNum = eachRow.getLastCellNum();
				if(cellNum == -1) return new String[0]; //빈라인이면 -1이 오는듯
	    		String[] line = new String[cellNum]; 
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
