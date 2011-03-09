package erwins.util.UILib{
	import com.adobe.serialization.json.*;
	
	import flash.display.DisplayObject;
	import flash.display.Sprite;
	import flash.printing.PrintJob;
	
	import mx.controls.Alert;
	import mx.core.IUIComponent;
	import mx.printing.FlexPrintJob;
	import mx.printing.FlexPrintJobScaleType;
	import mx.utils.*;
	
	public class PrintUtil{
		
		/**
		 * 해당 객체를 프린트한다. ex) Map 등
		 * 아직은 ListBase등의 특정 객체만 되는듯 하다. 비추.
		 **/
		public static function print(obj:DisplayObject):void{
			var printJob:PrintJob = new PrintJob();
			printJob.start();
	  		var sprite:Sprite = new Sprite();
	  		obj.width = printJob.pageWidth;
	  		obj.height = printJob.pageHeight;
	  		sprite.addChild(obj);
	  		//printJob
	  		try {
				printJob.addPage(sprite);
			} catch (error:Error) {
				Alert.show(error.getStackTrace(),error.message);
			}
	  		printJob.send();
		}
		
		/**
		 * 다른 책에서의 예제임. => Flex3 실전 트레이닝 북.
		 **/
		public static function printFlex(obj:IUIComponent):void{
			var pj:FlexPrintJob = new FlexPrintJob();
			if(!pj.start()) return;
			//pj.addObject(obj,FlexPrintJobScaleType.MATCH_WIDTH);  //기본설정임.
			pj.addObject(obj,FlexPrintJobScaleType.SHOW_ALL);
	  		pj.send();
		}
	}
		

}