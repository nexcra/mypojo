package erwins.swt.network;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;

import erwins.swt.SWTBuildable;
import erwins.swt.StoreForList;
import erwins.swt.img.ImageUtil;
import erwins.swt.network.FtpSynchService.FTPcallback;
import erwins.swt.network.FtpSynchService.SynchType;
import erwins.swtUtil.lib.BuildUtil;
import erwins.swtUtil.lib.LayoutUtil;
import erwins.swtUtil.lib.MessageUtil;
import erwins.swtUtil.lib.SimpleTreeItem;
import erwins.swtUtil.lib.TableUtil;
import erwins.swtUtil.lib.TreeItemGenerator;
import erwins.swtUtil.lib.TreeUtil;
import erwins.swtUtil.root.FailCallback;
import erwins.util.lib.Strings;
import erwins.util.vender.apache.NetRoot.FtpLog;

public class FtpSynch implements SWTBuildable{
	
	private static final StoreForList<FtpSynchService> ftpSynchFile = new StoreForList<FtpSynchService>("FtpSynchFile");
	private Shell shell;
	private Table table;
	private Tree dependencyTree;
	
	private Button addDirectory;
	private Button removeDirectory;
	private Button commit;
	private Button update;
	private Button commitLog;
	private Button updateLog;
	
	private Text ip;
	private Text port;
	private Text id;
	private Text pass;
	private Text remoteDir;
	private Button passive;
	
	public void build(final Composite root) {
		this.shell = root.getShell();
		
		buildTop(root);
		buildMid(root);
		
		final Composite bot = new Composite(root, SWT.BORDER);
		bot.setLayout(new GridLayout());
		bot.setLayoutData(LayoutUtil.FULL);
		
		dependencyTree = new Tree(bot, SWT.SINGLE | SWT.BORDER | SWT.FULL_SELECTION);
		dependencyTree.setLayoutData(LayoutUtil.FULL);
		
		addListener();
		addMainListener();
	
		initialize();
	}

	private void buildMid(final Composite root) {
		final Composite mid = new Composite(root, SWT.NONE);
		mid.setLayout(LayoutUtil.container(11));
		mid.setLayoutData(LayoutUtil.hBox(30));
		
		Label ipText = new Label(mid,SWT.CENTER | SWT.SHADOW_OUT);
		ipText.setText("서버IP"); 
		ip = new Text(mid,SWT.BORDER | SWT.SINGLE | SWT.LEFT);
		ip.setLayoutData(LayoutUtil.FULL);
		
		Label portText = new Label(mid,SWT.CENTER | SWT.SHADOW_OUT);
		portText.setText("port"); 
		port = new Text(mid,SWT.BORDER | SWT.SINGLE | SWT.LEFT);
		port.setLayoutData(LayoutUtil.FULL);
		
		Label idText = new Label(mid,SWT.CENTER | SWT.SHADOW_OUT);
		idText.setText("접속ID"); 
		id = new Text(mid,SWT.BORDER | SWT.SINGLE | SWT.LEFT);
		id.setLayoutData(LayoutUtil.FULL);
		
		Label passText = new Label(mid,SWT.CENTER | SWT.SHADOW_OUT | SWT.PASSWORD);
		passText.setText("PASS"); 
		pass = new Text(mid,SWT.BORDER | SWT.SINGLE | SWT.LEFT);
		pass.setLayoutData(LayoutUtil.FULL);
		
		Label remotePathText = new Label(mid,SWT.CENTER | SWT.SHADOW_OUT | SWT.PASSWORD);
		remotePathText.setText("원격 디렉토리"); 
		remoteDir = new Text(mid,SWT.BORDER | SWT.SINGLE | SWT.LEFT);
		remoteDir.setLayoutData(LayoutUtil.FULL);
		
		passive = new Button(mid,SWT.TOGGLE);
		passive.setText("패시브 모드");
		remoteDir.setLayoutData(LayoutUtil.FULL);
	}

