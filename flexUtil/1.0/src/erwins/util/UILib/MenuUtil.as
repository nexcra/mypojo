package erwins.util.UILib{
	import com.adobe.serialization.json.*;
	
	import erwins.util.lib.Sets;
	
	import flash.events.MouseEvent;
	
	import mx.collections.ArrayCollection;
	import mx.controls.MenuBar;
	import mx.controls.menuClasses.MenuBarItem;
	import mx.events.MenuEvent;
	import mx.utils.*;
	
	/** FLEX menu 사용시 유용한 기능. */
	public class MenuUtil{
		
		/** 새 컬렉션을 만들어 0번에 디폴트를 추가한다.  */
		public static function defaultOption(collection:ArrayCollection,text:String='전체',lablename:String='label'):ArrayCollection{
			collection = Sets.deepCopy(collection);
			var item:Object = new Object();
			item[lablename] = text;
			collection.addItemAt(item,0);
			return collection;
		}
		
		/** 기존 컬렉션의  0번에 디폴트를 추가한다.  */
		public static function addDefaultOption(collection:ArrayCollection,text:String='전체',lablename:String='label'):ArrayCollection{
			var item:Object = new Object();
			item[lablename] = text;
			collection.addItemAt(item,0);
			return collection;
		}
		
		/**
		 * 하나의 객체에서 반복 사용됨으로 초기화 후 사용한다. 
		 * 각각 id or value로 비교한다. 
		 * orgArray에 checks가 포함되어있다면 선택된 상태로 orgArray를 변경한다.
		 * args : checks는 map이나 list형태의 배열 이여야 한다.
		 * 주의 : 체크 이후 dataProvider를 다시 지정해줘여 화면이 refresh된다.
		 **/
		public static function check(orgArray:ArrayCollection,checks:Object=null):void{
			for each(var init:Object in orgArray) init.toggled = false; 
			if(checks==null) return;
			for each(var base:Object in orgArray){
				for each(var checked:Object in checks){
					if(base.id!=null && base.id == checked.id) base.toggled = true;
					else if(base.value!=null && base.value == checked.value) base.toggled = true;
				}
			}
		}
		
		/**
		 * checked된 id를 배열로 리턴한다. (deep copy)
		 * http형식으로 보내려면 배열은 Collection이 아닌 Array이여야 하며  List<String> 이여야 한다.
		 **/  
		public static function checkedArrayForHttp(orgArray:ArrayCollection):Array{
			var result:Array = new Array();
			for each(var base:Object in orgArray){
				if(base.toggled){
					var temp:String = base.id;
					if(temp==null) temp = base.value;
					result.push(temp);
				}
			}
			return result;
		}
		
		/**
		 * checked된 것을 배열로 리턴한다. (shallow copy)
		 **/  
		public static function checkedArray(orgArray:ArrayCollection):ArrayCollection{
			var result:ArrayCollection = new ArrayCollection();
			for each(var base:Object in orgArray){
				if(base.toggled) result.addItem(base);
			}
			return result;
		}
		
		/**
		 * 기본 이벤트는 ROOT의 경우 클릭에 반응하지 않는다. 이를 고쳐서 전부 반응하게 만들긔
		 **/  
		public static function click(menu:MenuBar,callback:Function):void{
			menu.addEventListener(MenuEvent.ITEM_CLICK,function(e:MenuEvent):void{
				callback(e.item);
			});
			menu.addEventListener(MouseEvent.CLICK,function(e:MouseEvent):void{
				var item:MenuBarItem = e.target as MenuBarItem;
				callback(item.data);
			});
		}
	}
}