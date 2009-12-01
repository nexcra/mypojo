package erwins.util.effect{
	import com.adobe.serialization.json.*;
	
	import mx.charts.effects.SeriesInterpolate;
	import mx.core.UIComponent;
	import mx.effects.Fade;
	import mx.effects.Glow;
	import mx.effects.Iris;
	import mx.utils.*;
	
	/** 
	 * state이펙트 적용히 해당 target객체가 현재시점에서 존재하는지 잘 봐야 한다.
	 * XML로 코딩시 범용으로 사용은 못하지만 이를 자동으로 적용해준다.
	 **/
	public class EventEffect{
		
		private static var _overGlow:Glow;
		private static var _outGlow:Glow;
		private static var _showFade:Fade;
		private static var _iris:Iris;
		private static var _seriesInterpolate:SeriesInterpolate;
		
		public static function get seriesInterpolate():SeriesInterpolate{
			if(_seriesInterpolate) return _seriesInterpolate;
			var _seriesInterpolate:SeriesInterpolate = new SeriesInterpolate();
			_seriesInterpolate.duration = 800;
			return _seriesInterpolate;
		}		
		
		public static function get iris():Iris{
			if(_iris) return _iris;
			var _iris:Iris = new Iris();
			_iris.duration = 150;
			return _iris;
		}
		
		/** 툴팁에 사용된다. */
		public static function get showFade():Fade{
			if(_showFade) return _showFade;
			var _showFade:Fade = new Fade();
			_showFade.alphaFrom = 0;
			_showFade.alphaTo = 0.8;
			_showFade.duration = 400;
			return _showFade;
		}
		
		public static function get overGlow():Glow{
			if(_overGlow) return _overGlow;
			_overGlow = new Glow();
			_overGlow.color = 0x00FF00;
			_overGlow.alphaFrom = 1;
			_overGlow.alphaTo = 0.3;			
			_overGlow.blurXFrom = 0.0;
			_overGlow.blurXTo = 50.0;
			_overGlow.blurYFrom = 0.0;
			_overGlow.blurYTo = 50.0;
			_overGlow.duration = 500; 
			return _overGlow;
		}
		
		public static function get outGlow():Glow{
			if(_outGlow) return _outGlow;
			_outGlow = new Glow();
			_outGlow.color = 0x0000FF;
			_outGlow.alphaFrom = 0.3;
			_outGlow.alphaTo = 1;			
			_outGlow.blurXFrom = 50.0;
			_outGlow.blurXTo = 1.0;
			_outGlow.blurYFrom = 50.0;
			_outGlow.blurYTo = 1.0;			
			_outGlow.duration = 500;
			return _outGlow;
		}
		
		/**  대충 하면 다 먹힌다. 버튼 등에 하면 잔상이 남는 버그 있음.  **/
		public static function mouseoverGlow( ... targets):void{
			for each(var target:UIComponent in targets){
				target.setStyle("rollOverEffect",EventEffect.overGlow);		
				target.setStyle("rollOutEffect",EventEffect.outGlow);
			}
		}
		
		public static function showHideIris( ... targets):void{
			for each(var target:UIComponent in targets){
				target.setStyle("hideEffect",EventEffect.iris);		
				target.setStyle("showEffect",EventEffect.iris);
			}			
		}
		
		
	}
}