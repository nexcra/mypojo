

//==============================  벨리데이션 ======================================//
/** 기본 벨리데이션 체크 */
$.isNull = function(name,message){
	if(!(name instanceof $)) name =  $('#'+name);
	if(name.length == 0){
		alert(errors.nodata);
		return true;
	}
	var type = name.attr('type'), tag = name.attr('tagName').toLowerCase();
	if ( (type == 'text' || type == 'password' || type == 'hidden' || tag == 'textarea') && name.val() == ''){
		alert(errors.required.format(message));
		name.effect("highlight", {}, 3000).select();
		return true;
	}
	else if (tag == 'select' && ( name.selectedIndex == -1 || name.val() == '' ) ){
		alert(errors.required.format(message));
		name.select();
		return true;
	}
	return false;
};
/** 체크박스일때 사용한다. */
$.isNulls = function(checkBoxs,message){
	if(!(checkBoxs instanceof $)) checkBoxs =  $('input[name='+checkBoxs+']:checkbox');
	if(checkBoxs.length==0){
		alert(errors.nodata);
		return true;
	}
	var checked = $.findChecked(checkBoxs);
	if(checked==0){
		alert(errors.required.format(message));
		return true;
	}else return false;
}
/** 크기를 비교한다. A가 B보다 크면 오류이다.. 일자 비교용 */
$.isLargeThan = function(A,B,message){
	if(!(A instanceof $)) A =  $('#'+A);
	if(!(B instanceof $)) B =  $('#'+B);
	if(A.val() <= B.val()) return false;
	alert(errors.time.format(message));
	A.select();
	return true;
}

/** 크기를 비교한다. A가 B보다 크면 오류이다.. 숫자 비교용 */
$.isLargeValueThan = function(A,B,message){
	if(!(A instanceof $)) A =  $('#'+A);
	if(!(B instanceof $)) B =  $('#'+B);
	if(A.val().toNumber() <= B.val().toNumber()) return false;
	alert(errors.compare.format(message));
	A.select();
	return true;
}

/** 크기를 비교한다. A가 B보다 크면 오류이다.. 숫자 비교용(같은 값도 허용하지 않음 */
$.isLargeValueMoreThan = function(A,B,message){
	if(!(A instanceof $)) A =  $('#'+A);
	if(!(B instanceof $)) B =  $('#'+B);
	if(A.val().toNumber() < B.val().toNumber()) return false;
	alert(errors.compare.format(message));
	A.select();
	return true;
}

/** 정수형태가 아닌지 체크. (decimal 아님) */
$.isNotNumber = function(name,message){
	if(!(name instanceof $)) name =  $('#'+name);
	if(name.val().isNumeric()) return false;
	alert(errors.integer.format(message));
	name.select();
	return true;
}
/** 소수체크.  */
$.isNotDecimal = function(name,intSize,fractionSize,message){
	if(!(name instanceof $)) name =  $('#'+name);
	var value = name.val();
	if(!name.val().isDecimal()){
		alert(errors.decimal.format(message));
		name.select();
		return true;
	}
	var part = [value,''];
	var dotIndex = value.indexOf('.');
	if(dotIndex != -1) part = value.split('.');
	
	if(part[0].length > intSize){
		alert(errors.limitlength.format([message[0]+'의 정수부',intSize]));
		name.select();
		return true;
	}
	if(part[1].length > fractionSize){
		alert(errors.limitlength.format([message[0]+'의 소수부',fractionSize]));
		name.select();
		return true;
	}
	return false;
}
/** 입력값의 길이 체크 */
$.isLarge = function(name,maxSize,message){
	if(!(name instanceof $)) name =  $('#'+name);
	var value = name.val();
	if(value==null) return;
	if(value.length <= maxSize) return false;
	alert(errors.limitlength.format([message[0],maxSize]));
	name.select();
	return true;
}
/** 입력값의 숫자value 체크 */
$.isLargeValue = function(name,maxSize,message){
	if(!(name instanceof $)) name =  $('#'+name);
	var value = name.val();
	if(value.toNumber() <= maxSize) return false;
	alert(errors.smallValue.format([message[0],maxSize]));
	name.select();
	return true;
}
/** date인지 여부 */
$.isNotDate = function(name){
	if(!(name instanceof $)) name =  $('#'+name);
	var check = dateValiOnlyDate(name.val());
	if(check!=0) return false;
	name.select();
	return true;
}
/** month인지 여부 */
$.isNotMonth = function(name){
	if(!(name instanceof $)) name =  $('#'+name);
	var value = name.val();
	
	if($.isNotNumber(name,['월'])) return true;
	var num = value.toNumber();
	if( num >= 1 && num <= 12) return false;
	alert('월은 01 ~ 12 사이의 값을 입력하여야 합니다.');
	name.select();
	return true;
}
/** year인지 여부 */
$.isNotYear = function(name){
	if(!(name instanceof $)) name =  $('#'+name);
	var value = name.val();
	if(value >= '1900' && value <= '2999') return false;
	alert('1900 ~ 2999 사이의 값을 입력하여야 합니다.');
	name.select();
	return true;
}

