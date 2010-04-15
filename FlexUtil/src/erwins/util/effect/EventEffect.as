package erwins.util.effect{
	import com.adobe.serialization.json.*;
	
	import mx.charts.effects.SeriesInterpolate;
	import mx.charts.effects.SeriesSlide;
	import mx.charts.effects.SeriesZoom;
	import mx.core.UIComponent;
	import mx.effects.Blur;
	import mx.effects.Fade;
	import mx.effects.Glow;
	import mx.effects.Iris;
	import mx.effects.Parallel;
	import mx.effects.Resize;
	import mx.effects.Sequence;
	import mx.effects.Zoom;
	import mx.effects.easing.Bounce;
	import mx.utils.*;
	
	/** 
	 * state이펙트 적용히 해당 target객체가 현재시점에서 존재하는지 잘 봐야 한다.
	 * XML로 코딩시 범용으로 사용은 못하지만 이를 자동으로 적용해준다.
	 **/
	public class EventEffect{
		
		/* ============================== 범용 =============================== */
		
		private static var _overGlow:Glow;
		private static var _outGlow:Glow;
		private static var _showFade:Fade;
		private static var _iris:Iris;

		/** 글자 등에 대충 다 먹히는 기본이펙트. */
		public static function get iris():Iris{
			if(_iris) return _iris;
			var _iris:Iris = new Iris();
			//_iris.duration = 150;
			_iris.duration = 250;
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
		
		
		/** 마우스 오버 */
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
		
		/** 마우스 아웃 */
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
		
		/** 문자열 등에 적용. */
		public static function showHideIris( ... targets):void{
			for each(var target:UIComponent in targets){
				target.setStyle("hideEffect",EventEffect.iris);		
				target.setStyle("showEffect",EventEffect.iris);
			}			
		}
		
		/** 이펙트 추가된 문자열show : result메세지에 쓰인다. */
		public static function get showText():Parallel{
			var p:Parallel = new Parallel();
			var r:Resize = new Resize();
			r.duration = 1500;
			r.easingFunction = Bounce.easeOut;
			var s:Sequence = new Sequence();
			var b1:Blur = new Blur();
			b1.duration = 400;
			b1.blurYFrom = 1.0;
			b1.blurYTo = 20.0;
			var b2:Blur = new Blur();
			b2.duration = 400;
			b2.blurYFrom = 20.0;
			b2.blurYTo = 1.0;
			s.addChild(b1);
			s.addChild(b2);
			p.addChild(r);
			p.addChild(s);
			return p;
		}	
		
		/**  타이틀 윈도우 ON 
		 *  ex) creationCompleteEffect="{EventEffect.onWindow}" **/
		public static function get onWindow():Sequence{
			var s:Sequence = new Sequence();
			var p:Parallel = new Parallel();
			var f:Fade = new Fade();
			f.duration = 800;
			p.addChild(f);
			s.addChild(p);
			return s;
		}
				
		/**  타이틀 윈도우 OFF 
		 * ex) removedEffect="{EventEffect.offWindow}" **/
		public static function get offWindow():Sequence{
			var s:Sequence = new Sequence();
			var p:Parallel = new Parallel();
			p.addChild(new Fade());
			p.addChild(new Zoom());
			s.addChild(p);
			return s;
		}			
		
		
		/* ============================== 챠트용 =============================== */
		
		private static var _seriesInterpolate:SeriesInterpolate;
		private static var _seriesZoom:SeriesZoom;
		private static var _seriesSlideUp:SeriesSlide;
		private static var _seriesSlideDown:SeriesSlide;
		
		/** 챠트-인터폴레이트 */
		public static function get seriesInterpolate():SeriesInterpolate{
			if(_seriesInterpolate) return _seriesInterpolate;
			var _seriesInterpolate:SeriesInterpolate = new SeriesInterpolate();
			_seriesInterpolate.duration = 800;
			return _seriesInterpolate;
		}
		
		/** 챠트-줌 (in/out 공용임. line챠트에서는 안먹는듯... 당연한가?) */
		public static function get seriesZoom():SeriesZoom{
			if(_seriesZoom) return _seriesZoom;
			var _seriesZoom:SeriesZoom = new SeriesZoom();
			_seriesZoom.duration = 1000;
			return _seriesZoom;
		}
		
		/** 챠트-슬라이드in */
		public static function get seriesSlideUp():SeriesSlide{
			if(_seriesSlideUp) return _seriesSlideUp;
			var _seriesSlideUp:SeriesSlide = new SeriesSlide();
			_seriesSlideUp.duration = 1000;
			_seriesSlideUp.direction = "up";
			return _seriesSlideUp;
		}
		
		/** 챠트-슬라이드ioutn */
		public static function get seriesSlideDown():SeriesSlide{
			if(_seriesSlideDown) return _seriesSlideDown;
			var _seriesSlideDown:SeriesSlide = new SeriesSlide();
			_seriesSlideDown.duration = 1000;
			_seriesSlideUp.direction = "down";
			return _seriesSlideDown;
		}		
		
	}
}