package erwins.util.UILib{
	import com.adobe.serialization.json.*;
	
	import erwins.component.PanelToolTip;
	
	import mx.core.UIComponent;
	import mx.managers.PopUpManager;
	import mx.utils.*;
	
	public class LoadingPopup{
		
		private var _loading:PanelToolTip;
		private var _base:UIComponent;
		
		public function LoadingPopup(base:UIComponent):void{
			_base = base;
			_loading = new PanelToolTip();
			_loading.title = "======= Loading =======";
			_loading.text = "처리중입니다. 잠시만 기다려 주세요.";
		}
		
		public function set title(title:String):void{
			_loading.title = title;
		}
		public function set text(text:String):void{
			_loading.text = text;
		}
		
		/** base가 있을때 boolean값에 따라 로딩을 나타냈다 사려졌다 한다. */
		public function popup(locked:Boolean):void{
			if(locked){
				PopUpManager.addPopUp(_loading,_base,true);
				PopUpManager.centerPopUp(_loading);
			}
			else PopUpManager.removePopUp(_loading);
		}
		
		
	}
}