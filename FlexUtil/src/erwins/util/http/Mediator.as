package erwins.util.http{
	import com.adobe.serialization.json.*;
	
	import erwins.util.UILib.PopupUtil;
	
	import mx.controls.*;
	import mx.core.UIComponent;
	import mx.managers.CursorManager;
	import mx.utils.*;
	
	/**
	 * Lockable을 지원하는 중계기 이다.
	 * lock의 상태가 변할 때 마다 실행해준다. 등록된 펑션이 없다면 커서만 변경해준다.
	 **/  
	public class Mediator implements Lockable{
		
		private var watch:Function;
		private var _locked:Boolean = false;
		private var base:UIComponent;
		
		/** 
		 * watch등록시 lock이 걸릴때 마다watch를 실행시킨다.
		 * base등록시 lock이 걸릴때마다 전역 모달창을 나타낸다.
		 * */
		public function Mediator(base:UIComponent=null,watch:Function = null){
			this.watch = watch;
			this.base = base;
		}
		
		/**
		 * 디폴트로 커서를 변경해 준다.
		 **/ 
		public function set locked(lock:Boolean):void{
			_locked = lock;
			
			if(_locked) CursorManager.setBusyCursor();
			else CursorManager.removeBusyCursor();
			
			if(watch!=null) watch();
			
			PopupUtil.progress(base,_locked);
			
		}
		
		public function get locked():Boolean{
			return _locked;
		}
		
	}
}