package erwins.util.lib{
	import com.adobe.serialization.json.*;
	
	import erwins.util.json.Jsons;
	
	import flash.external.ExternalInterface;
	
	import mx.utils.*;
	
	public class Js{
		
		/** Alert */		
		public static function alert(message:String):void{
			ExternalInterface.call("alert",message);
		}
		public static function eval(message:String):Object{
			return ExternalInterface.call("eval",message);
		}
		
		/** HTML의 위치를 이동한다. */
		public static function relocation(url:String,param:Object):void{
			var buff:String = "location.href = '" + url;
			if(param!=null){
				buff += "?";
				buff += Jsons.serialize(param);
			}
			buff+= "';";
			eval(buff);
		}
		
		/** 
		 * 이 버전은 인자를 하나바께 못받는다..  예외 시 null을 리턴한다. 
		 * ... params 이 자동으로 Array로 바껴서 재입력이 불가능 하다.
		 * */
		public static function call(jsFuncName:String,param:String):Object{
			var result:Object = null;
			try{
				result =  ExternalInterface.call(jsFuncName,param);
			}catch(e:Error){
				//무시한다. ㄷㄷ
			}
			return result;
		}
		
		/** 외부 함수와 내부 함수 매칭 */
		public static function callBack(jsFuncName:String,callBack:Function):void{
			ExternalInterface.addCallback(jsFuncName, callBack); 
		}
		
		public static function getJavascriptObject(javascriptObject:String):Object{
			return Js.eval("var tempFs = function(){return "+javascriptObject+";}; tempFs();");
		}
		
		public static function setJavascriptObject(javascriptObject:String,queryString:String):void{
			Js.eval(javascriptObject + " = '" + queryString +"';");
		}
		
		/* ===== 이하 망했음 ㅠㅠ ===== */
		public static const HASH:String = "location.hash";
		public static const TITLE:String = "document.title";
		public static const URL:String = "document.URL";
		
		/** #이후로 된 문자열을 세팅한다. 없으면 만들고 있다면 교체한다.
		 * title이 도중에 바뀌는거때문에 수동으로 조작해 준다. ㅋㅋ 문제나도 몰라잉~ */
		public static function setHash(key:String,value:String):void{
			//var title:String = getJavascriptObject(TITLE);
			var json:Object = getHash();
			if(json==null) json = new Object();
			json[key] = value;
			var queryString:String = JSON.encode(json);
			setJavascriptObject(HASH,queryString);
			//setJavascriptObject(TITLE,title);
		}
		
		/** #이후로 된 문자열의 특정 프로퍼티를 가져온다. 
		 * 해석 불가능한 문자일 경우 해시를 지워준다.
		 * null을 리턴하지 않고 빈 객체를 리턴해준다. */
		public static function getHash():Object{
			var temp:Object = getJavascriptObject(HASH);
			if(!(temp is String)) return null;
			var hash:String = String(temp);
			hash = hash.substring(1,hash.length); //맨 처음의 #를 잘라준다
			var obj:Object;
			try{
				obj =  JSON.decode(hash);
			}catch(e:JSONParseError){
				trace(e.message);
				setJavascriptObject(HASH,'');
			}
			if(obj==null) obj = new Object();
			return  obj;
		}		

	}
}