package erwins.util.UILib{
	import com.adobe.serialization.json.*;
	
	import flash.events.TimerEvent;
	import flash.utils.Timer;
	
	import mx.utils.*;
	public class TimeUtil{
		
		/**
		 * 시간이 지난후 트리거를 실행한다. 
		 * 이벤트 디스패치 순서상 작동하지않는 부분을 간단히 처리할때도 유용히 사용할 수 있다. (권장하지는 않는다) 
		 * 다음과 같이 running중 이벤트를 컨트롤 가능하다. if(before!=null && before.running) before.stop();
		 **/
		public static function fire(fun:Function,duration:int = 2000):Timer{
			var timer:Timer = new Timer( duration, 1 );
		    timer.addEventListener(TimerEvent.TIMER,function(event:TimerEvent):void{
		    	timer.stop();
				fun();
		    });
			timer.start(); // 타이머 시작
			return timer; 
		}

	}
}