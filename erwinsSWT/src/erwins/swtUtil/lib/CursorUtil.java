package erwins.swtUtil.lib;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.widgets.Display;

public abstract class CursorUtil {
	
	public static Cursor WAIT;
	public static Cursor HAND;
	public static Cursor NO;
	public static Cursor HELP;
	
	public static void loadCursor(Display display) {
		WAIT = display.getSystemCursor(SWT.CURSOR_WAIT);
		HAND = display.getSystemCursor(SWT.CURSOR_HAND);
		NO = display.getSystemCursor(SWT.CURSOR_NO);
		HELP = display.getSystemCursor(SWT.CURSOR_HELP);
	}
	
	
}
