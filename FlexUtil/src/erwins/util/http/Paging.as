package erwins.util.http{
	import com.adobe.serialization.json.*;
	
	import flash.events.MouseEvent;
	
	import mx.collections.ArrayCollection;
	import mx.controls.*;
	import mx.core.UIComponent;
	import mx.events.ListEvent;
	import mx.utils.*;
	
	/**
	 * 자료 전송이 아닌 페이징 조회시 사용된다.
	 * getter에 바인딩을 하면 내부에서 변화하는 이벤트는 적용되지 않는다. 
	 * 따라서 편의 를 위해 public멤버필드를 사용한다.
	 **/ 
	public class Paging{ 
		
		/** 현제 페이지 번호 */
		[Bindable]
		public var nowPageNo:int= 1;
		[Bindable]
		public var nextAble:Boolean = true;
		[Bindable]
		public var beforeAble:Boolean = true;
		
		/** Hibernate의 rownum이 있을 경우 next / before를 초기화 한다. */
		public function renew(list:ArrayCollection):void{
			nextAble = list[list.length-1].rownum!=1;
			beforeAble = nowPageNo != 1;
		}
		
		private var fun:Function ;
		
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
			this.nowPageNo = terget;
			try{
				fun(callback);
			}finally{
			}
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
				}else throw new Error(eachBase + " : is not supported UIComponent");
			}
		}
	}
}