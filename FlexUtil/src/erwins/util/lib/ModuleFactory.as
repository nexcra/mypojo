package erwins.util.lib{
	import com.adobe.serialization.json.*;
	
	import erwins.util.lib.Alerts;
	
	import flash.display.DisplayObject;
	
	import mx.controls.Alert;
	import mx.events.ModuleEvent;
	import mx.managers.CursorManager;
	import mx.modules.IModuleInfo;
	import mx.modules.ModuleManager;
	import mx.utils.*;
	public  class ModuleFactory{
		
		public static var map:Object = new Object();
		public static var click:Object = new Object();
		public static var busy:Boolean = false;
		
		public static function count(url:String):int{
			return map[url] as int;
		}
		
		/** 기존 로드된 모듈이라면  요청을 무시한다. */
		public static function loadUnique(url:String,callback:Function):void{
			var info:IModuleInfo = ModuleManager.getModule(url);
			if(map[url]==null){
				info.addEventListener(ModuleEvent.ERROR, function(e:ModuleEvent):void{
					Alerts.debug("load Error : " + e.errorText);
				});
				var func:Function = function(e:ModuleEvent):void{
					trace('ModuleEvent.READY');
					var result:Object =  info.factory.create();
					CursorManager.removeBusyCursor();
					if(result==null) Alert.show("로드한 모듈이 null입니다 URL을 확인하세요\n" + url); 
					else{
						map[url] = 1;
						callback(result);
						info.removeEventListener(ModuleEvent.READY,func);
					}
				};
				info.addEventListener(ModuleEvent.READY,func);
				//왜인지 이거 추가하니까 잘됨?????
				info.addEventListener(ModuleEvent.SETUP,function():void{
					trace('ModuleEvent.SETUP');
				});
				info.load();
				CursorManager.setBusyCursor();
			}
			//가끔 두번 클릭해야 뜰때까 있다. 그때 마우스 비지 방지.
			if(click[url]) CursorManager.removeBusyCursor();
			click[url] = true;
		}		
		
		/** 기존 로드된 모듈이라면 카운트를 하나 올리고 리턴해 준다. */
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