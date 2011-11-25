/** HTML용 벨리데이터. JQuery가 필요하다. 
 * 최종적으로 서버에 보내기 직전에 한번만 호출된다.
 * 사이트에 따라 alert해주는곳 다 바꾸면 된다.
 * ex) 
 * var v = new V('알고리즘 설정');
 * v.is(first,'적용시작일').notNull().date().smallThan(second);
 * v.run(function(){
			$.send('/qc/algoEl/update.do',$('#form').serialize(),function(){
				parent.window.returnValue = true;
				parent.window.close(); 
			});
		});  */
var V = function(confirmName){
	me = this;
	this.dom = null;
	this.is = function(dom,name){
		if (!(dom instanceof $)) dom = $('#'+dom);
		dom.data('domName',name);
		me.dom = dom;
		if(me.dom.length == 0){
			alert('개발 오류. dom을 찾을 수 없습니다. : ' + name);
			return me;
		}
		return me;
	}
	this.options = new Array();
	this.run = function(callback){
		for(var i=0;i<me.options.length;i++){
			var option = me.options[i];
			var isSuccess = option();
			if(!isSuccess) return;
		}
		if($.confirm(confirmName)) return;
		callback();
	}
	/** 모든 비교는 ''값 입력시 true를 리턴한다. 따라서 null이 되면 안되는곳은 null체크를 해야 한다. */
	this.notNull = function(){
		var dom = me.dom, name = dom.data('domName');
		var type = dom.attr('type'), tag = dom.attr('tagName').toLowerCase();
		me.options.push(function(){
			if ((type == 'text' || type == 'password' || type == 'hidden' || tag == 'textarea') && dom.val() == '') {
				alert(errors.required.format(name));
				dom.effect("highlight", {}, 3000).select();
				return false;
			} else if (tag == 'select' && (dom.selectedIndex == -1 || dom.val() == '')) {
				alert(errors.required.format(name));
				dom.select();
				return false;
			}
			return true;
		});
		return me;
	}
	/** date형식만 된다 */
	this.date = function(){
		var dom = me.dom, name = dom.data('domName');
		me.options.push(function(){
			var value = dom.val();
			if(value=='') return true;
			var check = dateValiOnlyDate(value);
			if (check != 0) return true;
			dom.select();
			return false;
		});
		return me;
	}
	/** target의 value보다 값이 같거나 작아야 한다.   */
	this.smallThan = function(target){
		var dom = me.dom, name = dom.data('domName');
		if (!(target instanceof $)) target = $('#'+target);
		me.options.push(function(){
			var A = dom.val();
			var B = target.val();
			if(A=='' || B=='') return true;
			var targetName = target.data('domName');
			if(A > B){
				alert(errors.smallValue.format([name,targetName]));
				dom.select();
				return false;	
			}else return true;
		});
		return me;
	}
	/** textarea 등에 입력한계를 제한할때 사용된다   */
	this.max = function(maxSize){
		var dom = me.dom, name = dom.data('domName');
		me.options.push(function(){
			var value = dom.val();
			if(value=='') return true;
			if (value.length <= maxSize) return true;
			alert(errors.limitlength.format( [ name, maxSize ]));
			dom.select();
		});
		return me;
	}
	
	// ========== 이하 검증안됨 =============
	/** 체크박스일때 사용한다.  */
	this.notNullCheck = function(name,checkBoxs){
		if (!(checkBoxs instanceof $)) checkBoxs = $('input[name=' + checkBoxs + ']:checkbox');
		me.options.push(function(){
			var checked = $.findChecked(checkBoxs);
			if (checked == 0) {
				alert(errors.required.format(message));
				return false;
			} else return true;
		});
		return me;
	}
	
	/** value보다 값이 같거나 작아야 한다.   */
	this.small = function(value){
		var dom = me.dom, name = dom.data('domName');
		if (!(target instanceof $)) target = $('#'+target);
		me.options.push(function(){
			var A = dom.val();
			var B = target.val();
			if(A=='' || B=='') return true;
			var targetName = target.data('domName');
			if(A > B){
				alert(errors.smallValue.format([name,targetName]));
				dom.select();
				return false;	
			}else return true;
		});
		return me;
	}
	
	/** 숫자를 체크한다. 정수의 경우 input의 size로 자리수 제한이 가능하다.
	 * 따라서 사이즈 제한의 경우 소수로 간주한다.  */
	this.num = function(intSize, fractionSize){
		var dom = me.dom, name = dom.data('domName');
		me.options.push(function(){
			var value = name.val();
			if(value=='') return true;
			if(intSize){ //실수
				if(!value.isDecimal()){
					alert(errors.integer.format(message));
					dom.select();
					return false;
				}
				var part = [ value, '' ];
				var dotIndex = value.indexOf('.');
				if (dotIndex != -1) part = value.split('.');
				if (part[0].length > intSize) {
					alert(errors.limitlength.format( [ message[0] + '의 정수부', intSize ]));
					name.select();
					return false;
				}
				if (part[1].length > fractionSize) {
					alert(errors.limitlength
							.format( [ message[0] + '의 소수부', fractionSize ]));
					name.select();
					return false;
				}
			}else{ //정수
				if(!value.isNumeric()){
					alert(errors.integer.format(message));
					dom.select();
					return false;
				}	
			}
			return true;
			
		});
		return me;
	}
}
/** 유효성검사기 Option */
var O = new Object();

