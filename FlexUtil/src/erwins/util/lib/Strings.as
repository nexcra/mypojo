package erwins.util.lib{
	import com.adobe.serialization.json.*;
	
	import mx.collections.ArrayCollection;
	import mx.utils.*;
	
	/** StringUtil을 상속한다. */
	public class Strings extends StringUtil{
		
		/** HTML제거.  */
	    public static function removeHTML(value:String):String{
	        var r:RegExp = /<[a-zA-Z\/][^>]*>/g
	        return value.replace(r,"");
	    }		
		
		/** 매칭되는 문자열이 있는지? */
		public static function isContain(str:String,key:String):Boolean{
			if(str==null || key==null) return false;
			if(str.indexOf(key) > -1) return true;
			else return false;
		}
		public static function isContainIgnoreCase(str:String,key:String):Boolean{
			if(str==null || key==null) return false;
			if(str.toUpperCase().indexOf(key.toUpperCase()) > -1) return true;
			else return false;
		}
		
		/** collection타입도 지원한다. */
		public static function isEmpty(str:String):Boolean{
			if(str==null || str=="") return true;
			return false;
		}
		
		/** join한다. */
		public static function join(array:ArrayCollection,seperator:String=''):String{
			var result:String = '';
			var first:Boolean = true;
			for each(var str:String in array){
				if(first) first = false;
				else result += seperator;
				result += str;
			}
			return result;
		}
		
	}
}