package erwins.util.UILib{
	import com.adobe.serialization.json.*;
	
	import erwins.component.PanelToolTip;
	
	import mx.containers.Panel;
	import mx.core.UIComponent;
	import mx.managers.PopUpManager;
	import mx.utils.*;
	
	public class PopupUtil{
		
		private static var _loading:PanelToolTip;
		
		public static function get loading():PanelToolTip{
			if(_loading) return _loading;
			_loading = new PanelToolTip();
			_loading.title = "======= Loading =======";
			_loading.text = "처리중입니다. 잠시만 기다려 주세요.";
			return _loading;
		}
		
		/** base가 있을때 boolean값에 따라 로딩을 나타냈다 사려졌다 한다. */
		public static function progress(base:UIComponent,locked:Boolean,pop:Panel=null):void{
			if(base){
				if(pop==null) pop = PopupUtil.loading;
				if(locked){
					PopUpManager.addPopUp(pop,base,true);
					PopUpManager.centerPopUp(pop);
				}
				else{
					PopUpManager.removePopUp(pop);
				} 
			}
		}
		
		
	}
}