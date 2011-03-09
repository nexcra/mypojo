
package erwins.util.vender.apache;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

import erwins.util.lib.StringUtil;

/**
 * POI 패키지의 HSSF를 편리하게.. 헤더칸은 1칸 이라고 일단 고정 사각 박스를 예쁘게 채울려면 반드시 null에 ""를 채워 주자~
 * @author  erwins(my.pojo@gmail.com)
 */
public abstract class PoiSheetReaderRoot{
    
    public static interface StringArrayPoiCallback{
    	public void readRow(String[] line);
    }
    
    /** 배당은 항상 작은애 기준이다. 즉 컬럼이 작으면 라인이 더 들어와도 짤리고, 라인이 적으면 컬럼이 많아도 읽지 않는다. */
    public static abstract class StringMapPoiCallback implements StringArrayPoiCallback{
    	private boolean first = true;
    	private String[] column = null;
		@Override
		public void readRow(String[] line) {
			if(first){
				column = new String[line.length];
				boolean camelize = camelize();
				for(int i=0;i<line.length;i++) column[i] =  camelize ? StringUtil.getCamelize(line[i]) : line[i];
				first = false;
				return;
			}
			Map<String,String> result = new HashMap<String,String>();
			int size = column.length < line.length ? column.length : line.length;
			for(int i=0;i<size;i++){
				result.put(column[i],line[i]==null ? null : line[i].trim());
			}
			process(result);
		}
		protected abstract void process(Map<String,String> line);
		protected boolean camelize(){return false;};
    }
    
    /** 배당은 항상 작은애 기준이다. 즉 컬럼이 작으면 라인이 더 들어와도 짤리고, 라인이 적으면 컬럼이 많아도 읽지 않는다. */
    public static abstract class StringColimnPoiCallback implements StringArrayPoiCallback{
    	private boolean first = true;
    	protected String[] column = null;
    	@Override
    	public void readRow(String[] line) {
    		if(first){
    			column = new String[line.length];
    			boolean camelize = camelize();
    			for(int i=0;i<line.length;i++) column[i] =  camelize ? StringUtil.getCamelize(line[i]) : line[i];
    			first = false;
    			return;
    		}
    		readColimn(line);
    	}
    	protected abstract void readColimn(String[] line);
    	protected boolean camelize(){return false;};
    }
    
    /** Iterator<Row> rows = sheet.iterator(); 요렇게 구현하주자. */
	protected void readEach(StringArrayPoiCallback callback, Iterator<Row> rows) {
		while(rows.hasNext()){
    		Row eachRow = rows.next();
    		String[] line = new String[eachRow.getLastCellNum()]; 
    		Iterator<Cell> cells = eachRow.iterator();
    		while(cells.hasNext()){
    			Cell eachCell = cells.next();
    			line[eachCell.getColumnIndex()] = toString(eachCell);
    		}
    		if(!isEmpty(line)) callback.readRow(line);
    	}
	}
    
    /** 전체가 빈 배열인지? */
    private static boolean isEmpty(String[] line) {
    	for(String each : line) if(!each.equals("")) return false;
    	return true; 
    }

  
    /**
     * CELL_TYPE_NUMERIC의 경우 double임으로 2 => 2.0 이런식으로 바뀐다.
     * BigDecimal로 변경함으로 성능 문제시 교체하자.
     */
    private static String toString(Cell cell) {
        if (cell == null) return "";
        switch (cell.getCellType()) {
            case Cell.CELL_TYPE_NUMERIC:
                return new BigDecimal(cell.getNumericCellValue()).toString();
            default:
            	return cell.getRichStringCellValue().getString().trim();
        }
    }
    
}
