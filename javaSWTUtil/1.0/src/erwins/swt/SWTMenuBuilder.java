package erwins.swt;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabFolderEvent;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;

import erwins.swt.img.ImageUtil;
import erwins.swtUtil.lib.LayoutUtil;
import erwins.swtUtil.lib.MenuItemGenerator;
import erwins.swtUtil.listener.FolderCloseListener;
import erwins.util.root.Shutdownable;

public class SWTMenuBuilder implements Shutdownable{
	
	private static final StoreForList<SWTMenu> codeLineFile = new StoreForList<SWTMenu>("SelectedMenu");
	private final CTabFolder folder;
	
	public SWTMenuBuilder(final Shell shell){
		
		folder = new CTabFolder(shell, SWT.BORDER);// SWT.FLAT);
		folder.setLayoutData(LayoutUtil.FULL);
		folder.setSimple(false);
		folder.setUnselectedImageVisible(true);
		folder.setUnselectedCloseVisible(true);
		
		folder.addCTabFolder2Listener(new FolderCloseListener() {
			@Override
			public void close(CTabFolderEvent arg0) {
				CTabItem item = (CTabItem)arg0.item;
				SWTMenu menu = (SWTMenu)item.getData();
				codeLineFile.remove(menu);
			}
		});
		
		Listener itemListener = new Listener() {
			public void handleEvent(Event event) {
				if (event.type != SWT.Selection) return;
				MenuItem item = (MenuItem)event.widget;
				SWTMenu menu = (SWTMenu)item.getData();
				addTab(menu);
				codeLineFile.add(menu);
				folder.redraw(); //??
			}
		};
		
		MenuItemGenerator<SWTMenu> generator = new MenuItemGenerator<SWTMenu>(shell);
		generator.setNodeItemImage(ImageUtil.OPEN.getImage());
		generator.setLeafItemImage(ImageUtil.TABLE.getImage());
		generator.setItemListener(itemListener);
		generator.generate(SWTMenu.root.getChildren());
		
		initialize();
	}

	private void initialize() {
		for(SWTMenu each : codeLineFile.get()) addTab(each);
	}
	
	private List<Shutdownable> sutdowns = Collections.synchronizedList(new ArrayList<Shutdownable>());
	
	private void addTab(SWTMenu menu) {
		SWTBuildable module =  menu.getSwt();
		if(module==null) return;
		
		CTabItem newTab = new CTabItem(folder, SWT.CLOSE);
		
		Composite body = new Composite(folder, SWT.NONE);
		body.setLayout(new GridLayout());
		newTab.setData(menu);
		newTab.setText(menu.getName());
		newTab.setControl(body);
		newTab.setImage(ImageUtil.CIRCLE.getImage());
		
		module.build(body);
		
		if(module instanceof Shutdownable){
			sutdowns.add((Shutdownable)module);
		}
		
	}

	/** shutdown�� �����ص� �����Ѵ�. */
	@Override
	public void shutdown() {
		for(Shutdownable each : sutdowns){
			try {
				each.shutdown();
			} catch (Exception e) {
				//NON
			}
		}
		
	}	


}
