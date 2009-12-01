package erwins.util.http{
	import com.adobe.serialization.json.*;
	
	import erwins.util.Json;
	
	import mx.controls.Alert;
	import mx.rpc.events.FaultEvent;
	import mx.rpc.events.ResultEvent;
	import mx.rpc.http.HTTPService;
	import mx.utils.*;
	
	/**
	 * 자바스크립트와 유사하나. interface의 활용이 가능하다. Good!!
	 * */
	public class Ajax{
		
		private var httpS:HTTPService = new HTTPService();
		private var mediator:Mediator = null;
		private var url:String;
		
		public function Ajax(url:String){
			this.url = url;
		}
		
		/** 
		 * success / fail 메소드 보다 mediator가 먼저 호출된다!??  왜?
		 **/	
		public function send(params:Object,success:Function=null,fail:Function=null):void{
			if(params==null) params = new Object();
			httpS.url = url;
			httpS.method="POST";
			httpS.addEventListener("result", function resultHandler(event:ResultEvent):void {
				httpS.removeEventListener("result", resultHandler);
				httpS.disconnect();
				if(mediator!=null) mediator.locked = false;
				if(success!=null){
					var json:Json = new Json(event);
					success(json);
				}
			});
			httpS.addEventListener("fault", function faultHandler(event:FaultEvent):void{
				httpS.removeEventListener("fault", faultHandler);
				httpS.disconnect();
				trace("fault : " + event.fault);
				Alert.show(url+"\n"+ event.fault.toString(),"http request fail!");
				if(mediator!=null) mediator.locked = false;
				if(fail!=null) fail();
			});
			if(mediator!=null) mediator.locked = true; //여기서만 lock해준다.
			httpS.send(params);
		}
		
		public function setMediator(mediator:Mediator):void{
			this.mediator = mediator;
		}
	}
}