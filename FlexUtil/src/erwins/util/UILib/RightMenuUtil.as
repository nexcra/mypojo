package erwins.util.UILib{
	import com.adobe.serialization.json.*;
	
	import flash.display.Sprite;
	import flash.events.ContextMenuEvent;
	import flash.ui.ContextMenu;
	import flash.ui.ContextMenuItem;
	
	import mx.utils.*;
	public  class RightMenuUtil{
		
		private var menu:ContextMenu;
		
		/** 마우스 우클릭 메뉴를 생성해준다.  마지막의 select이벤트는 마우스 좌표를 얻기 위한 행위로서 없어도 된다. */
		public static function create(base:Sprite,select:Function=null):RightMenuUtil{
			var obj:RightMenuUtil  = new RightMenuUtil();
			obj.menu = new ContextMenu();
			if(select!=null){
				obj.menu.addEventListener(ContextMenuEvent.MENU_SELECT, function(e:ContextMenuEvent):void {
			    	select(e);
			    });	
			}
			obj.menu.hideBuiltInItems();
		    base.contextMenu = obj.menu;
		    return obj;
		}
		
		public function add(name:String,click:Function,line:Boolean=false,enable:Boolean=true):RightMenuUtil{
			var menuItem:ContextMenuItem = new ContextMenuItem(name,line,enable);
		    menuItem.addEventListener(ContextMenuEvent.MENU_ITEM_SELECT, function(e:ContextMenuEvent):void {
				click(e);
		    });
		    menu.customItems.push(menuItem);
			return this;
		}
		
	}
}