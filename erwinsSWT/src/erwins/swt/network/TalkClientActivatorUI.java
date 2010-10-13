package erwins.swt.network;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import erwins.swt.SWTBuildable;
import erwins.swtUtil.lib.BuildUtil;
import erwins.swtUtil.lib.LayoutUtil;

public abstract class TalkClientActivatorUI implements SWTBuildable{
	
	protected Shell shell;
	
	protected List loginList;
	protected Text view;

	protected Text message;
	protected Text loginId;
	
	protected Button connect;
	protected Button login;
	protected Button logout;
	protected Button viewClear;
	protected Button send;
	
	public void build(final Composite root) {
		this.shell = root.getShell();
		buildTop(root);
		buildBot(root);
		addMainListener();
		initialize();
	}
	
	abstract protected void  addMainListener();
	abstract protected void  initialize();
	
	private void buildTop(final Composite root) {
		final Composite top = new Composite(root, SWT.BORDER);
		top.setLayout(LayoutUtil.container(3));
		top.setLayoutData(LayoutUtil.hBox(450));
		
		final Composite topLeft = new Composite(top, SWT.BORDER);
		topLeft.setLayout(LayoutUtil.container(1));
		topLeft.setLayoutData(LayoutUtil.vBox(120));

		BuildUtil.addLabel(topLeft, "접속 리스트");
		
		loginList = new List(topLeft,SWT.V_SCROLL | SWT.MULTI);
		loginList.setLayoutData(LayoutUtil.FULL);
		
		view = new Text(top,SWT.WRAP | SWT.MULTI | SWT.READ_ONLY | SWT.BORDER | SWT.V_SCROLL);
		view.setLayoutData(new GridData(680,420));
		
		final Composite btns = new Composite(top, SWT.NONE);
		btns.setLayout(LayoutUtil.container(1));
		btns.setLayoutData(LayoutUtil.FULL);
		
		connect = BuildUtil.addButton(btns, "접속");
		loginId = new Text(btns,SWT.SINGLE );
		loginId.setLayoutData(LayoutUtil.hBox(20));
		login = BuildUtil.addButton(btns, "로그인");
		logout = BuildUtil.addButton(btns, "로그아웃");
		viewClear = BuildUtil.addButton(btns, "화면 지움.");
	}	

	private void buildBot(final Composite root) {
		final Composite bot = new Composite(root,SWT.BORDER);
		bot.setLayout(LayoutUtil.container(2));
		bot.setLayoutData(LayoutUtil.hBox(85));
		
		message = new Text(bot,SWT.MULTI | SWT.BOLD | SWT.WRAP | SWT.BORDER);
		message.setLayoutData(new GridData(800,75));
		send = BuildUtil.addButton(bot, "전송");
	}

}