	private void buildTop(final Composite root) {
		final Composite top = new Composite(root, SWT.BORDER);
		top.setLayout(LayoutUtil.container(2));
		top.setLayoutData(LayoutUtil.hBox(150));

		table = new Table(top,SWT.SINGLE | SWT.FULL_SELECTION | SWT.BORDER);
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		table.setLayoutData(new GridData(700,120));

		TableUtil.addColumn(table, "서버IP",120);
		TableUtil.addColumn(table, "port",50);
		TableUtil.addColumn(table, "접속ID",100);
		TableUtil.addColumn(table, "로컬 디렉토리",190);
		TableUtil.addColumn(table, "원격 디렉토리",190);
		TableUtil.addColumn(table, "PASSIVE",60);
		
		final Composite btns = new Composite(top, SWT.NONE);
		btns.setLayout(LayoutUtil.container(2));
		btns.setLayoutData(LayoutUtil.FULL);
		
		addDirectory = BuildUtil.addButton(btns, "디렉토리 추가");
		removeDirectory = BuildUtil.addButton(btns, "디렉토리 삭제");
		commit = BuildUtil.addButton(btns, "commit");
		commitLog = BuildUtil.addButton(btns, "commitLog");
		update = BuildUtil.addButton(btns, "update");
		updateLog = BuildUtil.addButton(btns, "updateLog");
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
				TableItem item = table.getSelection()[0];
				FtpSynchService service = (FtpSynchService)item.getData();
				table.remove(table.indexOf(item));
				ftpSynchFile.remove(service);
			}
		});

		addDirectory.addListener(SWT.MouseUp,new Listener() {
			String beforeSelected = null;
			@Override
			public void handleEvent(Event arg0) {
				
				if(Strings.isEmptyAny(ip.getText(),port.getText(),id.getText(),pass.getText(),remoteDir.getText())){
					MessageUtil.alert(shell,"모든 항목을 입력해 주세요.");
					return;
				}
				
				DirectoryDialog fileDialog = new DirectoryDialog(addDirectory.getShell(), SWT.OPEN);
				fileDialog.setFilterPath(beforeSelected);
				String dir = fileDialog.open();
				if(dir==null) return;
				beforeSelected = dir;
				FtpSynchService service = new FtpSynchService(ip.getText(),Integer.parseInt(port.getText()),
						id.getText(),pass.getText(),dir,remoteDir.getText());
				service.setPassive(passive.getSelection());
				ftpSynchFile.add(service);
				addTableItem(service);
			}
		});
		
		table.addListener(SWT.MouseDoubleClick, new Listener() {
			
			@Override
			public void handleEvent(Event arg0) {
				TableItem item = table.getSelection()[0];
				FtpSynchService service = (FtpSynchService)item.getData();
				
				ip.setText(service.getIp());
				port.setText(String.valueOf(service.getPort()));
				id.setText(service.getId());
				remoteDir.setText(service.getRemotDir());
				passive.setSelection(service.isPassive());
			}
		});
	}

	/** Synch팩토리를 숨기기 위해 (static이 아닌 내부 객체를 리턴하기 때문 ㅠㅠ)  SynchType.COMMIT 를 따로 만들었다.  */
	private void addMainListener() {
		Listener clicked = new Listener() {
			@Override
			public void handleEvent(Event arg0) {
				TableItem[] selected = table.getSelection();
				if(selected.length==0){
					MessageUtil.alert(shell, "하나의 컬럼을 선택하세요");
					return;
				}
				TableItem item = table.getSelection()[0];
				
				FtpSynchService service = (FtpSynchService)item.getData();
				SynchType type =  (SynchType)arg0.widget.getData();
				
				TreeUtil.clearAndAddItem(dependencyTree, service.getLocalDir()+" 와 동기화 중입니다. 잠시 기다려 주세요.");
		    	
				service.synchronize(type, new FTPcallback() {
					@Override
					public void run(final FtpLog log) {
						
						Display.getDefault().syncExec(new Runnable() {
							
							@Override
							public void run() {
								SimpleTreeItem root = new SimpleTreeItem();
						    	
								eachIterate( root,"다운로드",log.getDownloaded());
								eachIterate( root,"업로드",log.getUploaded());
								eachIterate( root,"원격지 디렉토리 생성",log.getFtpDirectoryMaked());
								eachIterate( root,"원격지 디렉토리 삭제",log.getFtpDirectorydeleted());
								eachIterate( root,"원격지 파일 삭제",log.getFtpFileDeleted());
								eachIterate( root,"로컬 파일 삭제",log.getLocalFileDeleted());
								eachIterate( root,"파일 이동",log.getMoved());
								eachIterate( root,"오류내역",log.getError());
					
						    	dependencyTree.removeAll();
						    	SimpleTreeItem.addItemIfNoChildren(root,"로그 내역이 없습니다.");
						    	
						    	TreeItemGenerator<SimpleTreeItem> generator = new TreeItemGenerator<SimpleTreeItem>(dependencyTree);
						    	generator.setNodeItemImage(ImageUtil.CLOSE.getImage());
						    	generator.setLeafItemImage(ImageUtil.FILE.getImage());
						    	generator.generate(root.getChildren());
							}
						});
					}
				},new FailCallback() {
					@Override
					public void exceptionHandle(Exception e) {
						MessageUtil.alert(shell, e.getMessage());
					}
				});
			}

			private void eachIterate(SimpleTreeItem root,String name, Iterable<String> list) {
				if(!list.iterator().hasNext()) return;
				SimpleTreeItem parent = new SimpleTreeItem();
				parent.setName(name);
		    	for(String each : list){
		    		SimpleTreeItem child = new SimpleTreeItem();
	    			child.setName(each);
	    			parent.addChildren(child);
		    	}
		    	root.addChildren(parent);
			}
		};
		
		commit.addListener(SWT.MouseUp, clicked);
		commit.setData(SynchType.COMMIT);
		commitLog.addListener(SWT.MouseUp, clicked);
		commitLog.setData(SynchType.COMMIT_LOG);
		update.addListener(SWT.MouseUp, clicked);
		update.setData(SynchType.UPDATE);
		updateLog.addListener(SWT.MouseUp, clicked);
		updateLog.setData(SynchType.UPDATE_LOG);
	}

	private void initialize() {
		for(FtpSynchService each : ftpSynchFile.get()){
			addTableItem(each);
		}
	}
	
	private void addTableItem(FtpSynchService service) {
		TableItem item = new TableItem(table, SWT.NONE);
		item.setData(service);
		item.setText(0, service.getIp());
		item.setText(1,String.valueOf(service.getPort()));
		item.setText(2,service.getId());
		item.setText(3,service.getLocalDir());
		item.setText(4,service.getRemotDir());
		item.setText(5,service.isPassive() ? "PASSIVE" : "-");
	}	

}
