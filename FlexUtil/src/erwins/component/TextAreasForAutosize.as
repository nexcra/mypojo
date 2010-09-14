package erwins.component{
	import erwins.util.UILib.InitUtil;
	import erwins.util.UILib.TimeUtil;
	import erwins.util.lib.Alerts;
	
	import flash.events.KeyboardEvent;
	
	import mx.controls.TextArea;
	import mx.utils.*;
	
	/**  엔터 시 조금씩 늘어나는 텍스트 에어리어. 버그 많을듯 하니 실사용시 주의해야 한다.  
	 * 설정 ex: width="800" height="200" minAutoSize="200" maxAutoSize="400"  */
	public  class TextAreasForAutosize extends TextArea{
		private var me:TextAreasForAutosize = this as TextAreasForAutosize;
		private var _minAutoSize:int = 0;
		private var _maxAutoSize:int = 1200;
		private var _autoSizing:Boolean = true;
		private var _resize:Function;
		private var _offset1:int = 20;
		
		/** 정확한 이벤트 구조를 몰라서 걍 했다1.  */
		public function set offset1(offset1:int):void{
			this._offset1 = offset1;
		}
		public function set minAutoSize(size:int):void{
			this._minAutoSize = size;
		}
		public function set maxAutoSize(size:int):void{
			this._maxAutoSize = size;
		}
		public function set autoSizig(autoSizing:Boolean):void{
			this._autoSizing = autoSizing;
		}
		
		/*  일부러 오버라이드 하지 않았다. htmlText등 여러 방법이 있기 때문이다.
		override public function set text(text:String):void{
			super.text = text;
			TimeUtil.fire(_resize,100);
		}
		*/
		
		/**  이걸 해주어야 강제로 값 입력시 정상적인 화면갱신이 된다. 
		 * (text의 높이를 구하기 위해서 반드시 width는 %가 아닌 값으로 잡아주어야 한다.)
		 * validateNow()를 반드시 해주어야 정상적인 처리가 된다.  */
		public function resize():void{
			_resize();
		}
		
		/** 최소값을 정의하지 않으면 현재 높이가 최소값이 된다. 
		 * 더해주는 높이는 대충 내가 정한거임. ㅋㅋ */
		public function TextAreasForAutosize(){
			me.verticalScrollPolicy = "off";
			if(!_autoSizing) return;
			
			InitUtil.initialize(me,function():void{
				if(_minAutoSize==0) _minAutoSize = me.height;
				var nowTextHeight:int = 0;
				
				_resize = function():void{
					if(nowTextHeight == me.textHeight) return;
					nowTextHeight =  me.textHeight;
					var expected:int = nowTextHeight + _offset1; 
					if(expected < _minAutoSize || expected > _maxAutoSize) return;
					me.height = expected;
				}
				
				me.addEventListener(KeyboardEvent.KEY_UP||KeyboardEvent.KEY_DOWN,_resize);
			});
		}
		
	}
}