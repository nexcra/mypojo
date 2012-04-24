/** 인풋박스 등에 엔터키 이벤트를 바인드한다. */
$.fn.enter = function(func) {
	return this.each(function() {
		var me = $(this);
		me.keyup(function(e){
			var keyCode =  e.keyCode;
			if(keyCode==13){
				func();
			}
		});
	});
};

/** 폼을 초기화한다. 기본 지원하는 초기화가 select의 인덱스를 null로 넣어버려서? 요걸 사용한다. */
$.fn.clearForm = function() {
	return this.each(function() {
		var type = this.type, tag = this.tagName.toLowerCase();
		if (tag == 'form') return $(':input',this).clearForm();
		if (type == 'text' || type == 'password' || tag == 'textarea' || type == 'hidden')this.value = '';
		else if (type == 'checkbox' || type == 'radio') this.checked = false;
		else if (tag == 'select') this.selectedIndex = 0;
	});
};

/** 셀렉트박스 : 선택된 text를 가져온다. 특수목적용 */
$.fn.domText = function() {
	var me = $(this);
	if(me.length == 0) throw new Error('No Element!');
	//~수정해서 아직 검증 안됨
	var tagName = me.attr('tagName');
	if(tagName == 'select'){
		var index = me.attr('selectedIndex');
		var option = me.attr('options')[index];
		return option.text ;	
	}else throw new Error(tagName+' 는 지원하지 않는 tag');
};

/** 체크박스에 값 설정. 
 * 설정할때마다 모든 체크는 false로 초기화 한다. */
$.fn.domCheck = function(codes) {
	return this.each(function(){
		var checkBox = this;
		checkBox.checked = false;
		$.each(codes,function(i,code){
			if(code == checkBox.value) checkBox.checked = true;
		});
	});
};

/** 해당 체크박스를 전부 선택/선택취소 한다. */
$.fn.domCheckToggle = function(isCheck) {
	return this.each(function() {
		this.checked = isCheck;
	});
};

/** 체크박스에  체크박스를 전부 선택/선택취소하는 이벤트를 걸어준다.
 * ex) var ckecks =  $('input[name=check]:checkbox');
		$('#checkAll').checkAllBtn(ckecks); */
$.fn.domCheckAllBtn = function(checkBoxs) {
	return this.each(function() {
		$(this).click(function(){
			checkBoxs.domCheckToggle(this.checked);
		});
	});
};

/**  전,후 처리는 별도로 할것!
 * ex) var select = $("#stnId");
		select.domOption(list); */
$.fn.domOption = function(list,defaultOption) {
	//var nameKey = defaultOption.nameKey==null ? 'name' : defaultOption.nameKey;
	//var valueKey = defaultOption.valueKey == null ? 'value' : defaultOption.valueKey;
	return this.each(function() {
		var select = $(this);
		$('option', select).remove();
		var options = select.attr('options');
		//var pad = 0;
		/*
		if(defaultOption!=null){ //선처리
			if(defaultOption.name != null){
				options[0] = new Option(defaultOption.name,defaultOption.value);
				pad = 1;	
			}
		}*/
		$.each(list,function(k,v){
			options[k] = new Option(v[nameKey], v[valueKey]);
		});
		select.selectedIndex = 1;
		/*
		if(defaultOption!=null){ //후처리
			var value = defaultOption.initValue ;
			if(value == null || value == '') select.selectedIndex = 1;
			else select.val(defaultOption.initValue);
		}*/
	});
};


/** select에 해당하는  value를 찾아서 그것만 show해준다.  
어디에 쓰일려나.. 쓸모없어보임. */
$.fn.toggleByValue = function() {
	return this.each(function() {
		var me = $(this);
		var options = me.attr('options');
		$.each(options,function(k,v){
			var target = $(v).val().dom();
			if(target != null) target.hide();
		});
		var selected = $(me).val().dom();
		if(selected != null) selected.show();
	});
};

/** 프로그레스 / 팝업창 등을 가운데로 옮긴다. */
$.fn.toCenter = function() {
	return this.each(function(){
		var element = $(this);
		var win = $(window);
		var x = win.width();
		var y = win.height();
		element.remove().appendTo("body");
		element.css("position", "absolute");
		element.css("left", (x + win.scrollLeft())/2 - element.width()/2);
		element.css("top", (y + win.scrollTop())/2 - element.height()/2);
	});
};


//================= 프로젝트별 달라짐 ==========================

/** 테이블 마우스오버시 색을 입혀준다. 미검증" +
"  ex) $('tr','#listBody').tableMouseOver('yellow'); */
$.fn.tableMouseOver = function(color) {
	if(color==null) color = '#DCFFB9'; //디폴트 색 (연한 하늘색)
	return this.each(function(){
		var me = $(this);
		me.mouseover(function(){
			var background = me.css('background');
			if(background==null) background = '';
			me.data('background',background); //이거 좀 이상한듯
			me.css('background',color);
		});
		me.mouseout(function(){
			var background = me.data('background');
			if(background==null) background = '';
			me.css('background',background); //null이면 적용 안된다.
		});
	});
};

