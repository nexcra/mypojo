package erwins.util.validate{
	import com.adobe.serialization.json.*;
	
	import erwins.util.*;
	import erwins.util.UILib.MenuUtil;
	import erwins.util.json.Jsons;
	import erwins.util.lib.Alerts;
	
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
		
		/** JSON값으로 변경해준다. */
		public function toJson():String{
			return JSON.encode(param);
		}
		
		// ======================================================================================= */
		// =================================== 단일값 ============================================= */
		
		public function addAll( ... components):void{
			for each(var item:Object in components){
				add(item as UIComponent);
			}
		}
		
		/** 특이한 경우. ManyToOne으로 연결될때 사용한다. */
		public function addEntity(component:UIComponent):CheckValue{
			var value:String = Jsons.getValue(component);
			if(tempId==null){
				var key:String = component.id; 
				param[key] = new Object();
				param[key].id = value;
			} 
			else{
				param[tempId] = new Object();
				param[tempId].id = value;
				tempId = null;
			}
			return new CheckValue(value,component);
		}
		
		public function add(component:UIComponent):CheckValue{
			var value:String = Jsons.getValue(component);
			if(value == "") value = null;
			if(tempId==null){
				var key:String = component.id; 
				if(key==Jsons.ENTITY_ID) param["id"] = value;  //예약어인 id를 대체해준다.
				param[key] = value; //이것을 스킵하기않고 넣어준다.
			} 
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