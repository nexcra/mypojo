package erwins.component.file {
	import com.adobe.serialization.json.*;
	
	import flash.net.FileFilter;
	
	import mx.utils.*;
	
	/** 필터는 대소문자를 구분하지 않는다. */
	public class FileFilterOption{
		
		public static const EXCELL2003:FileFilter = new FileFilter("Excell2003(*.xls)","*.xls"); 
		public static const IMG:FileFilter = new FileFilter("Images (*.jpg, *.jpeg, *.gif, *.png, *.bmp)", "*.jpg; *.jpeg; *.gif; *.png; *.bmp"); 
		
		public static const CSV:FileFilter = new FileFilter("CSV(*.csv;)","*.csv;");
    	public static const ALL:FileFilter = new FileFilter("All(*.*)","*.*");
	}
}