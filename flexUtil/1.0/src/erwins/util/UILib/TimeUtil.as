package erwins.util.UILib{
	import com.adobe.serialization.json.*;
	
	import flash.events.TimerEvent;
	import flash.utils.Timer;
	
	import mx.core.UIComponent;
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
		
		/** 사라졌다가 일정시간 지난 휴 다시 보이게 한다.
		 * EventEffect.showHideIris(loginBtn) 처럼 hide/show 이펙트를 미리 줘야 간지난다. */
		public static function hideAndShow(base:UIComponent,duration:int = 2000):void{
			base.setVisible(false);
			fire(function():void{
				base.setVisible(true);
			},duration);
		}

	}
}