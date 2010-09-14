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
	 * ex) <erwins:SuggestInput  keyField="label2" adjustX="-250" >
    		<erwins:list><mx:DataGrid  dataProvider="{arr}" /></erwins:list>
    	   </erwins:SuggestInput>
	 **/
	public class SuggestInput extends TextInputs{
	
		private var me:SuggestInput = this as SuggestInput;
		
		public function SuggestInput(){
			super();
			/** refresh의 편의를 위해 리스너를 제거하지는 않는다. */
			me.addEventListener(FlexEvent.CREATION_COMPLETE,function(e:FlexEvent):void{
				
				_listBase.addEventListener(FlexMouseEvent.MOUSE_DOWN_OUTSIDE,function(e:FlexMouseEvent):void{
					showDropDown = false;
				});
				
				/** 콜백 추가~ */
				var success:Function = function():void{
					me.text = _listBase.selectedItem[_keyField];
					_selectedItem = _listBase.selectedItem;
					me.setFocus();
					me.textField.setSelection(0,me.textField.text.length);
					showDropDown = false;
					_listBase.selectedIndex = -1;
					if(_callback!=null) _callback();
				};
				
				_listBase.addEventListener(ListEvent.ITEM_CLICK,function(e:ListEvent):void{
					var index:int = e.rowIndex;
					if(index < 0) return;
					success();
				});
				
				//주의 KEY_UP 으로 해야 한다.
				_listBase.addEventListener(KeyboardEvent.KEY_UP,function(e:KeyboardEvent):void{
					if(_listBase.selectedIndex != -1) me.textField.text = _listBase.selectedItem[_keyField];
					if (e.keyCode == Keyboard.ENTER) success();
				});
				
				dispatchEvent(new Event("listChanged")); //????
				
				me.textField.alwaysShowSelection = true;
				
			});
			
			me.addEventListener(KeyboardEvent.KEY_DOWN,function(e:KeyboardEvent):void{
				if (e.keyCode == Keyboard.BACKSPACE ) {
			 		if (me.textField.text.length==0) {
			 			showDropDown = false;
			 		}
		     	}
		 		if (e.keyCode == Keyboard.DOWN) {
		 			showDropDown = true;
		   			_listBase.setFocus();
		   			_listBase.invalidateDisplayList();
		   			_listBase.selectedIndex=0;
		   		}
			});
			
			//?? 정확한 용도는 불명.
			me.addEventListener(ResizeEvent.RESIZE,function(e:ResizeEvent):void{
				showDropDown = false;
			}, false, 0, true);
		}
		
		/** ============================ 메소드  ============================== */
		
		/** dataProvider에 필터를 걸어준다. */
		public function filterActive(active:Boolean=true):void{
			var dataProvider:ArrayCollection = _listBase.dataProvider as ArrayCollection;
			DataUtil.applyUID(dataProvider);
			var charCodeTable:Dictionary = HangulFilter.hashToCharMap(dataProvider as ICollectionView, _keyField);

			if(!active){  //필터 삭제도 가능.
				dataProvider.filterFunction = null;
				return;
			}
	
			dataProvider.filterFunction = function(item:Object):Boolean {
				var uid:String = mx.utils.UIDUtil.getUID(item);
				var codesArr:Array = charCodeTable[uid];
				var flag:Boolean = HangulFilter.containsChar(me.textField.text, codesArr);
				return flag;
		 	};
		 	
			adjustPoint();
		}
		
		/** 팝업될 공간의 위치정보를 부모? 기준으로 세팅해 준다. */
		public function adjustPoint():void{
			var point:Point = new Point(me.x,me.y);
			//현재 이거 대단히 위험함.. 오류 발생여지가 충문 : me.parent <<== 요구문 ㅋㅋ
	     	point = me.parent.localToGlobal(point);  //부모 기준으로 해야한다.. 왜인지는 몰라.
	        _listBase.x = point.x;
	        _listBase.y = point.y + me.height;
	      	_listBase.width =  _listBase.width ==  0 ? me.width : _listBase.width;
	      	_listBase.height =  _listBase.height ==  0 ? 200: _listBase.height;
      	
    		PopUpManager.addPopUp(_listBase,me);
    		showDropDown = false;	
		}		
		
		/** 공용베소드. */
		private function set showDropDown(value:Boolean):void{
			_listBase.visible = value;
		}
	
	    /**
	    * 실제 키 이벤트 처리. 필터를 걸었음으로  refresh만 해준다.
	    **/
	    override protected function keyUpHandler(event:KeyboardEvent):void {
	    	super.keyUpHandler(event);
	    	if (event.keyCode == Keyboard.ENTER) return; //검색명령을 내리기 위해 엔터를 칠 수 있음으로 이는 무시한다.
	    	_listBase.dataProvider.refresh();
	    	showDropDown = StringUtil.trim(me.textField.text).length == 0?false:true;
	    }		
	
		/** ============================ getter / setter ============================== */
	
		private var _listBase:ListBase;
	
		public function set list(value:ListBase):void{
			_listBase = value;
		}
		
		/*  ??
		[Bindable("listChanged")]
		[Inspectable(type="mx.controls.listClasses.ListBase")]
		public function get list():ListBase{
			return _listBase;
		}*/
	
		private var _keyField:String;
		
		public function set keyField(value:String):void{
			_keyField = value;
		}
	
		public function get keyField():String{
			return _keyField;
		}
		
		private var _selectedItem:Object;
		
		public function get selectedItem():Object{
			return _selectedItem;
		}
		
		private var _callback:Function;
		
		public function set callback(callback:Function):void{
			_callback = callback;
		}

	}
}