/** 시작일-종료일을 벨리데이션 체크한다.
 * ex) if($.validateBetweenDate('stTime','clseTime',['시작일','종료일'])) return; */
$.validateBetweenDate = function(A,B,names){
	if(!(A instanceof $)) A =  $('#'+A);
	if(!(B instanceof $)) B =  $('#'+B);
	if($.isNull(A,[names[0]])) return true;
	if($.isNull(B,[names[1]])) return true;
	if($.isNotDate(A)) return true;
	if($.isNotDate(B)) return true;
	if($.isLargeThan(A,B,names)) return true;
	return false;
}
 /** 시작년-종료년도를 벨리데이션 체크한다.  */
 $.validateBetweenYear = function(A,B,names){
	 A =  $('#'+A);
	 B =  $('#'+B);
	 if($.isNull(A,[names[0]])) return true;
	 if($.isNull(B,[names[1]])) return true;
	 if($.isNotYear(A)) return true;
	 if($.isNotYear(B)) return true;
	 if($.isLargeThan(A,B,names)) return true;
	 return false;
 }
 /** 필수입력항목인 일자를 벨리데이션 체크한다. */
 $.validateDate = function(A,names){
	 A =  $('#'+A);
	 if($.isNull(A,names)) return true;
	 if($.isNotDate(A)) return true;
	 return false;
 }
 /** 영문/숫자 조합이 아니라면. 첫문자는 숫자가 올 수 없다. */
 $.isNotAlphanumeric = function(A,names){
	 A =  $('#'+A);
	 var value = A.val();
	 if(value[0].isNumeric()){
		 alert('{0}의 첫 문자는 숫자일 수 없습니다.'.format(names));
		 A.select();
		 return true;
	 }
	 if(!value.isAlphaNumeric()){
		 alert('{0}에는 영문/숫자조합만 올 수 있습니다.'.format(names));
		 A.select();
		 return true;		 
	 }
	 return false;
 }

 /** confirm을 호출해서 !를 리턴한다.
  * ex) if($.confirm('서비스컨텐츠')) return; */
$.confirm = function(subject){
	if(subject==null) return false;
	var msg  = '{0}이(가) 저장됩니다. \n\n이대로 저장하시겠습니까?'.format([subject]);
	return !confirm(msg);
}
  
//==============================  기상청 로직 ======================================//

/** 기상청용 datepicker */
$.fn.datepickerk = function() {
	return this.each(function() {
		/** .을 생략했을경우 재입력 해준다. */
		var addDot = function(me){
			var value = me.val();
			if(value.indexOf('.') != -1) return;
			if(value.length == 8){
				var result = value.substring(0,4)+'.'+value.substring(4,6)+'.'+value.substring(6,8);
				me.val(result);
			}
		}
		$(this).datepicker().attr('size',10).attr('maxlength',10).keyup(function(){
			var me = $(this);
			addDot(me);
		});
	});
};
	
// ==============================  신규추가 ======================================//	
 
