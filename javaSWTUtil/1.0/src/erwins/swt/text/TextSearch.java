package erwins.swt.text;

import java.io.File;

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
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;

import erwins.swt.SWTBuildable;
import erwins.swt.StoreForList;
import erwins.swt.img.ImageUtil;
import erwins.swt.text.TextSearchService.TextSearchResult;
import erwins.swtUtil.lib.BuildUtil;
import erwins.swtUtil.lib.LayoutUtil;
import erwins.swtUtil.lib.MessageUtil;
import erwins.swtUtil.lib.SimpleTreeItem;
import erwins.swtUtil.lib.TableUtil;
import erwins.swtUtil.lib.TreeItemGenerator;
import erwins.util.lib.Files;
import erwins.util.lib.Strings;

public class TextSearch implements SWTBuildable{
	
	private static final StoreForList<TextSearchService> codeLineFile = new StoreForList<TextSearchService>("TextSearchFile");
	private Shell shell;
	private Table table;
	private Tree dependencyTree;
	private Text inputString;
	
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

		TableUtil.addColumn(table, "디렉토리 경로",400);
		TableUtil.addColumn(table, "검색할 문자열",210);
		
		final Composite btns = new Composite(body, SWT.NONE);
		btns.setLayout(LayoutUtil.container(1));
		btns.setLayoutData(LayoutUtil.FULL);
		
		inputString = new Text(btns, SWT.LEFT);
		inputString.setLayoutData(LayoutUtil.FULL);
		
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
					MessageUtil.alert(shell, "하나의 컬럼을 선택하세요");
					return;
				}
				TableItem item = selected[0];
				TextSearchService service = (TextSearchService)item.getData();
				table.remove(table.indexOf(item));
				codeLineFile.remove(service);
			}
		});
		
		addDirectory.addListener(SWT.MouseUp,new Listener() {
			String beforeSelected = null;
			@Override
			public void handleEvent(Event arg0) {
				
				String searchKey = inputString.getText();
				if(Strings.isEmpty(searchKey)){
					MessageUtil.alert(shell,"검색어를 입력해 주세요.");
					return;
				}
				
				DirectoryDialog fileDialog = new DirectoryDialog(addDirectory.getShell(), SWT.OPEN);
				fileDialog.setFilterPath(beforeSelected);
				String dir = fileDialog.open();
				if(dir==null) return;
				beforeSelected = dir;
				File directory = new File(dir);
				TextSearchService service = new TextSearchService(directory,searchKey);
				codeLineFile.add(service);
				addTableItem(service);
			}
		});		
		
		table.addListener(SWT.MouseDoubleClick,new Listener() {
			@Override
			public void handleEvent(Event arg0) {
				TableItem[] selected = table.getSelection();
				if(selected.length==0){
					MessageUtil.alert(shell, "하나의 컬럼을 선택하세요");
					return;
				}
				TableItem item = selected[0];
				TextSearchService service = (TextSearchService)item.getData();
				
				SimpleTreeItem root = new SimpleTreeItem();
		    	
		    	for(TextSearchResult each : service.scan()){
		    		String relativePath  = Files.getrelativePath(each.getFile(), service.getRoot());
		    		SimpleTreeItem parent = new SimpleTreeItem();
		    		parent.setName(relativePath);
		    		
		    		for(String line : each){
		    			SimpleTreeItem child = new SimpleTreeItem();
		    			child.setName(line);
		    			parent.addChildren(child);
		    		}
		    		root.addChildren(parent);
		    	}
	
		    	dependencyTree.removeAll();
		    	
		    	SimpleTreeItem.addItemIfNoChildren(root,service.getSearchString()+"로 검색된 데이터가 없습니다.");
		    	
		    	TreeItemGenerator<SimpleTreeItem> generator = new TreeItemGenerator<SimpleTreeItem>(dependencyTree);
		    	generator.setNodeItemImage(ImageUtil.CLOSE.getImage());
		    	generator.setLeafItemImage(ImageUtil.FILE.getImage());
		    	generator.generate(root.getChildren());
			}
		});
	}

	private void initialize() {
		for(TextSearchService each : codeLineFile.get()){
			if(each.getRoot().exists()) addTableItem(each);
		}
	}
	
	private void addTableItem(TextSearchService service) {
		
		TableItem item = new TableItem(table, SWT.NONE);
		item.setData(service);
		item.setText(0, service.getRoot().getAbsolutePath());
		item.setText(1,service.getSearchString());
	}	

}
