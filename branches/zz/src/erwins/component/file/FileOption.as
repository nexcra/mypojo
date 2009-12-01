package erwins.component.file {
	import com.adobe.serialization.json.*;
	
	import flash.net.FileFilter;
	
	import mx.utils.*;
	
	public class FileOption{
		
		public var fileName:String = "";
		public var successCallback:Function;
		/** e:error를 인자로 받는다. */
		public var failCallback:Function;
		public var args:Object;
		
		public var filterList:Array;
		
		public function addArg(key:String,value:String):FileOption{
			if(args==null) args = new Object();
			args[key] = value;
			return this;
		}
		
		public static const csvFilter:FileFilter = new FileFilter("CSV(*.csv;)","*.csv;");
    	public static const allFilter:FileFilter = new FileFilter("All(*.*)","*.*");
		
		/** 필터는 대소문자를 구분하지 않는다. */
		public function addFilter(filter:FileFilter):FileOption{
			if(filterList==null) filterList  = new Array();
			filterList.push(filter);
			return this;
		}
		
		
		
	}
}