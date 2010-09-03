package erwins.util.UILib{
	
	import erwins.util.lib.Colors;
	
	import mx.charts.LinearAxis;
	import mx.controls.advancedDataGridClasses.AdvancedDataGridColumn;
	import mx.formatters.NumberFormatter;
	
	/** 라벨 평선 유틸 : 전부 다 사용 가능하다.  나머지는 Advance전용 */
	public class LFUtil{
		
		// ========================= 라벨 펑션  ============================== //
		
		/** 8자리 문자를 년월일로 바꿔준다. 어드밴스드에도 가능해야 함으로 *로 지정한다.
		 * ex) labelFunction="{LabelFunctions.toDate('jobDt')}" */
		public static function toDate(field:String):Function{
			var fun:Function = function(item:Object,column:*):String{
				var value:String = item[field];
				if(value==null || value=='') return null;
				if(value.length!=8) throw new Error(value+" date length must be 8!");
				return value.substring(0,4)+"년" + value.substring(4,6)+"월" + value.substring(6,8) + "일";
			}
			return fun;
		}
		
		/** 4자리마다 ,를 찍어준다.
		 * ex) labelFunction="{LabelFunctions.toNumeric('sum')}" */
		public static function toNumeric(field:String):Function{
			var fun:Function = function(item:Object,column:*):String{
				var value:String = item[field];
				return numberFormatter.format(value);
			}
			return fun;
		}
		
		// ========================= Advanced 전용 스타일펑션 ============================== //
		
		/** 4자리마다 ,를 찍어준다. 
		 * ex) formatter="{LabelFunctions.numberFormatter} */
		[Bindable] public static var numberFormatter:NumberFormatter = new NumberFormatter();		
		
		/** 빨갛게~ 만들어 준다.
		 * AdvancedDataGrid에 붙이면 로우전체가 영향받고,AdvancedDataGridColumn에 붙이면 해당 컬럼만 영향받는다.
		 * ex) styleFunction="{LabelFunctions.toRed(function(data:Object):Boolean{return data.sum>0})} */
		public static function toRed(func:Function):Function{
			var temp:Function = function(data:Object,col:AdvancedDataGridColumn):Object{
				if(func(data)==true) return {color:0xFF0000,fontWeight:"bold"};
				return null;
			}
			return temp;
		}
		/** 빨갛게~ 만들어 준다.
		 * AdvancedDataGrid에 붙이면 로우전체가 영향받고,AdvancedDataGridColumn에 붙이면 해당 컬럼만 영향받는다.
		 * ex) styleFunction="{LabelFunctions.toBlue(function(data:Object):Boolean{return data.sum>0})} */
		public static function toBlue(func:Function):Function{
			var temp:Function = function(data:Object,col:AdvancedDataGridColumn):Object{
				if(func(data)==true) return {color:Colors.BLUE,fontWeight:"bold"};
				return null;
			}
			return temp;
		}
		
		// ========================= 챠트 ============================== //
		
		/** 세로축의 숫자형 데이터를 포매팅 해준다.
		 * <mx:verticalAxis><mx:LinearAxis labelFunction="{LabelFunctions.linearAxisToNumeric('건')}" /></mx:verticalAxis> */
		public static function linearAxisToNumeric(str:String=''):Function{
			var temp:Function = function(value:Number,previousValue:Number,axis:LinearAxis):String{
				return numberFormatter.format(value) + str;
			};
			return temp;
		}
		
		//가로축 데이터
		//<mx:horizontalAxis><mx:CategoryAxis id="xLine" categoryField="jobDt" labelFunction="qwe"/></mx:horizontalAxis> 
		//var qwe:Function = function(value:Object,previousValue:Object,axis:CategoryAxis,item:Object):String{
		
	}
}