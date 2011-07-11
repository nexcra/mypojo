package erwins.util.lib{
	import com.adobe.serialization.json.*;
	
	import mx.collections.ArrayCollection;
	import mx.utils.*;
	
	/** StringUtil을 상속한다. */
	public class Strings extends StringUtil{
		
		/** SMS등 한글이 들어가면 2로 카운트하는 문자길이를 계산한다. */
		public static function textLength(text:String):int{
			var sum:int = 0;
			for(var i:int = 0;i<text.length;i++){
				if(text.charCodeAt(i) >= 12593) sum+= 2;
				else sum+= 1;
			}
			return sum;
		}
		
		/** 양키의 간이 변환기 ㅋㅋ 쎈스 넘치는군 */
		public static function replaceAll(value:String,target:String,replaced:String):String{
			return value.split(target).join(replaced);
		}		
		
		/** HTML제거.  */
	    public static function removeHTML(value:String):String{
	        var r:RegExp = /<[a-zA-Z\/][^>]*>/g
	        return value.replace(r,"");
	    }
	    
	    /** 확인필요 */
		/**
		 * str의 byte 단위 길이를 반환한다.
		 * ex) byteLength("hello") --> 5 byte
		 *     byteLength("안녕") ---> 4 byte
		 */
		public static function byteLength(str:String):uint {
			var length:uint = 0;
			
			for (var idx:uint = 0; idx < str.length; idx++) {
				if (str.charCodeAt(idx) < 256) {
					length += 1;
				} else {
					length += 2;
				}
			}
			
			return length;		
		}
		
		/** 확인필요 */
		public static function chkMaxLength(compareText:String,maxLength:Number):Boolean{
	 		var paramLength:uint = compareText.length;
	 		var strLength:Number = 0;
 			var subStr:String = "";
 			var retnBool:Boolean = true;
	 		for(var i:uint = 0; i< paramLength;i++){
	 			strLength += (escape(compareText.charAt(i)).length > 4) ? 2 : 1;
	 		}
 			if(strLength>=maxLength){
 				retnBool = false;	 					
 			}
 			return retnBool;
	 	}
	 	
	 	/** 확인필요 */
	   public static function getTextByte(compareText:String):Number{
	 		var paramLength:uint = compareText.length;
	 		var strLength:Number = 0;
	 		for(var i:uint = 0; i< paramLength;i++){
	 			strLength += (escape(compareText.charAt(i)).length > 4) ? 2 : 1;
	 		}
 			return strLength;
	 	}
	 	
		/*
	   public static function isRRNAdd(rrnAdd:String):Boolean{
	 		var isRRN:Boolean = true;
	 		
            var len:Number = rrnAdd.length;

            var strRRN:String;   
            var keyNum:Array = [2, 3, 4, 5, 6, 7, 8, 9, 2, 3, 4, 5];   
            var sum:int = 0;   
               
            strRRN = (rrnAdd.toString()).replace(new RegExp("-", "g"), "");   
               
               
            if(strRRN.length != 13)   
            {   
                Alert.show("주민등록번호 13자리를 입력하셔야합니다");   
                isRRN = false;    
            }else{   
                for(var ii:int=0; ii<12; ii++)   
                {   
                    sum += parseInt(strRRN.substr(ii, 1)) * keyNum[ii];   
                }   
                   
                var strRRNCheckNum:int = parseInt(strRRN.substr(12, 1));    
                var caculateCheckNum:int = (11-(sum%11))%10;               
                   
                if(strRRNCheckNum != caculateCheckNum)   
                {   
                    Alert.show("잘못된 주민등록번호입니다");   
                    isRRN = false;  
                }   
            }   
 			return isRRN;
	 	}
	 	*/	 	
		
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