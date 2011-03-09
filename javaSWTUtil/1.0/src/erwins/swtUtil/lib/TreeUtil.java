package erwins.swtUtil.lib;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

public abstract class TreeUtil{

	public static void clearAndAddItem(Tree tree,String name){
		tree.removeAll();
		TreeItem item = new TreeItem(tree,SWT.NONE);
		item.setText(name);
	}
	
}
