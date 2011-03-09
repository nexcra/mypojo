package erwins.swtUtil.lib;

import java.util.Collection;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;

import erwins.util.reflexive.Connectable;

@SuppressWarnings("unchecked")
public class MenuItemGenerator<T extends Connectable> extends AbstractGenerator{
	
	private final Shell shell;
	
	private int leafItem = SWT.PUSH;
	private Listener itemListener = null;
	
	public MenuItemGenerator(Shell shell){
		this.shell = shell;
	}

	public void generate(Collection<T> root){
		Menu bar = new Menu(shell,SWT.BAR | SWT.LEFT_TO_RIGHT);
		shell.setMenuBar(bar);
		for(T each : root){
			MenuItem item = new MenuItem(bar,SWT.CASCADE);
			reflexiveItem(each, item,false);
		}
	}
	
	private void getMenuItem(MenuItem parent,Collection<T> root){
		Menu fileSubmenu = new Menu(shell, SWT.DROP_DOWN);
		for(T each : root){
			boolean isLeaf = isLeaf(each);
			MenuItem item = new MenuItem(fileSubmenu, isLeaf ? leafItem : SWT.CASCADE);
			reflexiveItem(each, item,isLeaf);
		}
		parent.setMenu(fileSubmenu);
	}

	private void reflexiveItem(T each, MenuItem item,boolean isLeaf) {
		writeDefaultItem(each, item);
		item.addListener(SWT.Selection, itemListener);
		if(!isLeaf) getMenuItem(item,each.getChildren());
	}
	
	
	/* ================================================================================== */
	/*                                                                                    */
	/* ================================================================================== */
	
	public void setLeafItem(int leafItem) {
		this.leafItem = leafItem;
	}

	public void setItemListener(Listener itemListener) {
		this.itemListener = itemListener;
	}	
	
	
	
}
