package erwins.util.lib;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;

import javax.imageio.ImageIO;



/**
 * 대충 유틸
 **/
public abstract class ImgUtil{
    
	
	/** 
	 * AWT를 이용한 간이 이미지 생성
	 * 간이캡챠에 사용.  <-- 캡차는 구글 리캡차를 사용하자. 
	 *  */
	public void stringToImage(String text,int w,int h,OutputStream out) throws IOException{
		
		Font font = new Font("Lucida Bright Demibold", Font.ITALIC + Font.BOLD , 28);
		
		/* 시스템 폰트 리스트 확인 */
		//Font[] fontList = GraphicsEnvironment.getLocalGraphicsEnvironment().getAllFonts();
		//for( Font f : fontList){
		//    System.out.println(f.getName());
		//}
		
		FontRenderContext frc = new FontRenderContext(null, false, true);
		
		Rectangle2D bounds = font.getStringBounds(text, frc);
		
		// 이미지 생성
		BufferedImage image = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
		Graphics2D g = image.createGraphics();
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, w, h);
		g.setColor(new Color(0, 0, 0));
		g.setFont(font);
		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
		g.drawString(text, (float) 5, (float) -bounds.getY()); //image글자를 약간 오른쪽부터 그리기 위해 5를 고정.
		g.dispose();
		ImageIO.write(image, "png", out);
	}

}