/** month인지 여부 */
O.isNotMonth = function(name) {
	if (!(name instanceof $))
		name = $('#' + name);
	var value = name.val();

	if (O.isNotNumber(name, [ '월' ]))
		return true;
	var num = value.toNumber();
	if (num >= 1 && num <= 12)
		return false;
	alert('월은 01 ~ 12 사이의 값을 입력하여야 합니다.');
	name.select();
	return true;
}
/** year인지 여부 */
O.isNotYear = function(name) {
	if (!(name instanceof $))
		name = $('#' + name);
	var value = name.val();
	if (value >= '1900' && value <= '2999')
		return false;
	alert('1900 ~ 2999 사이의 값을 입력하여야 합니다.');
	name.select();
	return true;
}

/** 시작일-종료일을 벨리데이션 체크한다.
 * ex) if(O.validateBetweenDate('stTime','clseTime',['시작일','종료일'])) return; */
O.validateBetweenDate = function(A, B, names) {
	if (!(A instanceof $))
		A = $('#' + A);
	if (!(B instanceof $))
		B = $('#' + B);
	if (O.isNull(A, [ names[0] ]))
		return true;
	if (O.isNull(B, [ names[1] ]))
		return true;
	if (O.isNotDate(A))
		return true;
	if (O.isNotDate(B))
		return true;
	if (O.isLargeThan(A, B, names))
		return true;
	return false;
}

/** 시작년-종료년도를 벨리데이션 체크한다.  */
O.validateBetweenYear = function(A, B, names) {
	A = $('#' + A);
	B = $('#' + B);
	if (O.isNull(A, [ names[0] ]))
		return true;
	if (O.isNull(B, [ names[1] ]))
		return true;
	if (O.isNotYear(A))
		return true;
	if (O.isNotYear(B))
		return true;
	if (O.isLargeThan(A, B, names))
		return true;
	return false;
}
/** 필수입력항목인 일자를 벨리데이션 체크한다. */
O.validateDate = function(A, names) {
	A = $('#' + A);
	if (O.isNull(A, names))
		return true;
	if (O.isNotDate(A))
		return true;
	return false;
}
/** 영문/숫자 조합이 아니라면. 첫문자는 숫자가 올 수 없다. */
O.isNotAlphanumeric = function(A, names) {
	A = $('#' + A);
	var value = A.val();
	if (value[0].isNumeric()) {
		alert('{0}의 첫 문자는 숫자일 수 없습니다.'.format(names));
		A.select();
		return true;
	}
	if (!value.isAlphaNumeric()) {
		alert('{0}에는 영문/숫자조합만 올 수 있습니다.'.format(names));
		A.select();
		return true;
	}
	return false;
}