
package erwins.util.vender.apache;

import groovy.lang.Closure;

import java.util.Iterator;
import java.util.Map;

import org.apache.commons.collections.map.ListOrderedMap;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;

/**
 * POI 패키지의 HSSF를 편리하게.. 헤더칸은 1칸 이라고 일단 고정 사각 박스를 예쁘게 채울려면 반드시 null에 ""를 채워 주자~
 * @author  erwins(my.pojo@gmail.com)
 */
public class PoiSheetReader extends PoiSheetReaderRoot implements Iterable<String[]>{
    
	protected XSSFSheet sheet;
    
    public PoiSheetReader(XSSFSheet sheet){
    	this.sheet = sheet;
    }
    
    public String getSheetName(){
    	return sheet.getSheetName();
    }
	
    /** 
     * 시트 이름이 XSSFSheet객체에 있는게 아니라 WB에 있다. ㅅㅂ.
     * 모든 셀이 빈공간이라면 여백으로 간주하고 스킵한다.
     * */
    public void read(StringArrayPoiCallback callback){
    	Iterator<Row> rows = sheet.iterator();
    	readEach(callback, rows);
    }
    
    public void read(final Closure init){
    	read(null,init);
    }
    
    /** Groovy용
     * Closure를 사용함으로 조낸 느리긴 하다.   */
    @SuppressWarnings(value={ "unchecked"})
    public void read(final Closure init,final Closure callback){
    	boolean first = true;
    	String[] column = null;
    	int columnLength = 0;
    	
    	Iterator<String[]> iterator =  iterator();
    	while(iterator.hasNext()){
    		String[] line = iterator.next();
    		if(first){
    			column = new String[line.length];
    			for(int i=0;i<line.length;i++) column[i] = line[i]==null ? "" : line[i]; 
    			first = false;
    			if(init!=null) init.call(new Object[]{column});
    			columnLength = column.length;
    			continue;
    		}
    		int lineLength =  line.length; //길이가 줄어들 수 있다. 
    		if(columnLength < lineLength)  lineLength = columnLength; //컬럼보다 더 길게 들어온 데이터는 무시한다.
    		Map<Object,String> result = new ListOrderedMap();
    		for(int i=0;i<lineLength;i++){
    			result.put(column[i],line[i] == null ? "" : line[i].trim());
    		}
    		callback.call(result);
    	}
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
