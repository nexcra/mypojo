package erwins.component{
	
import erwins.openSource.DataUtil;
import erwins.openSource.HangulFilter;

import flash.events.Event;
import flash.events.FocusEvent;
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
	 * ex) <erwins:SuggestInput keyField="label" id="keyWord" color="red" prompt="검색할 단어를 입력하세요." width="100%" >
			<erwins:list><mx:DataGrid dataProvider="{_list}"  /></erwins:list>
		   </erwins:SuggestInput>
	 *	popWin.keyWord.filterActive(); 를 해주어야 정상작동 한다.
	 *  이때 그리드가 들어갈 좌표를 결졍해 주는데, 이는  filterActive()를 호출하는 this에 따라 결정된다.
	 * 따라서 팝업창에서 서제스트를 호출하는 경우 부모가 Popup하는곳(부모가 this로 취급받는곳)에서 filterActive()를 호출하도록 하자.
	 * filter를 적용함으로 얕은 copy를 한 collection을 사용하도록 하자.
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
				if (e.keyCode == Keyboard.TAB ) showDropDown = false;
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
		
		/** 팝업될 공간의 위치정보를  세팅해 준다.
		 * localToGlobal 이 메소드는 this를 기준응로 작동한다. 이상하게 위치가 나온다면 메소드 호출위치 확인 */
		public function adjustPoint():void{
			var point:Point = new Point(me.x,me.y);
	     	point = me.localToGlobal(point);
	     	if(_basePoint){
	     		point.x += _basePoint.x;
	     		point.y += _basePoint.y;
	     	}
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
		
		private var _basePoint:Point;
		public function set base(basePoint:Point):void{
			_basePoint = basePoint;
		}

	}
}