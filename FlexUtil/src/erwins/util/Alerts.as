package erwins.util{

    import flash.events.TimerEvent;
    import flash.utils.Timer;
    
    import mx.controls.Alert;
    import mx.events.CloseEvent;
    import mx.managers.BrowserManager;
    import mx.managers.PopUpManager;

    public class Alerts{
    	
    	/** 디버깅용 alert */
    	public static function debug(obj:Object):void{
    		if(obj==null){
    			Alert.show('null값이 입력되었습니다.','디버깅중 입니다.');
    			return;
    		}
    		if(Json.isReflexive(obj)){
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
  		
    	/**  확인 후 command를 실행한다. */
  		public static function confirm( command:Function,message:String="정말로 삭제하시겠습니까? 삭제되면 복구되지 않습니다."
  				,title:String="확인해주세요~"):void{
  					
  			Alert.show(message,title,Alert.YES|Alert.NO,null,function(e:CloseEvent):void{
				switch(e.detail){
					case Alert.YES : command(); return;
					case Alert.NO : return;
				}
			});
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