package erwins.swt.network;

import java.io.File;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import erwins.swt.SWTBuildable;
import erwins.swt.StoreForMap;
import erwins.swtUtil.lib.BuildUtil;
import erwins.swtUtil.lib.LayoutUtil;
import erwins.swtUtil.lib.MessageUtil;
import erwins.util.counter.Counter;
import erwins.util.counter.SimpleCounter;
import erwins.util.lib.RegEx;
import erwins.util.lib.Strings;
import erwins.util.root.StringCallback;
import erwins.util.vender.apache.RESTful;

public class DownloadByUrl implements SWTBuildable{
	
	private static final StoreForMap<String> directory = new StoreForMap<String>("DownloadByUrl");
	private static final String SAVED_DIR = "savedDir";
	private Shell shell;
	
	private Label directoryName;
	
	private Button setDirectory;
	private Button execute;
	
	private Text url;
	private Text exts;
	
	private List result;
	
	public void build(final Composite root) {
		this.shell = root.getShell();
		
		final Composite top = new Composite(root, SWT.BORDER);
		top.setLayout(LayoutUtil.container(7));
		top.setLayoutData(LayoutUtil.hBox(65));
		
		directoryName = BuildUtil.addLabel(top, "");
		directoryName.setLayoutData(LayoutUtil.hSpan(3));
		setDirectory = BuildUtil.addButton(top, " 디렉토리 입력/변경 ");
		BuildUtil.addLabel(top, "확장자 :");
		exts = BuildUtil.addText(top);
		exts.setText("jpg");
		execute = BuildUtil.addButton(top, "URL 다운도드 실행");
		execute.setLayoutData(LayoutUtil.vSpan(2));
		
		BuildUtil.addLabel(top, "URL :");
		url = BuildUtil.addText(top);
		url.setLayoutData(LayoutUtil.hSpan(5));
		
		final Composite bot = new Composite(root, SWT.BORDER);
		bot.setLayout(new GridLayout());
		bot.setLayoutData(LayoutUtil.FULL);
		
		result = new List(bot,SWT.V_SCROLL);
		result.setLayoutData(LayoutUtil.FULL);
		
		
		addListener();
		initialize();
	}


	private void addListener() {
		
		setDirectory.addListener(SWT.MouseUp,new Listener() {
			String beforeSelected = null;
			@Override
			public void handleEvent(Event arg0) {
				
				DirectoryDialog fileDialog = new DirectoryDialog(shell, SWT.OPEN);
				fileDialog.setFilterPath(beforeSelected);
				String dir = fileDialog.open();
				if(dir==null) return;
				beforeSelected = dir;
				
				directoryName.setText(dir);
				directory.put(SAVED_DIR, dir);
			}
		});		
		
    	execute.addListener(SWT.MouseUp,new Listener() {
			@Override
			public void handleEvent(Event arg0) {
				String urlText = url.getText();
				if(Strings.isEmpty(urlText)){
					MessageUtil.alert(shell, "URL을 입력해 주세요");
					return;
				}
				File directory = new File(directoryName.getText());
				if(!directory.isDirectory()){
					MessageUtil.alert(shell, directoryName.getText()+" 는 디렉토리가 아닙니다.");
					return;
				}
				String extsText = exts.getText();
				if(Strings.isEmpty(extsText)){
					MessageUtil.alert(shell, "원하는 확장자 명(ex jpg)을 ,로 구분해서 입력해 주세요.");
					return;
				}
				String[] extsArray = extsText.split(",");
				
				processAsynch(urlText, directory, extsArray);
			}

		});
	}
	
	private void processAsynch(final String urlText,final File rootDir,final String[] ableExt) {
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				String html = RESTful.get(urlText).run().asString("EUC-KR"); //보통 한국.
				
				if(!rootDir.exists()) rootDir.mkdirs();
		        html = RegEx.TAG_SCRIPT.replace(html,"");
		        final Counter counter = new SimpleCounter();
		        RegEx.TAG_IMG.process(html, new StringCallback(){
		            public void process(String line) {
		                final String src = RegEx.find("(?<=src=('|\")).*?(?=\\1)", line);
		                if(src==null) return; // <img src=http://img.ruliweb.com/image/memo2.gif/> 처럼 ""로 안둘러싸여져 있을때 무시.
		                if(ableExt.length!=0 && !Strings.isMatchIgnoreCase(src, ableExt)) return;
		                String fileName = Strings.getLast(src,"/");
		                final File local = new File(rootDir,fileName);
		                RESTful.get(src).run().asFile(local);
		                
		                addResultToDisplayForAsynch(src + "==>" + local.getAbsolutePath());
		                counter.next();
		            }
		        });
		        
		        addResultToDisplayForAsynch(counter.count() + " 개의 다운로드가 완료되었습니다.");
			
			}
		}).start();
	}
	
	private void addResultToDisplayForAsynch(final String text) {
		Display.getDefault().syncExec(new Runnable() {
			@Override
			public void run() {
				result.add(text);
			}
        });
	}	
		
	private void initialize() {
		String exist = directory.get(SAVED_DIR);
		if(exist!=null) directoryName.setText(exist);
	}

}