$.send = function(url,data,callback){
	$.loading();
	$.ajax({
		type:'POST',url:url,dataType:'json',data:data,
		success:function(json,status){
			$.loading(false);
			if(json.success) callback(json.message,json);
			else{
				alert("작업 실패\n" + json.message);
			}
		},error:function(xhr,status,error){
			$.loading(false);
			alert('오류! \n'+status + error);
		}
	});
};
$.bindEnter = function(btn,input,func){
	if(!(btn instanceof $)) btn =  $('#'+btn);
	if(!(input instanceof $)) input =  $('#'+input);
	btn.click(func);
	input.keyup(function(e){
		var keyCode =  e.keyCode;
		if(keyCode==13){
			func();
		}	
	});
};
/** max까지 입력되면 다음DOM으로 포커싱을 이동한다. */
$.bindTab = function(dom1,dom2){
	if(!(dom1 instanceof $)) dom1 =  $('#'+dom1);
	if(!(dom2 instanceof $)) dom2 =  $('#'+dom2);
	dom1.keyup(function(event){
		var keyCode = event.which;
		var ignoreKeyCodes = ',9,16,17,18,19,20,27,33,34,35,36,37,38,39,40,4,5,46,144,145,';
		if(ignoreKeyCodes.indexOf(','+keyCode+',') > -1) return;

		var $this = $(this);
		var currentLength = $this.val().length;
		var maximumLength = $this.attr('maxlength');
		if(currentLength == maximumLength){
			dom2.select();
		}
	});
	return $;
};
/** json 값 설정. key와 id가 동일해아 한다.
 * 라디오 버튼의 경우 id가 없음으로 따로 setRadio를 해야한다.
 * ex) var reqParam = ${reqParam};
	$.setInput(reqParam);
	$.setRadio(radios,reqParam.searchDateType); */
$.setInput = function(json,prefix){
	if(prefix==null) prefix = '';
	for(var key in json) {
		var dom = $('#'+prefix+key);
		if(dom.length !=1 ) continue;
		var type = dom.attr('type');
		var value = json[key];
		if(type=='checkbox'){
			dom.setCheckBox(value);
		}else dom.attr('value',value); 
	}
}
/** 라디오 버튼에 값 설정. ex) var radios =  $('input[name=type]:radio'); */
$.setRadio = function(radios,value){
	if(value==null || value=='') radios[0].checked = true;
	else $.each(radios,function(i,v) { if( value == v.value ) v.checked = true; });
}
/** 라디오 버튼의 값을 가져옴 */
$.getRadio = function(radios){
	for(var i=0;i<radios.length;i++){
		if( radios[i].checked ) return radios[i].value;
	}
	return null;
}
/** 셀렉트박스에 선택된 text를 가져온다. 특수목적용 */
$.fn.getSelectBoxText = function() {
	var cd = $(this);
	if(cd.length == 0){
		alert('no element!');
		return '';
	}
	var index = cd.attr('selectedIndex');
	var option = cd.attr('options')[index];
	return option.text ;
};
/** 체크박스에 값 설정. 
 * 설정할때마다 모든 체크는 false로 초기화 한다. */
$.fn.setCheckBox = function(codes) {
	return this.each(function(){
		var checkBox = this;
		checkBox.checked = false;
		$.each(codes,function(i,code){
			if(code == checkBox.value) checkBox.checked = true;
		});
	});
};

/** 폼을 초기화한다. */
$.fn.clearForm = function() {
	return this.each(function() {
		var type = this.type, tag = this.tagName.toLowerCase();
		if (tag == 'form') return $(':input',this).clearForm();
		if (type == 'text' || type == 'password' || tag == 'textarea' || type == 'hidden')this.value = '';
		else if (type == 'checkbox' || type == 'radio') this.checked = false;
		else if (tag == 'select') this.selectedIndex = 0;
	});
};
/** 해당 체크박스를 전부 선택/선택취소 한다. */
$.fn.checkAll = function(isCheck) {
	return this.each(function() {
		this.checked = isCheck;
	});
};
/** 체크박스를 전부 선택/선택취소하는 버튼을 만든다.
 * ex) var ckecks =  $('input[name=check]:checkbox');
		$('#checkAll').checkAllBtn(ckecks); */
$.fn.checkAllBtn = function(checkBoxs) {
	return this.each(function() {
		$(this).click(function(){
			checkBoxs.checkAll(this.checked);
		});
	});
};

/** ex) var select = $("#stnId");
		select.buildoption(list,{name:'전체',value:'',initValue:stnId}); */
