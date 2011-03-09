package erwins.component{

import mx.containers.HBox;

	/** 숫자를 이쁘게 보여준다. 그리 쓸일이 많아보이진 않는다. 성능보장못함. */
	public class NumberRoller extends HBox{
		
		private var _value:int;
	
		public function set value(value:int):void{
			
			this._value = value;
			
			var list:Array = this.getChildren();
			
			var number:String = value.toString();
			
			while(number.length > list.length){
				var added:NumberRollerInstance = new NumberRollerInstance();
				added.setStyle("fontSize",_numberFontSize);
				this.addChildAt(added,0);
				list = this.getChildren();
			}
			
			while(number.length < list.length){
				this.removeChildAt(list.length-1);
				list = this.getChildren();
			}
			
			for(var index:int=0;index<number.length;index++){
				var num:int = Number(number.charAt(index));
				var target:NumberRollerInstance = list[index] as NumberRollerInstance;
				target.value = num;
				
			}
			this.invalidateProperties();
		
		}
		
		private var _numberFontSize:int = 15;
		
		public function set numberFontSize(value:int):void{
			this._numberFontSize = value;
		}
		
		public function get value():int{
			return _value;
		}
	
	}
}