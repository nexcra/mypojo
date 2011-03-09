package erwins.swtUtil.lib;

import java.util.Collection;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

import erwins.util.reflexive.Connectable;

@SuppressWarnings("unchecked")
public class TreeItemGenerator<T extends Connectable> extends AbstractGenerator{
	
	private final Tree tree;
	
	public TreeItemGenerator(Tree tree){
		this.tree = tree;
	}

	public void generate(Collection<T> root){
		for(T each : root){
			TreeItem item = new TreeItem(tree,SWT.NONE);
			reflexiveItem(each, item);
		}
	}
	
	private void getTreeItem(TreeItem parent,Collection<T> root){
		for(T each : root){
			TreeItem item = new TreeItem(parent,SWT.NONE);
			reflexiveItem(each, item);
		}
	}

	private void reflexiveItem(T each, TreeItem item) {
		writeDefaultItem(each, item);
		getTreeItem(item,each.getChildren());
	}
	
}
