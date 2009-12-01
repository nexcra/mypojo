package erwins.util{
	import com.adobe.serialization.json.*;
	
	import flash.external.ExternalInterface;
	
	import mx.utils.*;
	
	public class Js{
		
		/** Alert */		
		public static function alert(message:String):void{
			ExternalInterface.call("alert",message);
		}
		public static function eval(message:String):String{
			return ExternalInterface.call("eval",message);
		}
		
		/** HTML의 위치를 이동한다. */
		public static function relocation(url:String,param:Object):void{
			var buff:String = "location.href = '" + url;
			if(param!=null){
				buff += "?";
				buff += Json.serialize(param);
			}
			buff+= "';";
			eval(buff);
		}
		
		/** 
		 * 이 버전은 인자를 하나바께 못받는다..
		 * ... params 이 자동으로 Array로 바껴서 재입력이 불가능 하다.
		 * */
		public static function call(jsFuncName:String,param:String):String{
			return ExternalInterface.call(jsFuncName,param);
		}
		/** 외부 함수와 내부 함수 매칭 */
		public static function callBack(jsFuncName:String,callBack:Function):void{
			ExternalInterface.addCallback(jsFuncName, callBack); 
		}

	}
}