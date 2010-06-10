package erwins.swt;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import erwins.swt.img.ImageUtil;
import erwins.swtUtil.lib.CursorUtil;
import erwins.swtUtil.lib.LayoutUtil;

public class Activator {

	public static void main(String[] args) {
		Display display = new Display();
		final Shell shell = new Shell(display);
		shell.setText("영감남의 SWT");

		ImageUtil.loadImage(display);
		CursorUtil.loadCursor(display);

		new SWTMenuBuilder(shell);
		
		shell.setLayout(LayoutUtil.container(1));
		shell.setSize(1024, 650);
		// shell.setMaximized(true);
		shell.open();

		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) display.sleep();
		}
		display.dispose();
	}

}
