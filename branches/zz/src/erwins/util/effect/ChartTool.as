package erwins.util.effect{
	import com.adobe.serialization.json.*;
	import com.google.maps.Color;
	
	import flash.filters.BevelFilter;
	
	import mx.core.UIComponent;
	import mx.graphics.SolidColor;
	import mx.utils.*;
	
	/** 필요할때 만들자. */
	public class ChartTool{
		
		public static function get fill():Array{
			var list:Array = new Array();
			
			var c1:SolidColor = new SolidColor();
			c1.color = Color.BLUE;
			var c2:SolidColor = new SolidColor();
			c2.color = Color.GREEN;
			var c3:SolidColor = new SolidColor();
			c3.color = Color.RED;			
			var c4:SolidColor = new SolidColor();
			c4.color = Color.YELLOW;			
			var c5:SolidColor = new SolidColor();
			c5.color = Color.GRAY12;
			var c6:SolidColor = new SolidColor();
			c6.color = Color.CYAN;			
			var c7:SolidColor = new SolidColor();
			c7.color = Color.MAGENTA;			
			list.push(c1,c2,c3,c4,c5,c6,c7);
			
			for each(var color:SolidColor in list){
				color.alpha = 0.9;
			}
			return list;
		}
		
	}
}