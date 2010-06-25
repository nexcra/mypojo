package erwins.util.http{
	import com.adobe.serialization.json.*;
	
	import erwins.component.ButtonBarForPaging;
	import erwins.util.UILib.KeyboardBinder;
	import erwins.util.lib.Binder;
	
	import flash.events.MouseEvent;
	
	import mx.collections.ArrayCollection;
	import mx.controls.*;
	import mx.core.UIComponent;
	import mx.events.ItemClickEvent;
	import mx.events.ListEvent;
	import mx.utils.*;
	
	/**
	 * 자료 전송이 아닌 페이징 조회시 사용된다.
	 * getter에 바인딩을 하면 내부에서 변화하는 이벤트는 적용되지 않는다. 
	 * 따라서 편의 를 위해 public멤버필드를 사용한다.
	 **/ 
	public class Paging{ 
		
		/** 현제 페이지 번호 */
		[Bindable] public var nowPageNo:int= 1;
		/** 이전 페이지 번호. 이는 너무 많은 페이지를 건너뛰어서 더이상 자료가 없을때 초기화를 위해 필요하다.  */
		[Bindable] public var beforePageNo:int= 1;
		[Bindable] public var nextAble:Boolean = true;
		[Bindable] public var beforeAble:Boolean = true;
		
		/** Hibernate의 rownum이 있을 경우 next / before를 초기화 한다. */
		public function renew(list:ArrayCollection):void{
			beforeAble = nowPageNo != 1;
			if(list==null || list.length <= 1){
				nowPageNo = beforePageNo;
				return;
			} 
			nextAble = list[list.length-1].rownum!=1;
			if(buttonBarForPagingMediator!=null) buttonBarForPagingMediator();
		}
		
		private var fun:Function ;
		private var buttonBarForPagingMediator:Function ;
		
		private var lock:Lockable;
		
		/**
		 * 인자로 받는 fun은 조회를 수행한다.
		 * fun은 조회가 끝난 후 callback을 할 수 있어야 한다.
		 **/ 
		public function Paging(fun:Function,lock:Lockable){
			this.fun = fun;
			this.lock = lock;
		}
		
		public function refresh(callback:Function=null):void{
			search(nowPageNo,callback);
		}		
		public function next():void{
			if(nextAble) search(nowPageNo+1);
		}
		public function before():void{
			if(beforeAble) search(nowPageNo-1);
		}
		
		/**
		 * 요청이 들어오더라도 nowPageNo를 바꾸지 않는다. 
		 * LOck이 걸리면 nowPageNo를 바꾼다.
		 * callback은 fun으로 넘기지 않는 한 별 의미 없다.
		 **/ 
		public function search(terget:int=1,callback:Function=null):void{
			if( terget < 1) return;  //0페이지 요청이면  스킵.
			if(lock.locked) return;
			this.beforePageNo = this.nowPageNo;
			this.nowPageNo = terget;
			try{
				fun(callback);
			}finally{
			}
		}
		
		/** 마우스휠 작동시 한칸씩 이동한다. 이동중의 휠스크롤은 무시된다. */
		public function addMouseWheelAction(base:UIComponent):void{
			base.addEventListener(MouseEvent.MOUSE_WHEEL,function(event:MouseEvent):void{
				var w:int = event.delta;
				if(w>0) before();
				else next(); 
			});
		}
		
		/** 뭐가 됬든 반응시  search하는 버튼을 만들어 준다. */
		public function addListener( ... bases):void{
			for each(var eachBase:Object in bases){
				var ui:UIComponent = 	eachBase as UIComponent;
				if(ui==null) throw new Error(eachBase + " : input must be UIComponent");
				if(ui is Button){
					ui.addEventListener(MouseEvent.CLICK,function(e:MouseEvent):void{
						search();
					});
				}else if(ui is ComboBox){
					ui.addEventListener(ListEvent.CHANGE,function(e:ListEvent):void{
						search();
					});
				}else if(ui is TextInput){
					Binder.onEnter(ui,search);
				}else if(ui is ButtonBarForPaging){
					var btns:ButtonBarForPaging = ui as ButtonBarForPaging;
					
					var keyBinder:KeyboardBinder = new KeyboardBinder(btns);
					var ctrlInterval:int = 10;
					var shiftKeyInterval:int = 5;
					
					btns.addEventListener(ItemClickEvent.ITEM_CLICK,function(e:ItemClickEvent):void{
						switch(e.index){
							case 0 : 
								if(keyBinder.ctrlKey) search(nowPageNo - ctrlInterval);
								else if(keyBinder.shiftKey) search(nowPageNo - shiftKeyInterval);
								else before(); 
								break;
							case 1 :
								if(keyBinder.ctrlKey) search(nowPageNo + ctrlInterval);
								else if(keyBinder.shiftKey) search(nowPageNo + shiftKeyInterval);
								else next(); 
								break;
						}
					});
					buttonBarForPagingMediator = function():void{
						UIComponent(btns.getChildren()[0]).enabled = beforeAble;
						UIComponent(btns.getChildren()[1]).enabled = nextAble;
					};
				}else throw new Error(eachBase + " : is not supported UIComponent");
			}
		}
	}
}