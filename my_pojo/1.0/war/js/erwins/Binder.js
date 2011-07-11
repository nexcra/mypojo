/** 
 * 작성 : erwins 2008.03
 * 이벤트를 배열로 저장합니다.(Object아님!!)
 * 주의 : 수정하지 마세요.
 */

var Binder = {
		
		/** 클릭시 이벤트. 만약 없으면 무시한다. */
		click : function(obj,fun){
			if(!obj) return;
			obj.observe('click',fun);
		},
		
		/**
		 * Load시의 이벤트를 추가한다. 프로그램상의 window.onload 직접호출은 이제 금지다.
		 */
		addLoadEvent : function(fun){
			var oldonload = window.onload;
			if(typeof window.onload != 'function'){
				window.onload = fun;
			}else{
				window.onload = function(){
					oldonload();
					fun();
				}
			}
		},
		
		/**
		 * FF등의 표준 브라우저에서만 작동한다. IE는 안됨.
		 * onload이벤트에서만 하도록.
		 * ex) Binder.mouseOver(document.getElementsByTagName('li'));
		 */
		mouseOver : function (list){
			for(var i=0;i<list.length;i++){
				list[i].onmouseover = function(){
					this.style.backgroundColor = 'yellow';
				}
				list[i].onmouseout = function(){
					this.style.backgroundColor = 'white';
				}
			}
		},
		
		/**
		 * dom객체에 click이벤트를 발생시킨다.
		 * e.stop();이 있어야지 크롬 등의 브라우저에서 기본 동작을 캔슬 후 작동할 수 있다.
		 * 세번째 인자인 target이 true일경우 id를 바인딩 해준다. 이는 버튼 자체의 this를 가지는 효과가 있다.
		 * IE / FF 동작 확인
		 */
		enter : function (id,method){
			var obj = $(id);
			if(obj==null) throw new Error(id + " : bindEnter할 대상이 없습니다.");
			obj.observe('keydown',function(e){
				var keyCode =  e.keyCode;
				if(keyCode==13){
					method(e,obj); 
				}
			});
		},
		
		/**
		 * 텍스트 에어리어에 값 입력시 자동 리사이즈.
		 * event : keyup
		 */
		textAreaAutoResize: function(e){
			var m=50;
			var s = this.scrollHeight;
			if(s>=m) this.style.pixelHeight=s+4;
		},
		

		/**
		 * 숫자에 3자리마다 .을 찍어준다. (onKeyUp 이벤트)
		 * element.observe('keyup',Ev.toMoney.bindAsEventListener(element));
	     * Ev.toMoney('',element);
		 * ex. onKeyUp="toMoney(this);"
		 */
		toMoney: function(e,obj) {
			obj = obj || this;
			obj.value = obj.value.toString().toNumeric();
			if(obj.value=='NaN') obj.value = '';
		},
		
		/**
		 * max값 이상일때 포커스 이동
		 * event : keydown
		 */
		moveFocus: function(e,target) {
			var temp = this.value;
			if (this.length >= this.maxLength) {
				target.focus();
			}
		}
}
