
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