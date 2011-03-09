package erwins.swtUtil.lib;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

/** 그외 abort , retry, ignore , cancel 등이 있다 */
public abstract class MessageUtil{

	public static void alert(Shell shell,String Message){
		MessageBox di = new MessageBox(shell,SWT.ICON_WARNING);
		di.setText("Alert");
		di.setMessage(Message);
		di.open();
	}
	
	/** 미확인 */
	public static boolean confirm(Shell shell,String Message){
		MessageBox di = new MessageBox(shell,SWT.ICON_QUESTION | SWT.YES | SWT.NO);
		di.setText("confirm");
		di.setMessage(Message);
		int result = di.open();
		return result == SWT.YES;
	}
	
	public static void message(Shell shell,String title,String Message){
		MessageBox di = new MessageBox(shell);
		di.setText(title);
		di.setMessage(Message);
		di.open();
	}
	
}
