package erwins.swtUtil.lib;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

public abstract class TableUtil{

	public static void addColumn(Table table,String name,int width){
		TableColumn tableColumn = new TableColumn(table,SWT.RIGHT); 
		tableColumn.setText(name);
		tableColumn.pack();
		tableColumn.setWidth(width);
	}
	
}
