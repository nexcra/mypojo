package erwins.util.UILib{
	import com.adobe.serialization.json.*;
	
	import flash.display.Sprite;
	import flash.events.ContextMenuEvent;
	import flash.ui.ContextMenu;
	import flash.ui.ContextMenuItem;
	
	import mx.utils.*;
	public  class ContextMenuUtil{
		
		/** line이 true이면 윗줄에 LINE을 그어준다. 첫줄은 당연히 default로 true이다. */
		public static function addMenu(base:Sprite,name:String,click:Function,line:Boolean=false,enable:Boolean=true):void{
			var menu:ContextMenu = base.contextMenu;
			if(menu==null){
				menu = new ContextMenu();
				menu.hideBuiltInItems();
				base.contextMenu = menu;
			}
			var menuItem:ContextMenuItem = new ContextMenuItem(name,line,enable);
		    menuItem.addEventListener(ContextMenuEvent.MENU_ITEM_SELECT, function(e:ContextMenuEvent):void {
				click(e);
		    });
		    menu.customItems.push(menuItem);
		}
		
	}
}