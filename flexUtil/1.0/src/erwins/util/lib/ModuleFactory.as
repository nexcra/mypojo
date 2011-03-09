package erwins.util.lib{
	import com.adobe.serialization.json.*;
	
	import erwins.util.UILib.TimeUtil;
	
	import flash.display.DisplayObject;
	import flash.utils.Timer;
	
	import mx.controls.Alert;
	import mx.events.ModuleEvent;
	import mx.managers.CursorManager;
	import mx.modules.IModuleInfo;
	import mx.modules.ModuleManager;
	import mx.utils.*;
	public  class ModuleFactory{
		
		public static var map:Object = new Object();
		
		public static function count(url:String):int{
			return map[url] as int;
		}
		
		/** 기존 로드된 모듈이라면  요청을 무시한다. */
		public static function loadUnique(url:String,callback:Function):void{
			if(map[url]) return;
			var info:IModuleInfo = ModuleManager.getModule(url);
			info.addEventListener(ModuleEvent.ERROR, function(e:ModuleEvent):void{
				trace('ModuleEvent.ERROR : ' + url);
				Alerts.debug("load Error : " + e.errorText);
				CursorManager.removeBusyCursor();
			});
			var func:Function = function(e:ModuleEvent):void{
				trace('ModuleEvent.READY : ' + url);
				var result:Object =  info.factory.create();
				CursorManager.removeBusyCursor();
				if(result==null) Alert.show("로드한 모듈이 null입니다 URL을 확인하세요\n" + url); 
				else{
					map[url] = true;
					callback(result);
					//info.removeEventListener(ModuleEvent.READY,func);
				}
			};
			
			info.addEventListener(ModuleEvent.READY,func);
			
			var count:int = 0;
			info.addEventListener(ModuleEvent.SETUP,function():void{
				trace('ModuleEvent.SETUP : ' + url);
				/** 혹시나 가끔 READY이벤트가 안될때가 있어서 타이머를 부착시켰다. 기동될때까지 load를 날려준다.  */
				var tt:Timer = TimeUtil.fire(function():void{
					if(!map[url]){
						count++;
						if(count>2) return;
						info.load();
						tt.start();
						trace('ModuleEvent.READY is not fired. Do reload : ' + url);
					}
				},500);
			});
			info.load();
			CursorManager.setBusyCursor();
		}		
		
		/** 기존 로드된 모듈이라면 카운트를 하나 올리고 리턴해 준다. - 사용중지 */
		public static function load(url:String,callback:Function):void{
			var info:IModuleInfo = ModuleManager.getModule(url);
			if(map[url]==null){
				info.addEventListener(ModuleEvent.READY, function(e:ModuleEvent):void{
					CursorManager.removeBusyCursor();
					map[url] = 1;
					callback(info.factory.create() as DisplayObject);
				},false,0,true);
				info.load();
				CursorManager.setBusyCursor();
			}else{
				CursorManager.removeBusyCursor();
				info.load();
				map[url] = map[url] + 1;
				callback(info.factory.create() as DisplayObject);
			}
		}
	}
}