/** 각각의 DOM 안에 tooltip을 내장한다.
  * 참고 :   http://stackoverflow.com/questions/1002934/jquery-x-y-document-coordinates-of-dom-object */
$.fn.buildEachTooltip = function(id) {
	if(id==null) id = 'tooltip';
	return this.each(function(){
		var me = $(this);
		var tooltip = $('<div class="tooltipValidation" ></div>').hide().appendTo('body');
		/** input박스가 가로/세로 스크롤될 수 있기 때문에 보여줄때마다 위치를 바꾼다.   */
		tooltip.tip = function(text){
			var tPosX = me.offset().left
			var tPosY = me.offset().top + 25;
			tooltip.css({top: tPosY, left: tPosX});	
			tooltip.text(text).show();
		}
		me.data(id,tooltip);
	});
}

/** 간이 툴팁. text가 없으면 tooltip 어트리뷰트를 가져온다. 아래 CSS가 필요하다. -> 추후 {}를 받도록 변경
 *  .tooltip {position:absolute; z-index:10; background:#efd; border:1px solid green; padding:3px;} */
$.fn.addTooltip = function(text) {
	return this.each(function(){
		var me = $(this);
		var value = text ==null ? me.attr('tooltip') : text ;
		var $tooltip = $('<div class="tooltip"></div>').appendTo('body');
		var positionTooltip = function(event) {
			var tPosX = event.pageX;
			var tPosY = event.pageY + 20;
			$tooltip.css({top: tPosY, left: tPosX});
		};
		var mouseOver = function(event) {
			me.addClass('hover');
			$tooltip.html( value ).show();
			positionTooltip(event);
		};
			 
		var mouseOut = function() {
			me.removeClass('hover');
			$tooltip.hide();
		};
		me.hover(mouseOver, mouseOut).mousemove(positionTooltip);
	});
}

/**  툴팁 무시 옵션 ex)
 * $('#isTooltip').click(function(){
		$('#tableBody tr').data('tooltipIgnore',!$('#isTooltip').attr('checked'));
	});  */
$.fn.addBoxTooltip = function(title,text) {
	return this.each(function(){
		var me = $(this);
		var value = text ==null ? me.attr('tooltip') : text ;
		var $tooltip = $('<div class="tooltip"></div>').appendTo('body');
		var table = $('<table class="tbl02" style="width: 500px;"></table>').appendTo($tooltip);;
		var tr = $('<tr></tr>').appendTo(table);
		$('<td style="width: 50px;" >'+title+'</td>').appendTo(tr);
		var td = $('<td></td>').appendTo(tr);
		var textarea = $('<textarea class="textArea" readonly="readonly" style="height:80px;"  ></textarea>').appendTo(td);
	 
		var positionTooltip = function(event) {
			var tPosX = event.pageX;
			var tPosY = event.pageY + 20;
			$tooltip.css({top: tPosY, left: tPosX});
		};
		var mouseOver = function(event) {
			var tooltipIgnore =  me.data('tooltipIgnore'); //툴팁 무시 옵션
			if(tooltipIgnore) return;
			me.addClass('hover');
			//$tooltip.html( value ).show();
			textarea.val(value);
			$tooltip.show();
			positionTooltip(event);
		};
		var mouseOut = function() {
			me.removeClass('hover');
			$tooltip.hide();
		};
		me.hover(mouseOver, mouseOut).mousemove(positionTooltip);
		$tooltip.hide();
	});
}

/** HTML로 구성된 페이지에 order를 넣는다. order는 단1개만 지원한다. (여러 컬럼 적용 불가)
 *  orderDefault이 들어간게 최초 적용되는 옵션이다. 서버와 동일하게 설정해준다.
 * java의 OrderByOption과 함께 사용한다.
 *  html ex)
 *  <input type="hidden" id="orderIndex" name="orderIndex"  value="${param.orderIndex }" />
	<input type="hidden" id="orderOption" name="orderOption" value="${param.orderOption }"  />
	
	<th width="100" orderIndex="0" orderDefault="desc" >유효시작일시</th>
	
	$('#forSort tr th').toOrderBy(function(orderIndex,orderOption){
		$('#orderIndex').val(orderIndex);
		$('#orderOption').val(orderOption);
		$('#list').submit();
	},'${param.orderIndex}','${param.orderOption}');
	   */
$.fn.toOrderBy = function(callback,index,option) {
	return this.each(function(){
		var me = $(this);
		var orderIndex = me.attr('orderIndex');
		if(orderIndex==null) return;

		me.css('cursor','pointer');
		me.css('text-decoration','underline');
		
		var orderDefault = me.attr('orderDefault');
		if(index=='' && orderDefault!=null){ //초기값은 option이 있다
			var optionText = orderDefault == 'asc' ? '▲' : '▼'; 
			me.text(me.text()+' ' + optionText);
		}else if(index == orderIndex){
			var optionText = option == 'asc' ? '▲' : '▼'; 
			me.text(me.text()+' ' + optionText);
			orderDefault = option;
		}
		me.click(function(){
			if(orderDefault=='asc') orderDefault = 'desc';
			else orderDefault = 'asc';
			callback(orderIndex,orderDefault);
		});
	});
};