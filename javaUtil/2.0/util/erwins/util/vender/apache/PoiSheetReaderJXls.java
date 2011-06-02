
package erwins.util.vender.apache;

import java.util.Iterator;

import jxl.Cell;
import jxl.Sheet;

public class PoiSheetReaderJXls implements Iterable<String[]>{
    
	private Sheet sheet;
    
    public PoiSheetReaderJXls(Sheet sheet){
    	this.sheet = sheet;
    }
    
    public String getSheetName(){
    	return sheet.getName();
    }
    
	@Override
	public Iterator<String[]> iterator() {
		final int max = sheet.getRows();
		Iterator<String[]> iterator = new Iterator<String[]>() {
			int current = 0;
			@Override
			public boolean hasNext() {
				return current < max;
			}
			@Override
			public String[] next() {
				Cell[] cells = sheet.getRow(current);
				int size = cells.length;
	    		String[] line = new String[size]; 
	    		for(int i=0;i<size;i++){
	    			line[i] = cells[i].getContents();
	    		}
	    		current++;
				return line;
			}
			@Override
			public void remove() {
				throw new UnsupportedOperationException();
			}
		};
		return iterator;
	}    
}
