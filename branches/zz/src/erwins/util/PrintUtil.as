package erwins.util{
	import com.adobe.serialization.json.*;
	
	import flash.display.Bitmap;
	import flash.display.Sprite;
	import flash.printing.PrintJob;
	
	import mx.controls.Alert;
	import mx.utils.*;
	
	public class PrintUtil{
		
		/**
		 * 해당 객체를 프린트한다. ex) Map 등
		 **/
		public static function print(obj:Bitmap):void{
			var printJob:PrintJob = new PrintJob();
			printJob.start();
	  		var sprite:Sprite = new Sprite();
	  		sprite.addChild(obj);
	  		try {
				printJob.addPage(sprite);
			} catch (error:Error) {
				Alert.show(error.getStackTrace(),error.message);
			}
	  		printJob.send();
		}
	}
		

}