package erwins.swtUtil.lib;

import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;

public abstract class LayoutUtil{
	
	/** 앞뒤로 다 늘어난다. */
	public static final GridData FULL = new GridData();
	
	static{
		FULL.horizontalAlignment = GridData.FILL;
		FULL.grabExcessHorizontalSpace = true;
		FULL.verticalAlignment = GridData.FILL;
		FULL.grabExcessVerticalSpace = true;
	}
	
	/** 너비는 100% 높이는 지정. */
	public static GridData hBox(int height){
		GridData gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		gridData.heightHint = height;
		return gridData;
	}
	
	public static GridLayout container(int column){
		GridLayout layout = new GridLayout();
		layout.horizontalSpacing = 5;
		layout.verticalSpacing = 5;
		layout.numColumns = column;
		return layout;
	}
	
}
