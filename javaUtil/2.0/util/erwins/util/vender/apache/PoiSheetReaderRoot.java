
package erwins.util.vender.apache;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.map.ListOrderedMap;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;

import erwins.util.lib.StringUtil;
import groovy.lang.Closure;

/**
 * POI 패키지의 HSSF를 편리하게.. 헤더칸은 1칸 이라고 일단 고정 사각 박스를 예쁘게 채울려면 반드시 null에 ""를 채워 주자~
 */
public abstract class PoiSheetReaderRoot implements Iterable<String[]>{
    
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
    		int size = eachRow.getLastCellNum();
    		if(size==-1) continue;
    		String[] line = new String[size]; 
    		Iterator<Cell> cells = eachRow.iterator();
    		while(cells.hasNext()){
    			Cell eachCell = cells.next();
    			line[eachCell.getColumnIndex()] = cellToString(eachCell);
    		}
    		if(!isEmpty(line)) callback.readRow(line);
    	}
	}
	
    /** 전체가 빈 배열인지? */
    private static boolean isEmpty(String[] line) {
    	for(String each : line) if( !StringUtil.isEmpty(each)) return false;
    	return true; 
    }
  
    /**
     * CELL_TYPE_NUMERIC의 경우 double임으로 2 => 2.0 이런식으로 바뀐다.
     * BigDecimal로 변경함으로 성능 문제시 교체하자.
     * DateUtil 의 is~~ 시리즈가 완벽하게 작동하지 않는다. 따라서 안되는 부분은 DateUtil.getJavaDate(cell.getNumericCellValue() 를 사용
     * DateUtil -> XSSF에서도 이게 통하는지는 의문.. . 걍 time을 일단은 문자로 넘겨준다.
     * 나증에 Object 로 이동하도록 변경하자
     * 
     * ... 왜 else return new BigDecimal(cell.getNumericCellValue()).toString(); 일케했을까? ㅠㅠ 일단 변경
     *   --> 24.7 일케 더블이 들어오면 24.6999999999 일케 바껴벼린다.
     */
    protected String cellToString(Cell cell) {
        if (cell == null) return "";
        try {
			switch (cell.getCellType()) {
			    case Cell.CELL_TYPE_NUMERIC:
			    	if(DateUtil.isCellDateFormatted(cell)) return String.valueOf(cell.getDateCellValue().getTime());
			    	//else return new BigDecimal(cell.getNumericCellValue()).toString();
			    	else{
			    		String strValue = String.valueOf(cell.getNumericCellValue());
			    		if(StringUtil.contains(strValue, 'E')) strValue = new BigDecimal(strValue).toPlainString(); //3.00004032E8 이런거 방지
			    		return strValue;
			    	}
			    default:
			    	return cell.getRichStringCellValue().getString().trim();
			}
		} catch (IllegalStateException e) {
			return e.getMessage() + ""; //null방지
		}
    }
    
    @SuppressWarnings("rawtypes")
	public void read(final Closure init){
    	read(null,init);
    }
    
    /** Groovy용
     * Closure를 사용함으로 조낸 느리긴 하다.   */
    @SuppressWarnings(value={ "unchecked","rawtypes"})
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
    
    public List<String[]> list(){
    	List<String[]> list = new ArrayList<String[]>();
    	Iterator<String[]> iterator =  iterator();
    	while(iterator.hasNext()){
    		list.add(iterator.next());
    	}
    	return list;
    }
    
    @SuppressWarnings({ "rawtypes", "unchecked" })
	public List read(){
    	List list = new ArrayList();
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
    			columnLength = column.length;
    			continue;
    		}
    		int lineLength =  line.length; //길이가 줄어들 수 있다. 
    		if(columnLength < lineLength)  lineLength = columnLength; //컬럼보다 더 길게 들어온 데이터는 무시한다.
    		Map<Object,String> result = new ListOrderedMap();
    		for(int i=0;i<lineLength;i++){
    			result.put(column[i],line[i] == null ? "" : line[i].trim());
    		}
    		list.add(result);
    	}
    	return list;
    }
    
}
