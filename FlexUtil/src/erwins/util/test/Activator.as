package erwins.util.test{
	import com.adobe.serialization.json.*;
	
	import flash.events.Event;
	import flash.events.TimerEvent;
	import flash.utils.Timer;
	
	import mx.core.Application;
	import mx.core.UIComponent;
	import mx.utils.*;
	
	public class Activator{
		
		/** Application에  빙글빙글이를 추가한다. */
		public static function add(base:Application):void{
			
			var rect:UIComponent = new UIComponent();
			rect.graphics.lineStyle(1);
			rect.x = 20;
			rect.y = 20;
			rect.graphics.drawRect(-5,-5,20,10);
			base.addChild(rect);
			
			var timer:Timer = new Timer(50,0); //delay:밀리초 / repeatCount :0이면 무한대
			timer.addEventListener(TimerEvent.TIMER,function(e:TimerEvent):void{
				rect.rotation += 10;
			});
			
			base.addEventListener(Event.ACTIVATE,function(e:Event):void{
				timer.start();
			});
			base.addEventListener(Event.DEACTIVATE,function(e:Event):void{
				timer.stop();
			});
		}
		
		
	}
}