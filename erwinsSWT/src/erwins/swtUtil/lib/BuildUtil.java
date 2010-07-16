package erwins.swtUtil.lib;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public abstract class BuildUtil{

	public static Button addButton(Composite composite,String text){
		Button btn = new Button(composite, SWT.PUSH);
		btn.setText(text);
		btn.setLayoutData(LayoutUtil.FULL);
		return btn;
	}
	
	public static Label addLabelFull(Composite composite,String text){
		Label label = addLabel(composite,text);
		label.setLayoutData(LayoutUtil.FULL);
		return label;
	}
	public static Label addLabel(Composite composite,String text){
		Label label = new Label(composite,SWT.CENTER | SWT.SHADOW_OUT);
		label.setText(text);
		return label;
	}
	
	public static Text addText(Composite composite){
		Text url = new Text(composite,SWT.BORDER | SWT.SINGLE | SWT.LEFT);
		url.setLayoutData(LayoutUtil.FULL);
		return url;
	}
	
}
