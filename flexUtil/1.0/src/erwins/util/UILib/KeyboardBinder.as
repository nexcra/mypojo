package erwins.util.UILib{
	import com.adobe.serialization.json.*;
	
	import flash.events.KeyboardEvent;
	
	import mx.core.UIComponent;
	import mx.utils.*;
	
	/** 키보드의 컨트롤/쉬프트를 감지해서 현재 눌려져있는지 검사한다. */
	public class KeyboardBinder{
		
		private var _ctrlKey:Boolean = false;
		private var _shiftKey:Boolean = false;
		
		public function get ctrlKey():Boolean{
			return _ctrlKey;
		}
		public function get shiftKey():Boolean{
			return _shiftKey;
		}
		
		public function KeyboardBinder(btns:UIComponent):void{
			/** 최초 버튼을 눌렀을때 컨트롤과 쉬프트를 인지한다.  */
			btns.addEventListener(KeyboardEvent.KEY_DOWN,function(e:KeyboardEvent):void{
				if(e.ctrlKey) _ctrlKey = true;
				if(e.shiftKey) _shiftKey = true;
			});
			
			/** 타 버튼과 중복 눌렀을 경우를 구분하기 위해 전수검사. */
			btns.addEventListener(KeyboardEvent.KEY_UP,function(e:KeyboardEvent):void{
				_ctrlKey = e.ctrlKey;
				_shiftKey = e.shiftKey;
			});
		}

	}
}