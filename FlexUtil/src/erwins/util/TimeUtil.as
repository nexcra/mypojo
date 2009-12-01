package erwins.util{
	import com.adobe.serialization.json.*;
	
	import flash.events.TimerEvent;
	import flash.utils.Timer;
	
	import mx.utils.*;
	public class TimeUtil{
		
		/**
		 * 시간이 지난후 트리거를 실행한다.
		 **/
		public static function fire(fun:Function,duration:int = 2000):void{
			var timer:Timer = new Timer( duration, 1 );
		    timer.addEventListener(TimerEvent.TIMER,function(event:TimerEvent):void{
		    	timer.stop();
				fun();
		    });
			timer.start(); // 타이머 시작
		}

	}
}