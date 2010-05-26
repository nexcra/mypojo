package erwins.util.UILib{
	import com.adobe.serialization.json.*;
	
	import mx.core.Container;
	import mx.core.UIComponent;
	import mx.events.FlexEvent;
	import mx.events.IndexChangedEvent;
	import mx.utils.*;
	
	/** 네이게이터 등 늦의 늦은 로딩이 되는 컨테이너는 해당 객체가 가진 컨테이너가 클릭될때 내용물을 생성하고 create_complite이벤트를 디스패치 한다.
	 * 즉 이벤트  직전까지 해당 id의 객체는 null이다. */
	public  class InitUtil{
		
		/** base객체는 주로 네비게이터에 담긴 최초의 컨테이너이다.
		 * 즉 id는 있으나 CREATION_COMPLETE이벤트가 발생하지는 않은 객체 이다.  */
		public static function init(base:UIComponent,callback:Function):void{
			var fun:Function = function(e:FlexEvent):void{
				callback();
				base.removeEventListener(FlexEvent.CREATION_COMPLETE,fun);
			};
			base.addEventListener(FlexEvent.CREATION_COMPLETE,fun);
		}
		
		/** CREATION_COMPLETE보다 INITIALIZE가 늦는 객체(탭, 뷰스택 등)에 사용 */
		public static function initialize(base:UIComponent,callback:Function):void{
			var fun:Function = function(e:FlexEvent):void{
				callback();
				base.removeEventListener(FlexEvent.INITIALIZE,fun);
			};
			base.addEventListener(FlexEvent.INITIALIZE,fun);
		}
		
		/** init과 비슷하나  스택류가 변경될때마다 호출한다.  */
		public static function addEventByIndex(base:Container,index:int,callback:Function):void{
			base.addEventListener(IndexChangedEvent.CHANGE,function(e:IndexChangedEvent):void{
				if(e.newIndex==index){
					callback();
				}
			});
		}		
				
		/** init과 비슷하나 바로 이벤트를 달아줄때... 쓸일 없을듯.  */
		public static function addEvent(base:UIComponent,eventName:String,callback:Function):void{
			var fun:Function = function(e:FlexEvent):void{
				base.addEventListener(eventName,callback);
				base.removeEventListener(FlexEvent.CREATION_COMPLETE,fun);
			};
			base.addEventListener(FlexEvent.CREATION_COMPLETE,fun);
		}
	}
}