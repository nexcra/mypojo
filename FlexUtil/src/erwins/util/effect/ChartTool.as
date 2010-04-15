package erwins.util.effect{
	import com.adobe.serialization.json.*;
	import com.google.maps.Color;
	
	import mx.graphics.GradientEntry;
	import mx.graphics.RadialGradient;
	import mx.graphics.SolidColor;
	import mx.utils.*;
	
	/** 필요할때 만들자. */
	public class ChartTool{
		
		/** 원형 그라디언트 빌더 (파이챠트용) */
		private static function build(color1:uint,color2:uint):RadialGradient{
			var temp:RadialGradient = new RadialGradient();
			var entry1:GradientEntry = new GradientEntry();
			entry1.color = color1;
			entry1.ratio = 0;
			
			var entry2:GradientEntry = new GradientEntry();
			entry2.color = color2;
			entry2.ratio = 1;
			temp.entries = [entry1,entry2];
			return temp;
		}
		
		public static function get fillRadialGradient():Array{
			return [build(Color.BLUE,0x4E82FF),
					build(Color.GREEN,0x78FF87),
					build(Color.RED,0xFE6137),
					build(Color.YELLOW,0xF7FF6E),
					build(Color.GRAY12,0xD3D3D2),
					build(Color.CYAN,0xC3F4FA)
				];
		}		
		
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