package erwins.component{
	
import erwins.openSource.DataUtil;
import erwins.openSource.HangulFilter;

import flash.events.Event;
import flash.events.KeyboardEvent;
import flash.geom.Point;
import flash.ui.Keyboard;
import flash.utils.Dictionary;

import mx.collections.ArrayCollection;
import mx.collections.ICollectionView;
import mx.controls.listClasses.ListBase;
import mx.events.FlexEvent;
import mx.events.FlexMouseEvent;
import mx.events.ListEvent;
import mx.events.ResizeEvent;
import mx.managers.PopUpManager;
import mx.utils.StringUtil;
import mx.utils.UIDUtil;

	/***
	 * 한글을 인식하는 서제스트.
	 * ex) <erwins:SuggestInput  displayField="label2" >
    		<erwins:list><mx:DataGrid  dataProvider="{arr}" /></erwins:list>
    	   </erwins:SuggestInput>
	 **/
	public class SuggestInput extends TextInputs{
	
		private var me:SuggestInput = this as SuggestInput;
		
		public function SuggestInput(){
			super();
			/** refresh의 편의를 위해 리스너를 제거하지는 않는다. */
			me.addEventListener(FlexEvent.CREATION_COMPLETE,function(e:FlexEvent):void{
				
				list.addEventListener(FlexMouseEvent.MOUSE_DOWN_OUTSIDE,function(e:FlexMouseEvent):void{
					showDropDown = false;
				});
				
				list.addEventListener(ListEvent.ITEM_CLICK,function(e:ListEvent):void{
					var index:int = e.rowIndex;
					if(index < 0) return;
					me.textField.text = list.selectedItem[displayField];
					me.setFocus();
					me.textField.setSelection(0,me.textField.text.length);
					showDropDown = false;
					list.selectedIndex = -1;
				});
				
				//주의 KEY_UP 으로 해야 한다.
				list.addEventListener(KeyboardEvent.KEY_UP,function(e:KeyboardEvent):void{
					if(list.selectedIndex != -1){
						me.textField.text = list.selectedItem[displayField];
					}
					if (e.keyCode == Keyboard.ENTER) { //엔터를 칠 경우..
			   			me.textField.setFocus();
			   			me.textField.setSelection(0,me.textField.text.length);
			   			list.selectedIndex = -1;
			   			showDropDown = false;
			   		}
				});
				
				//????
				dispatchEvent(new Event("listChanged"));
				
				//dataProvider에 필터를 걸어준다.
				var dataProvider:ArrayCollection = list.dataProvider as ArrayCollection;
				DataUtil.applyUID(dataProvider);
				var charCodeTable:Dictionary = HangulFilter.hashToCharMap(dataProvider as ICollectionView, displayField);
				
				dataProvider.filterFunction = function(item:Object):Boolean {
					var uid:String = mx.utils.UIDUtil.getUID(item);
					var codesArr:Array = charCodeTable[uid];
					var flag:Boolean = HangulFilter.containsChar(me.textField.text, codesArr);
					return flag;
			 	};
				
				me.textField.alwaysShowSelection = true;
				
				//위치정보 세팅
				var point:Point = new Point(me.x,me.y);
		     	//point = me.localToGlobal(point); //????
		     	
		        list.x = point.x;
		        list.y = point.y + me.height;
		      	list.width =  list.width ==  0 ? me.width : list.width;
		      	list.height =  list.height ==  0 ? 200: list.height;
	      	
	    		PopUpManager.addPopUp(list,me);
	    		showDropDown = false;
	    		
			});
			
			me.addEventListener(KeyboardEvent.KEY_DOWN,function(e:KeyboardEvent):void{
				if (e.keyCode == Keyboard.BACKSPACE ) {
			 		if (me.textField.text.length==0) {
			 			showDropDown = false;
			 		}
		     	}
		 		if (e.keyCode == Keyboard.DOWN) {
		 			showDropDown = true;
		   			list.setFocus();
		   			list.invalidateDisplayList();
		   			list.selectedIndex=0;
		   		}
			});
			
			//?? 정확한 용도는 불명.
			me.addEventListener(ResizeEvent.RESIZE,function(e:ResizeEvent):void{
				showDropDown = false;
			}, false, 0, true);
		}
		
		/** ============================ 메소드  ============================== */
		
		/** 공용베소드. */
		private function set showDropDown(value:Boolean):void{
			_listBase.visible = value;
		}
	
	    /**
	    * 실제 키 이벤트 처리. 필터를 걸었음으로  refresh만 해준다.
	    **/
	    override protected function keyUpHandler(event:KeyboardEvent):void {
	    	super.keyUpHandler(event);
	    	_listBase.dataProvider.refresh();
	    	showDropDown = StringUtil.trim(me.textField.text).length == 0?false:true;
	    }		
	
		/** ============================ getter / setter ============================== */
	
		private var _listBase:ListBase;
	
		public function set list(value:ListBase):void{
			_listBase = value;
		}
	
		[Bindable("listChanged")]
		[Inspectable(type="mx.controls.listClasses.ListBase")]
		public function get list():ListBase{
			return _listBase;
		}
	
		private var _displayField:String;
		
		public function set displayField(value:String):void{
			_displayField = value;
		}
	
		public function get displayField():String{
			return _displayField;
		}

	}
}