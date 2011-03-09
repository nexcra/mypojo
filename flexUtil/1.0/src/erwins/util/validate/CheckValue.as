package erwins.util.validate{
	import com.adobe.serialization.json.*;
	
	import erwins.util.*;
	import erwins.util.lib.Strings;
	
	import mx.controls.*;
	import mx.core.UIComponent;
	import mx.utils.*;
	
	
	/** 
	 * value를 체크하고 error를 던지는 벨리데이터. 
	 * */
	public class CheckValue{
		
		/** 입시 저장되는 value */
		public var value:Object = new Object;
		/** 입시 저장되는 컴포넌트 */
		private var component:UIComponent = null;
		
		public function CheckValue(value:Object,component:UIComponent=null){
			this.value = value;
			this.component = component;
		}
		
		/** error에 적당한 값들을 포함해서 던진다. */
		private function error(message:String):void{
			var error:MalformedError = new MalformedError(message,component);
			throw error;
		}
		
		public function isNotEmpty(message:String):CheckValue{
			if(value==null) error(message);
			if(value is String){
				if(Strings.isEmpty(value as String)) error(message);	
			}else if(value is Array){
				var array:Array = value as Array;
				if(array.length==0) error(message);
			}
			return this;
		}
		
		/** 비밀번호 등~  */
		public function isEquals(compare:String,message:String):CheckValue{
			if(value is String){
				if(compare != value.toString()) error(message);	
			}else if(value is Array){
				error('value must be String type');
			}
			return this;
		}		
		
		/** size와 문자 길이가 일치하는지? */
		public function isLength(size:int,message:String):CheckValue{
			if(value.toString().length != size) error(message);
			return this;
		}
		
		/** a와 b사이인지?  동일값은 true로 인식한다.  */
		public function isBetween(a:int,b:int,message:String):CheckValue{
			var val:int = Number(value);
			if(!( a <= val && b>=val )) error(message);
			return this;
		}
		public function isSmallThan(limit:int,message:String):CheckValue{
			var val:int = Number(value);
			if(val > limit) error(message);
			return this;
		}
		public function isLargeThan(limit:int,message:String):CheckValue{
			var val:int = Number(value);
			if(val < limit) error(message);
			return this;
		}

		
	}
}