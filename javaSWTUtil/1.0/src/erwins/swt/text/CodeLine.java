package erwins.swt.text;

import java.io.File;
import java.util.List;
import java.util.Map.Entry;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Tree;

import erwins.swt.SWTBuildable;
import erwins.swt.StoreForList;
import erwins.swt.img.ImageUtil;
import erwins.swtUtil.lib.BuildUtil;
import erwins.swtUtil.lib.LayoutUtil;
import erwins.swtUtil.lib.MessageUtil;
import erwins.swtUtil.lib.SimpleTreeItem;
import erwins.swtUtil.lib.TableUtil;
import erwins.swtUtil.lib.TreeItemGenerator;
import erwins.util.counter.AvgCounter;
import erwins.util.lib.Formats;

public class CodeLine implements SWTBuildable{
	
	private static final StoreForList<File> codeLineFile = new StoreForList<File>("CodeLineFile");
	private Shell shell;
	private Table table;
	private Tree dependencyTree;
	
	private Button addDirectory;
	private Button removeDirectory;
	
	public void build(final Composite root) {
		this.shell = root.getShell();
		final Composite body = new Composite(root, SWT.BORDER);
		body.setLayout(LayoutUtil.container(2));
		body.setLayoutData(LayoutUtil.hBox(150));

		table = new Table(body,SWT.SINGLE | SWT.FULL_SELECTION | SWT.BORDER);
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		table.setLayoutData(new GridData(700,120));

		TableUtil.addColumn(table, "프로젝트 소스",330);
		TableUtil.addColumn(table, "클래스 수",100);
		TableUtil.addColumn(table, "전체 라인",70);
		TableUtil.addColumn(table, "평균 라인",70);
		TableUtil.addColumn(table, "최대 라인",70);
		TableUtil.addColumn(table, "최소 라인",70);
		
		final Composite btns = new Composite(body, SWT.NONE);
		btns.setLayout(LayoutUtil.container(1));
		btns.setLayoutData(LayoutUtil.FULL);
		
		addDirectory = BuildUtil.addButton(btns, "디렉토리 추가");
		removeDirectory = BuildUtil.addButton(btns, "디렉토리 삭제");
		
		final Composite bot = new Composite(root, SWT.BORDER);
		bot.setLayout(new GridLayout());
		bot.setLayoutData(LayoutUtil.FULL);
		
		dependencyTree = new Tree(bot, SWT.SINGLE | SWT.BORDER | SWT.FULL_SELECTION);
		dependencyTree.setLayoutData(LayoutUtil.FULL);
		
		addListener();
		initialize();
	}

	private void addListener() {
		removeDirectory.addListener(SWT.MouseUp,new Listener() {
			@Override
			public void handleEvent(Event arg0) {
				TableItem[] selected = table.getSelection();
				if(selected.length==0){
					MessageUtil.alert(shell, "한개의 리스트를 선택해 주세요");
					return;
				}
				TableItem item = table.getSelection()[0];
				CodeLineService service = (CodeLineService)item.getData();
				table.remove(table.indexOf(item));
				codeLineFile.remove(service.getRoot());
			}
		});
		
		addDirectory.addListener(SWT.MouseUp,new Listener() {
			String beforeSelected = null;
			@Override
			public void handleEvent(Event arg0) {
				DirectoryDialog fileDialog = new DirectoryDialog(addDirectory.getShell(), SWT.OPEN);
				fileDialog.setFilterPath(beforeSelected);
				String dir = fileDialog.open();
				if(dir==null) return;
				beforeSelected = dir;
				File directory = new File(dir);
				codeLineFile.add(directory);
				addTableItem(directory);
			}
		});		
		
		table.addListener(SWT.MouseDoubleClick,new Listener() {
			@Override
			public void handleEvent(Event arg0) {
				TableItem[] selected = table.getSelection();
				if(selected.length==0){
					MessageUtil.alert(shell, "한개의 컬럼을 선택해 주세요");
					return;
				}
				TableItem item = selected[0];
				CodeLineService service = (CodeLineService)item.getData();
				SimpleTreeItem root = new SimpleTreeItem();
		    	for(Entry<String,List<String>> each : service.dependencyByJar()){
		    		
		    		SimpleTreeItem parent = new SimpleTreeItem();
		    		parent.setName(each.getKey());
		    		
		    		for(String line : each.getValue()){
		    			SimpleTreeItem child = new SimpleTreeItem();
		    			child.setName(line);
		    			parent.addChildren(child);
		    		}
		    		root.addChildren(parent);
		    	}
		    	dependencyTree.removeAll();
		    	TreeItemGenerator<SimpleTreeItem> generator = new TreeItemGenerator<SimpleTreeItem>(dependencyTree);
		    	generator.setNodeItemImage(ImageUtil.CLOSE.getImage());
		    	generator.setLeafItemImage(ImageUtil.FILE.getImage());
		    	generator.generate(root.getChildren());
			}
		});
	}

	private void initialize() {
		for(File each : codeLineFile.get()){
			if(each.exists()) addTableItem(each);
		}
	}
	
	private void addTableItem(File directory) {
		CodeLineService service = new CodeLineService(directory);
		AvgCounter c = service.getAvgCounter();
		TableItem item = new TableItem(table, SWT.NONE);
		item.setData(service);
		item.setText(0, directory.getAbsolutePath());
		item.setText(1, Formats.INT.get(c.getCount()));
		item.setText(2, Formats.INT.get(c.getSum()));
		item.setText(3, Formats.INT.get(c.getAvarage()));
		item.setText(4, Formats.INT.get(c.getMax()));
		item.setText(5, Formats.INT.get(c.getMin()));
	}	

}
