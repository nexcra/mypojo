package erwins.util.lib{
	import com.adobe.serialization.json.*;
	
	import mx.formatters.DateFormatter;
	import mx.utils.*;
	
	/** Date관련 유틸 */
	public class Days{
		
		/** 8자만 되는거 알지? */
	    public static function fromText(date:String):Date{
	        var month:String = String(int(date.substr(4, 2)) - 1);
	        return new Date(date.substr(0, 4), month, date.substr(6, 2));
	    }
	    
	    /** 8자씩 끊어서 년월일로. => 임시사용 */
	    public static function fromTextToStr(yyyyMMdd:String):String{
	        return yyyyMMdd.substr(0, 4)+'년'+ yyyyMMdd.substr(4, 2) + '월'+yyyyMMdd.substr(6, 2) + '일';
	    }
	    		
		public static function toText(date:Date=null,formatString:String = "YYYY년MM월DD일"):String{
			var df:DateFormatter = new DateFormatter();
        	df.formatString = formatString;
        	return df.format(date);
		}
		
		/** 당월의 1일 */
		public static function firstDateOfMonth(date:Date=null):Date{
			if(date==null) date = new Date();
			date.setDate(1);
			return date;
		}
		
		/** 당월의 마지막일 */
		public static function lastDateOfMonth(date:Date=null):Date{
			if(date==null) date = new Date();
			date.setDate(1); //31일의 경우 1달을 넘기면 1달 하고 1일이 넘어가 버린다. 따라서 디폴트로 세팅.
			date.setMonth(date.getMonth()+1);
			date.setDate(0); //전월 마지막일로 변경
			return date;
		}
		
	}
}