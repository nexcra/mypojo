package erwins.util.http{
	import com.adobe.serialization.json.*;
	
	import erwins.util.UILib.LoadingPopup;
	
	import mx.controls.*;
	import mx.core.UIComponent;
	import mx.managers.CursorManager;
	import mx.utils.*;
	
	/**
	 * Lockable을 지원하는 중계기 이다. 디자인 패턴의 Mediator를 적용할려고 했으나 현실은 좃망. ㅠㅠ
	 * lock의 상태가 변할 때 마다 프로그레스바를 실행해준다.
	 * 각 UI를 정돈해주는 watch는 수동으로 실행한다.
	 **/  
	public class Mediator implements Lockable{
		
		private var watch:Function;
		private var _locked:Boolean = false;
		private var base:UIComponent;
		private var _loading:LoadingPopup;
		
		/** 
		 * watch등록시 lock이 걸릴때 마다watch를 실행시킨다.
		 * base등록시 lock이 걸릴때마다 전역 모달창을 나타낸다.
		 * */
		public function Mediator(base:UIComponent,watch:Function = null){
			this.watch = watch;
			this.base = base;
			this._loading = new LoadingPopup(base);
			
		}
		
		/** 자료의 입력 등이 완전히 끝난 후에 실행해주는 메소드 */
		public function refresh():void{
			watch();
		}
		
		/**
		 * 디폴트로 커서를 변경해 준다.
		 **/ 
		public function set locked(lock:Boolean):void{
			_locked = lock;
			
			if(_locked) CursorManager.setBusyCursor();
			else CursorManager.removeBusyCursor();
			
			_loading.popup(_locked);
		}
		
		public function get locked():Boolean{
			return _locked;
		}
		
	}
}