$.fn.buildoption = function(list,defaultOption) {
	var nameKey = defaultOption.nameKey==null ? 'name' : defaultOption.nameKey;
	var valueKey = defaultOption.valueKey == null ? 'value' : defaultOption.valueKey;
	
	return this.each(function() {
		var select = $(this);
		$('option', select).remove();
		var options = select.attr('options');
		var pad = 0;
		if(defaultOption!=null){ //선처리
			if(defaultOption.name != null){
				options[0] = new Option(defaultOption.name,defaultOption.value);
				pad = 1;	
			}
		}
		$.each(list,function(k,v){
			options[k+pad] = new Option(v[nameKey], v[valueKey]);
		});
		if(defaultOption!=null){ //후처리
			var value = defaultOption.initValue ;
			if(value == null || value == '') select.selectedIndex = 1;
			else select.val(defaultOption.initValue);
		}
	});
};
/** 체크박스중에 체크된것만 가져온다. */
$.findChecked = function(ckecks){
	var checkedList = new Array();
	$.each(ckecks,function(i,v){ if(v.checked) checkedList.push(v);});
	return checkedList;	
}

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

/** 입력값의 길이 체크 */
$.isEmpty = function(value){
	if(value==null) return true;
	if(value=='') return true;
	return false;
}
/** 하나라도 매치되는지? */
$.isAny = function(list,value){
	for(var i=0;i<list.length;i++){
		if(list[i]==value) return true;
	}
	return false;
}
/** 테이블의 홀짝을 구분해서 색을 입혀준다. 
 * 사용된 선택자는 웹표준으로 E8 이하 버전은 지원되지 않아서 자바스크립트로 처리한다. */
$.initTableCss = function(body){
	var color = '#f0f0f0';
	if(body==null) $('tr:nth-child(even)').css('background',color);
	else $('tr:nth-child(even)',body).css('background',color);
}
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

/** 간이 로딩바. 이미지를 박아줘야 작동한다. */
$.loading = function(show){
	if(show==null) show = true;
	if(!$.miniProgressVar){
		$.miniProgressVar = $("<div><img src='/images/progress.gif' /><br><b>기다려 주세요</b></div>").toCenter();
		//$.miniProgressVar = $("<div style='background-color: white;' ><img src='/images/progress.gif' /><br>기다려 주세요</div>").toCenter();
	}
	if(show) $.miniProgressVar.show();
	else $.miniProgressVar.hide();
}

/** DIV로 감싼 프로그레스바를 간이 생성해서 넣어준다. */
$.buildProgressbar = function(text,interval) {
	if(interval==null) interval = 500; //0.5초간격
	//style='z-index:10'
	var body = $("<div></div>").css('width','200px').css('height','20px').text(text);
	var element =  $("<div></div>").appendTo(body);
	body.appendTo('body').toCenter();
	
	var progress = new Object();
	progress.index = 0;
	progress.func = function(){
		if(progress.index==null) progress.index = 0;
		progress.index += 10;
		if(progress.index>100) progress.index = 10;
		element.progressbar({value: progress.index});
	}
	//progress.func();
	var timer = null;
	progress.start = function(){
		if(timer!=null){
			alert('progress가 작동중');
			return;
		}
		progress.index = 0;
		body.show();
		timer = setInterval(progress.func,interval);
	}
	progress.stop = function(){
		if(timer==null){
			alert('progress가 기동중이지 않음');
			return;
		}
		clearInterval(timer);
		timer = null;
		body.hide();
	}
	return progress; 
};

/** ajax로 반복툴팁을 하면 잔상?이 남는다. 이걸로 효과가 있을지 의문 */
$.removeTooltip = function(text) {
	$("div.tooltip").remove();
}

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

/** map이 Array에만 반응해서 하나 만들었다.  */
$.collect = function(items,callback){
	var result = [];
	$.each(items,function(k,v){
		var value = callback(k,v);
		result.push(value);
	});
	return result;
}

/** item에 DOM or DOM의 ID값 or json을 넣으면 된다. 
 *  일반 문자열을 넣으면 form을 serialize한다. */
$.toQuery = function(items){
	if(items==null) return '';
	if(items instanceof Array){
		return  $.map(items,function(it){ 
			//~  $()일 경우 변경~
			return it+'='+$('#'+it).val();
		}).join('&');
	}else{
		if( !( items instanceof Object ) ) return $('#'+items).serialize(); 
		return  $.collect(items,function(k,v){ 
			var value = v == null ? '' : v;
			return k+'='+value;
		}).join('&');
	}
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