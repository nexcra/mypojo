package erwins.component{
	import com.adobe.serialization.json.*;
	
	import erwins.util.UILib.InitUtil;
	import erwins.util.UILib.TimeUtil;
	
	import flash.events.KeyboardEvent;
	
	import mx.controls.RichTextEditor;
	import mx.utils.*;
	
	/**  일단 사용금지!! 걍 텍스트 에어리어 참조할것.
	 * 설정 ex: width="800" height="200" minAutoSize="200" maxAutoSize="400"  */
	public  class RichTextEditors extends RichTextEditor{
		private var me:RichTextEditors = this as RichTextEditors;
		private var _minAutoSize:int = 0;
		private var _maxAutoSize:int = 1200;
		private var _autoSizing:Boolean = true;
		private var _resize:Function;
		private var _offset1:int = 20;
		private var _offset2:int = 98;
		
		/** 정확한 이벤트 구조를 몰라서 걍 했다1.  */
		public function set offset1(offset1:int):void{
			this._offset1 = offset1;
		}
		/** 정확한 이벤트 구조를 몰라서 걍 했다2.  */
		public function set offset2(offset2:int):void{
			this._offset2 = offset2;
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
		
		/** htmlText이 언제 끝나는지 알 수 없기 때문에 멍청한 TimeUtil.fire를 사용했다 ㅅㅂ.  */
		override public function set htmlText(htmlText:String):void{
			super.htmlText = htmlText;
			TimeUtil.fire(_resize,100);
		}
		
		/** 최소값을 정의하지 않으면 현재 높이가 최소값이 된다. 
		 * 더해주는 높이는 대충 내가 정한거임. ㅋㅋ */
		public function RichTextEditors(){
			if(!_autoSizing) return;
			
			InitUtil.initialize(me,function():void{
				if(_minAutoSize==0) _minAutoSize = me.height;
				
				var nowTextHeight:int = 0;
				
				_resize = function():void{
					if(nowTextHeight == me.textArea.textHeight) return;
					nowTextHeight =  me.textArea.textHeight;
					var expected:int = nowTextHeight + _offset2; 
					if(expected < _minAutoSize || expected > _maxAutoSize) return;
					me.textArea.height = nowTextHeight + _offset1;
					me.height = expected;
				}
				
				me.addEventListener(KeyboardEvent.KEY_UP,_resize);
			});
		}
		
	}
}