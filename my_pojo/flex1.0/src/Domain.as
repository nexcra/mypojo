package {
	import com.adobe.serialization.json.*;
	
	import erwins.component.popup.SimpleLogin;
	import erwins.util.UILib.ContextMenuUtil;
	import erwins.util.lib.Js;
	
	import mx.core.UIComponent;
	import mx.utils.*;
	public class Domain{
		/** 로컬테스트시는 /로 시작대되 되지만 실서버에서는 풀네임을 적어야 한다. 왜? ㅅㅂ */
		//public static const HOST:String = "http://my-pojo.appspot.com";
		private static var checked:Boolean = false;
		private static var server:Boolean;
		private static var was:Boolean = true;
		private var url:String = "";
			
		/** 메뉴  */
		public static function user():Domain{ return instance("/user"); }
		public static function mapLabel():Domain{ return instance("/mapLabel"); }
		//public static function trx():Domain{ return instance("/trx"); }
		
		/** 도메인객체 */		
		public function user():Domain{ url += "/user"; return this; }
		/*
		public function customer():Domain{ serverUrl += "/customer"; return this; }
		public function year():Domain{ serverUrl += "/year"; return this; }
		public function all():Domain{ serverUrl += "/all"; return this; }
		public function statistics():Domain{ serverUrl += "/statistics"; return this; }
		*/
		//public function info():Domain{ serverUrl += ".info"; return this; }
		
		/** 공용 명령어 */
		public function search():String{ return url + "/search"; }
		public function save():String{ return url + "/save"; }
		public function remove():String{ return url + "/remove"; }
		public function list():String{ return url + "/list"; }
		
		public function upload():String{ return url + "/upload"; }
		public function download():String{ return url + "/download"; }
		public function preDownload():String{ return url + "/preDownload"; }
		//public function login():String{ return serverUrl + ".login.do"; } // 이상하지만 그냥 쓴다.
		
		/*
		public function code():String{ return serverUrl + ".code.do"; }
		public function execute():String{ return serverUrl + ".execute.do"; }
		public function local():String{ return serverUrl + ".do"; }
		
		public function refresh():String{ return serverUrl + ".refresh.do"; }
		
		public function labelState():String{ return serverUrl + ".labelState.do"; }
		public function gradeCount():String{ return serverUrl + ".gradeCount.do"; }
		*/
		
		/** F11로 수정중인 상태일때는 직접 WAS의 주소를 명시해야 한다.  */
		private static function instance(menu:String):Domain{
			var domain:Domain = new Domain();
			//domain.url =  isServer() ?  HOST : "";
			domain.url += "/rest" + menu;
			return domain;
		}
		/*
		public static function get host():String{
			//return isServer() ?  HOST : "http://localhost:8888";
			return isServer() ?  HOST : "";
		}*/
		
		public static function isWas():Boolean{
			if(!checked) isServer();
			return was;
		}
		
		/** * 서버인지? 반드시 자바스크립트를 설정해야 한다.*/
		public static function isServer():Boolean{
			if(!checked){
				var result:Object = Js.call("isServer",null);
				if(result==null){
					was = false;
					server = false;
				}
				else server = result.toString()=="true"? true : false;
				checked = true;
			}
			return server;
		}		
	}
		
}