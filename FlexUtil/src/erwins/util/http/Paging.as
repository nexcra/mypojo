package erwins.util.http{
	import com.adobe.serialization.json.*;
	
	import mx.controls.*;
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
			search(nowPageNo+1);
		}
		public function before():void{
			search(nowPageNo-1);
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
	}
}