package erwins.util.lib{

    import erwins.component.popup.TextAreaPopup;
    import erwins.util.json.JsonDebug;
    import erwins.util.json.Jsons;
    
    import flash.events.TimerEvent;
    import flash.utils.Timer;
    
    import mx.controls.Alert;
    import mx.core.UIComponent;
    import mx.events.CloseEvent;
    import mx.managers.PopUpManager;

    public class Alerts{
    	
    	private static var _popupForDebug:TextAreaPopup;
    	
		/** 디버깅용 trace */
    	public static function debugTrace(base:UIComponent,obj:Object):void{
    		var message:String;
    		if(obj==null){
    			message = 'null값이 입력되었습니다.';
    		}else if(Jsons.isReflexive(obj)){
    			message = new JsonDebug(obj).parse();
    		}else message = obj.toString();
    		
    		if(_popupForDebug==null){
    			_popupForDebug = new TextAreaPopup();
				PopUpManager.addPopUp(_popupForDebug,base,false);
				_popupForDebug.title = '디버깅 중입니다.';
				_popupForDebug.text.htmlText = message;
				_popupForDebug.text.height = 600;
				_popupForDebug.text.width = 800;
				_popupForDebug.setFocus();
    		}else _popupForDebug.addTextLine(message);
    	}    	
    	
    	/** 디버깅용 alert */
    	public static function debug(obj:Object):void{
    		if(obj==null){
    			Alert.show('null값이 입력되었습니다.','디버깅중 입니다.');
    			return;
    		}
    		if(Jsons.isReflexive(obj)){
    			Alert.show(new JsonDebug(obj).parse(),'디버깅중 입니다.');
    		}else Alert.show(obj.toString(),'디버깅중 입니다.'); 
    	}
    	
    	/**  라벨과 크기의 조정이 가능 */
  		public static function test( command:Function,title:String="확인해주세요~"):void{
				Alert.buttonWidth = 100;
                Alert.yesLabel = "Cool";
                Alert.noLabel = "Lame";
                Alert.cancelLabel = "Never Mind"
  		}
  		
    	/**  확인 후 command를 실행한다.
    	 * 플래시 10이상일때 보안상 사용자의 이벤트반응 후에만 작동하는 API가 있다. 이런거 할때 유요하다. */
  		public static function confirm( command:Function,message:String="정말로 삭제하시겠습니까? 삭제되면 복구되지 않습니다."
  				,title:String="확인해주세요~"):void{
  					
  			Alert.show(message,title,Alert.YES|Alert.NO,null,function(e:CloseEvent):void{
				switch(e.detail){
					case Alert.YES : command(); return;
					case Alert.NO : return;
				}
			});
  		}
  		
    	/**  확인 후 command를 실행한다. 2가지 옵션 버전. */
  		public static function confirm2(btn1:String,command1:Function,btn2:String,command2:Function,title:String,message:String):void{
  			var temp1:String = Alert.yesLabel;
  			var temp2:String = Alert.okLabel;
  			Alert.yesLabel = btn1;
  			Alert.okLabel = btn2;
  			Alert.show(message,title,Alert.YES|Alert.OK|Alert.CANCEL,null,function(e:CloseEvent):void{
				switch(e.detail){
					case Alert.YES : command1(); return;
					case Alert.OK : command2(); return;
					case Alert.CANCEL : return;
				}
			});
			Alert.yesLabel = temp1;
  			Alert.okLabel = temp2;
  		}
    	
    	/**  delayTime동안 나타났다 사라지는 Alert이다. */
  		public static function show( text:String="", title:String="", delayTime:Number=1500, icon:Class=null ):Alert{
		    var alert:Alert;
		    var timer:Timer;
		
		    timer = new Timer( delayTime, 1 );
		   
		    timer.addEventListener(TimerEvent.TIMER,function(event:TimerEvent):void{
		    	timer.stop();
				mx.managers.PopUpManager.removePopUp( alert );
		    });
		 
		    alert = Alert.show(text, title, 4.0, null, null, icon, 4.0);
		    timer.start(); // 타이머 시작
		
		    return alert;
		}
	}

}