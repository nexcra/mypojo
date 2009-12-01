package erwins.util.validate{
	import com.adobe.serialization.json.*;
	
	import erwins.util.*;
	
	import mx.collections.ArrayCollection;
	import mx.controls.*;
	import mx.core.UIComponent;
	import mx.utils.*;
	
	
	/** 
	 * json으로 변환해주는 기본 벨리데이터. 
	 * 다건 입력의 경우 고려해 보기.
	 * */
	public class Validator{
		
		public var param:Object = new Object;
		
		/** component.id 대신 지정된 값을 파라메터로 사용한다. */
		public var tempId:String = null;
		
		public function id(tempId:String):Validator{
			this.tempId = tempId;
			return this;
		}
		
		// ======================================================================================= */
		// =================================== 단일값 ============================================= */
		
		public function add(component:UIComponent):CheckValue{
			var value:String = Json.getValue(component);
			if(tempId==null) param[component.id] = value;
			else{
				param[tempId] = value;
				tempId = null;
			}
			return new CheckValue(value,component);
		}
		
		// ======================================================================================= */
		// =================================== 배열값 ============================================= */		
		
		public function addCheckedArray(collection:ArrayCollection):CheckValue{
			var values:Array = MenuUtil.checkedArrayForHttp(collection);
			if(tempId==null) throw new Error("소스코드에 id를 입력하세요");
			param[tempId] = values;
			tempId = null;
			return new CheckValue(values);
		}
		
	}
}