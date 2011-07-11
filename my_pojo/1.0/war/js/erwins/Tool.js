/** 
 * 작성 : erwins 2008.03
 * 주의 : 수정하지 마세요.
 */

/*===========================================================================================
									폼 입력  (추후 prototype으로 교체)
===========================================================================================*/

var Tool = {
		
	/**
	 * parameter : 쿼리스트링을 입력하자. form을 입력해도 된다.
	 * @parameter : form일 경우 serialize해줌
	 * @validator : 예외를 던지는 벨리데이터.
	 * @btn : Lock을 걸어준다.
	 */
	send: function(url,parameter,after,validator,btn) {
		if(btn) btn.disabled = true;
		if(validator){
			try{
				validator();
			}catch(e){
				alert(e.message);
				if(btn) btn.disabled = false;
				return;
			}
		}
		
		//파라메터 분석.
		if(parameter!=null && parameter.tagName=='FORM') parameter = parameter.serialize();
		else if(parameter instanceof Object) parameter = Tool.serialize(parameter);
		
		new Ajax.Request(url,{
			parameters :parameter,
			method :"POST",
			onComplete : function(req) {
				if (200 == req.status) {
					var result = req.responseText.evalJSON();
					var message = result.message;
					if(result.isSuccess){
						if(after) after(message);
						else if(message && message!="") alert(message);
					}else{
						alert(message.unescapeHTML());//보통 escape해줌으로 다시 unescapeHTML을 해준다.
					}
					if(btn) btn.disabled = false;
				}
			},
			onFailure : function(e) {
				alert('error!!! : ' + e.message);
				inspect(e);
				if(btn) btn.disabled = false;
			}
		});
	},	
	
	/**
	 * Object를 serialize한다. 걍 문자열은 그대로 리턴.
	 * 배열형태는 나중에 지원하자.
	 * 즉 ?A=b&C=D 형태로 만든다.
	 */
	 serialize: function(obj) {
		//alert('serialize 메소드의 파라메터는 obj instanceof Object를 만족해야 합니다.');
		if(obj instanceof Object){
			var str = "";
			for(var name in obj){
				var value = obj[name];
				if(value instanceof Array){
					alert('아직 미지원');
				}else{
					if(str!="") str += "&";
					str += name +'='+ value;	
				}
				
			}
			return str;
		}else return obj;
	},
		
	/**
	 * json 안에는 각 해쉬가 들어있으며 각 해쉬는 option을 구성할 배열로 이루어져 있다.
	 * 즉 한개의 Entity안에 List<Option>이 여러개 들어있다 
	 * json의 id와 name으로 select dom에 option을 삽입한다.
	 * 마지막 인자가 true이면 디폴트 인자를 입력한다. 
	 */
	buildOption: function(json,select,def){
		if(def){
			var op = Builder.node('option', {value:''},"선택");  //디폴트
			select.insert(op);
		}
		for(var jsonName in json){
			var doms = json[jsonName];
			for(var i=0;i<doms.length;i++){
				var dom = doms[i];
				var op = Builder.node('option', {value:dom.id},dom.name);							
				select.insert(op);								
			}
		}
	},
	
	/**
	 * json 안에는 각 해쉬가 들어있으며 각 해쉬는 option을 구성할 배열로 이루어져 있다.
	 * 즉 한개의 Entity안에 List<Option>이 여러개 들어있다 
	 * json의 id와 name으로 ibSheet콤보 안에 삽입한다.
	 * ex)  sheet.InitDataCombo(0,"nonPlasticTypeCd","대상물품|OEM매입|OEM매도|수출","2101|2102|2103|2104");
	 */
	buildIBSheet: function(array,sheet,sheetName) {				
		var name = "";
		var id = "";
		for(var i=0;i<array.length;i++){
			if(i!=0){
				 name += "|";
				 id += "|";
			}
			name+=  array[i].name;
			id+=  array[i].id;
		}
		sheet.InitDataCombo(0,sheetName,name,id);
	},
	
	/**
	 * 간이로 만듬 1뎁스바께 안됨 ㅠ
	 * IE / FF 둘다 지원
	 */
	update: function(json){
		for(var entityName in json){
			var obj = json[entityName];
			if(obj instanceof Array){
				//요기 내용 추가요~ name배열로 가져오기.
				for(var i=0;i<obj.length;i++){
					this.update(obj[i]);
				}
			}
			var element = $(entityName);
			if(!element) continue;
			if(element){
				if(element.type == 'radio') this.setRadioValue(entityName, obj);
				else if(element.tagName == "TD")  element.update(obj);
				else element.value = obj;
			}
		}
	},
	
	/**
	 * 라디오버튼의 value를 가져온다.
	 */
	getRadioValue: function(name){
		var elements = $$('input[name="'+name+'"]');
		for(var i=0;i<elements.length;i++){
			if(elements[i].checked) return elements[i].value;
		}
	},
	
	/**
	 * 라디오값 입력  (라디오버튼이름, 값)
	 * ex. setRadioValue('reduceCd','<s:property value="makeResult.reduceCd"/>');
	 * dom확장하면 checked에 직접 입력 안먹음
	 */
	setRadioValue: function(name,code){
		var codes = document.getElementsByName(name);	
		for(var i=0;i<codes.length;i++){
			if(codes[i].value == code){
				codes[i].checked=true;
				break;
			}
		}
	},
	
	/**
	 * 붉게 강조한 문구를 리턴한다.
	 */
	bold: function(str){
		return '<span style="color:red;font-weight: bold;">' + str + '</span>';
	},
	
	/**
	 * Object의 구성 요소를 출력한다.
	 */
	inspect: function(obj){
		if(obj instanceof Object){
			for(var oo in obj){
				alert(oo + ':' + obj[oo]);
			}
		}else{
			alert("Object가 아님.");
		}
	},
	
	/**
	 * Object의 구성 요소를 출력한다.
	 */
	getExt: function(obj){
		if(obj instanceof Object){
			for(var oo in obj){
				alert(oo + ':' + obj[oo]);
			}
		}else{
			alert("Object가 아님.");
		}
	},
	/**
	 * MSE 6.0버전이면 true를 리턴한다. 
	 */
	isMSE6: function(name){
		var v = navigator.appVersion;
		if(v.indexOf('6.0') > -1){
			return true;
		}else{
			return false;
		}
	}
}


