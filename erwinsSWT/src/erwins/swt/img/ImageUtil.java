package erwins.swt.img;

import java.io.InputStream;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.widgets.Display;

public enum ImageUtil {
	BACK("/erwins/swt/img/back.jpg"),
	FORWARD("/erwins/swt/img/forward.jpg"),
	HOME("/erwins/swt/img/home.jpg"),
	REFRESH("/erwins/swt/img/refresh.jpg"),
	
	TABLE("/erwins/swt/img/table.png"),
	X("/erwins/swt/img/x.png"),
	CIRCLE("/erwins/swt/img/circle.png"),
	
	ANT("/erwins/swt/img/ant.png"),
	GOLD_BUG("/erwins/swt/img/goldbug.png"),
	
	OPEN("/erwins/swt/img/open.png"),
	CLOSE("/erwins/swt/img/close.png"),
	BIN("/erwins/swt/img/bin.png"),
	NOTE("/erwins/swt/img/note.png"),
	FILE("/erwins/swt/img/file.png");
	
	private ImageUtil(String uri){
		this.uri = uri;
	}
	
	private String uri;
	private Image image;
	
	public static void loadImage(Display display) {
		for(ImageUtil each : ImageUtil.values()){
			InputStream stream = ImageUtil.class.getResourceAsStream(each.uri);
			ImageData imageData = new ImageData(stream);
			if (imageData != null) {
				each.image =  new Image(display, imageData);// , mask);
			}
		}
	}

	public Image getImage() {
		return image;
	}
	
}
