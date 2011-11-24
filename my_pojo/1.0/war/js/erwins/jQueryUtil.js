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
/** 체크박스중에 체크된것만 가져온다. */
$.findChecked = function(ckecks){
	var checkedList = new Array();
	$.each(ckecks,function(i,v){ if(v.checked) checkedList.push(v);});
	return checkedList;	
}


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

/** 에거 수정하기 */
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

/** 간단 테스트기 */
$.getInfo =  function(obj){
	var str = '';
	if(obj instanceof Object){
		for(var oo in obj){
			str+= oo + '  :  ' + obj[oo] + '\n'
		}
		alert(str);
	}else{
		alert("Object가 아님.");
	}
}