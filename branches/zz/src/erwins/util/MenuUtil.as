package erwins.util{
	import com.adobe.serialization.json.*;
	
	import flash.display.Sprite;
	import flash.events.ContextMenuEvent;
	import flash.ui.ContextMenu;
	import flash.ui.ContextMenuItem;
	
	import mx.collections.ArrayCollection;
	import mx.utils.*;
	
	/** FLEX menu 사용시 유용한 기능. */
	public class MenuUtil{
		
		/** 마우스 좌클릭 메뉴를 생성해준다.  마지막의 select이벤트는 마우스 좌표를 얻기 위한 행위로서 없어도 된다. */
		public static function leftClickMenu(base:Sprite,name:String,click:Function,select:Function=null):void{
			var menu:ContextMenu = new ContextMenu();
			if(select!=null){
				menu.addEventListener(ContextMenuEvent.MENU_SELECT, function(e:ContextMenuEvent):void {
			    	select(e);
			    });	
			}
	    
		    var menuItem:ContextMenuItem = new ContextMenuItem(name);
		    menuItem.addEventListener(ContextMenuEvent.MENU_ITEM_SELECT, function(e:ContextMenuEvent):void {
				click(e);
		    });
		    
		    menu.customItems.push(menuItem);
		    menu.hideBuiltInItems();
		    base.contextMenu = menu;
		}
		
		/** 새 컬렉션을 만들어 0번에 디폴트를 추가한다.  */
		public static function defaultOption(collection:ArrayCollection,labelName:String='전체'):ArrayCollection{
			collection = Sets.deepCopy(collection);
			var item:Object = new Object();
			item.labelName = labelName;
			collection.addItemAt(item,0);
			return collection;
		}
		
		/**
		 * 하나의 객체에서 반복 사용됨으로 초기화 후 사용한다. 
		 * 각각 id로 비교한다. 
		 * orgArray에 checks가 포함되어있다면 선택된 상태로 orgArray를 변경한다.
		 **/
		public static function check(orgArray:ArrayCollection,checks:Object):void{
			for each(var init:Object in orgArray) init.toggled = false; 
			for each(var base:Object in orgArray){
				for each(var checked:Object in checks){
					if(base.id == checked.id) base.toggled = true;
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
	}
}