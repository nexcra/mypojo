package erwins.util.validate{
	import com.adobe.serialization.json.*;
	
	import flash.events.MouseEvent;
	
	import mx.controls.Alert;
	import mx.core.UIComponent;
	import mx.events.CloseEvent;
	import mx.events.MenuEvent;
	import mx.utils.*;
	
	/** 사용자의 잘못된 입력으로 인한 에러이다. */
	public class MalformedError extends Error{
		
		private var _source:UIComponent;
		
		public function MalformedError(message:String,source:UIComponent){
			super(message);
			this._source = source;
		}
		
		public function get source():UIComponent{
			return _source;
		}
		
		/** 디폴트 예외처리. */
		public function resolve():void{
			Alert.show(message,"입력 항목을 확인해 주세요.",0x4,null,function(e:CloseEvent):void{
				if(_source!=null){
					_source.setFocus();
					//이거 작동 안함.. ㅠㅠ 왜?
					_source.dispatchEvent(new MenuEvent(MenuEvent.ITEM_CLICK));
					_source.dispatchEvent(new MouseEvent(MouseEvent.CLICK));	
				}
			});
		}

	}
}