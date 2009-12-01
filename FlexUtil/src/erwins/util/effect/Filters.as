package erwins.util.effect{
	import com.adobe.serialization.json.*;
	import com.google.maps.Color;
	
	import flash.filters.BevelFilter;
	import flash.filters.DropShadowFilter;
	
	import mx.core.UIComponent;
	import mx.utils.*;
	
	/** 필요할때 만들자. */
	public class Filters{
		
		private static var _dropShadow:DropShadowFilter;
		private static var _bevel:BevelFilter;
		
		public static function get dropShadow():DropShadowFilter{
			if(_dropShadow) return _dropShadow;
			_dropShadow = new DropShadowFilter();
			_dropShadow.alpha = 0.35;
			_dropShadow.blurX = 6;
			_dropShadow.distance = 3; //6
			_dropShadow.color = Color.BLACK;
			_dropShadow.angle = 90;
			return _dropShadow;
		}
		
		public static function get bevel():BevelFilter{
			if(_bevel) return _bevel;
			_bevel = new BevelFilter();
			_bevel.angle = 45;
			_bevel.blurX = 0.5;
			_bevel.blurY = 0.5;
			_bevel.distance = 4; //6
			_bevel.strength = 0.7;
			_bevel.highlightAlpha = 0.7;
			_bevel.shadowAlpha = 0.7;
			return _bevel;
		}

		
		/**
		 * filter를 선택 적용하는 건 다음에 알아서 고치기.
		 **/
		public static function filter( ... targets):void{
			var filterArr:Array = [Filters.dropShadow,Filters.bevel];
			for each(var target:UIComponent in targets){
				target.filters = filterArr;
			}
		}
		
		/**
		 * <mx:GlowFilter id="glow" blurX="12" blurY="12" color="#88AEF7" quality="2" strength="1"/>
    <mx:BlurFilter id="blur" blurX="4" blurY="4" quality="2" />
    <mx:BevelFilter id="bevel" angle="45" blurX="0.5" blurY="0.5" distance="4" strength="0.7" highlightAlpha="0.7" shadowAlpha="0.7"  />
    * */
		
		
	}
}