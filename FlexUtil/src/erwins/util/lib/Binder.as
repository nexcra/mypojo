package erwins.util.lib{
	import com.adobe.serialization.json.*;
	
	import flash.display.DisplayObject;
	import flash.events.Event;
	import flash.events.KeyboardEvent;
	import flash.events.MouseEvent;
	
	import mx.controls.TextArea;
	import mx.core.UIComponent;
	import mx.utils.*;
	public class Binder{
		
		/**
		 * Enter키를 바인딩 한다. 2번째 인자인 btn이 있다면 추가로 click을 바인딩 해준다.
		 **/
		public static function onEnter(target:Object,fun:Function,btn:UIComponent=null):void{
			target.addEventListener(KeyboardEvent.KEY_UP,function(e:KeyboardEvent):void{
				var code:uint =  e.charCode;
				if(code==13) fun();
			});
			if(btn!=null){
				btn.addEventListener(MouseEvent.CLICK,function(e:MouseEvent):void{
					fun();
				});
			}
		}
		
		/** 
		 * ctrl키와 동시에 진행되는 키press를 바인딩 한다.
		 * fun이 해당되는 이벤트라면 true를, 아니라면 false를 리턴한다. 
		 * 16 : Shift
		 * */
		public static function ctrl(target:DisplayObject,fun:Function):void{
			target.addEventListener(KeyboardEvent.KEY_DOWN,function(e:KeyboardEvent):void{
				if(!e.ctrlKey) return;
				if(e.keyCode==17) return;
				if(!fun(e.keyCode)) return;
				e.stopPropagation();
				e.preventDefault();
			});
		}
		
		/** 예를 들자면 컨트롤 + 시프트 키는~ */
		public static function ctrlShift(target:DisplayObject,fun:Function):void{
			Binder.ctrl(target,function(e:uint):void{
				if(e==16) fun();
			});
		}
		
		/** 한글2바이트, 영문 1바이트를 고려한 Max제한. HTML은 안된다.  
		 * substr할때 한글길이는 1개로 인식되기 때문에 사이가 줄어들때까지 잘라준다. (속도 책임 못짐)*/
		public static function constraintTextAreaMax(target:TextArea,max:int,callback:Function=null):void{
			target.addEventListener(Event.CHANGE,function(e:Event):void{
				var text:String = target.text;
				var size:int = Strings.textLength(text);
				if(size < max) return;
				text = text.substr(0,max-1);
				size = Strings.textLength(text);
				while(size > max){
					text = text.substr(0,text.length-1);
					size = Strings.textLength(text);
				}
				target.text = text;
				if(callback!=null) callback(size);
			});
		}		


	}
}