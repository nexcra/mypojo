package erwins.util.http{
	import com.adobe.serialization.json.*;
	
	import erwins.util.json.Jsons;
	import erwins.util.lib.Alerts;
	
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
		
		/** send와 동일하지만 특정 key로 JSON을 전송한다. */
		public function sendJson(params:Object,success:Function=null,fail:Function=null):void{
			var json:String = JSON.encode(params);
			send({jsonParam:json},success,fail);
		}
		
		/** 동일한 mediator를 등록하였다면 해당 mediator당 1개씩의 request만을 던질 수 있다.  */
		public function send(params:Object,success:Function=null,fail:Function=null):void{
			if(mediator!=null && mediator.locked){
				Alerts.debug('only 1 request is available. you needed debug!');
				return;
			}
			if(params==null) params = new Object();
			httpS.url = url;
			httpS.method="POST";
			httpS.addEventListener("result", function resultHandler(event:ResultEvent):void {
				httpS.removeEventListener("result", resultHandler);
				httpS.disconnect();
				if(mediator!=null) mediator.locked = false; 
				if(success!=null){
					var json:Jsons = new Jsons(event);
					success(json);
				}
			});
			httpS.addEventListener("fault", function faultHandler(event:FaultEvent):void{
				httpS.removeEventListener("fault", faultHandler);
				httpS.disconnect();
				if(mediator!=null) mediator.locked = false;
				Alert.show(url+"\n"+ event.fault.toString(),"http request fail!");
				if(fail!=null) fail();
			});
			if(mediator!=null) mediator.locked = true; //여기서만 lock해준다.
			httpS.send(params);
		}
		
		/** 단지 호출 후 Lock을 거는 용도로만 사용한다. */
		public function setMediator(mediator:Mediator):void{
			this.mediator = mediator;
		}
	}
}