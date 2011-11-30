
/**
 * String 객체 문자열을 날짜로 변환한다.
 * =============================================================================
 * Letters Component
 * -----------------------------------------------------------------------------
 * yyyy Year MM Month dd Date HH Hour mm Minute SS Second sss Milli-Second
 * =============================================================================
 */
String.prototype.toDate = function(pattern) {
	if (this.length != pattern.length) {
		return null;
	}
	var yearBegin = pattern.indexOf("y");
	var yearEnd = pattern.lastIndexOf("y");
	var monthBegin = pattern.indexOf("M");
	var monthEnd = pattern.lastIndexOf("M");
	var dayBegin = pattern.indexOf("d");
	var dayEnd = pattern.lastIndexOf("d");
	var hourBegin = pattern.indexOf("H");
	var hourEnd = pattern.lastIndexOf("H");
	var minuteBegin = pattern.indexOf("m");
	var minuteEnd = pattern.lastIndexOf("m");
	var secondBegin = pattern.indexOf("S");
	var secondEnd = pattern.lastIndexOf("S");
	var millisecondBegin = pattern.indexOf("s");
	var millisecondEnd = pattern.lastIndexOf("s");

	var date = new Date(1970, 0, 1);

	var year = yearBegin < 0 ? date.getFullYear() : parseInt(this.substring(yearBegin, yearEnd + 1), 10);
	var month = monthBegin < 0 ? date.getMonth() + 1 : parseInt(this.substring(monthBegin, monthEnd + 1), 10);
	var day = dayBegin < 0 ? date.getDate() : parseInt(this.substring(dayBegin, dayEnd + 1), 10);
	var hour = hourBegin < 0 ? 0 : parseInt(this.substring(hourBegin, hourEnd + 1), 10);
	var minute = minuteBegin < 0 ? 0 : parseInt(this.substring(minuteBegin, minuteEnd + 1), 10);
	var second = secondBegin < 0 ? 0 : parseInt(this.substring(secondBegin, secondEnd + 1), 10);
	var millisecond = millisecondBegin < 0 ? 0 : parseInt(this.substring(millisecondBegin, millisecondEnd + 1), 10);

	// date.setMilliseconds(millisecond);
	// date.setSeconds(second);
	// date.setMinutes(minute);
	// date.setHours(hour);
	// date.setDate(day);
	// date.setMonth(month - 1);
	// date.setFullYear(year);

	return new Date(year, month - 1, day, hour, minute, second, millisecond);
}

String.prototype.startsWith = function(str){return (this.match("^"+str)==str)}

String.prototype.endsWith = function(str){return (this.match(str+"$")==str)}

/**
 * 문자열의 정규식 특수문자를 치환한다. Usage: string.meta()
 */
String.prototype.meta = function() {
	var replace = "";

	var pattern = new RegExp("([\\$\\(\\)\\*\\+\\.\\[\\]\\?\\\\\\^\\{\\}\\|]{1})", "");

	for ( var i = 0; i < this.length; i++) {
		if (pattern.test(this.charAt(i))) {
			replace = replace + this.charAt(i).replace(pattern, "\\$1");
		} else {
			replace = replace + this.charAt(i);
		}
	}
	return replace;
}


/**
 * 전부 치환한다. Usage: string.replaceAll('A','/')
 */
String.prototype.replaceAll = function(searchStr,replaceStr) {
	var body = this;
	while (body.indexOf(searchStr) != -1) {
		body = body.replace(searchStr, replaceStr);
	}
	return body;
}

/**
 * HTML tag부분을 없애준다
 */
String.prototype.removeTags = function() {
	return this.replace(/<[^>]+>/g, "");  
}

/**
 * 단어의 첫 글자를 대문자로 바꿔준다.
 */
String.prototype.capitalize = function() {
    return this.replace(/\b([a-z])/g, function($1){ 
        return $1.toUpperCase(); 
    }); 
}

/**
 * 문자열의 공백 제거
 */
String.prototype.trim = function() {
	return this.replace(/(^\s*)|(\s*$)/g, "");
}

/**
 * 문자열의 좌측에 특정문자를 덧붙인다. Usage: string.(size) string.(size, character)
 */
String.prototype.lpad = function(size) {
	var character = arguments.length > 1 ? arguments[1] : "0";
	var append = "";
	if (this.length < size) {
		for ( var i = 0; i < size - this.length; i++) {
			append = append + "0";
		}
	}
	return append + this;
}

/**
 * 문자열의 우측에 특정문자를 덧붙인다. Usage: string.rpad(size) string.rpad(size, character)
 */
String.prototype.rpad = function(size) {
	var character = arguments.length > 1 ? arguments[1] : "0";
	var append = "";
	if (this.length < size) {
		for ( var i = 0; i < size - this.length; i++) {
			append = append + "0";
		}
	}
	return this + append;
}

/**
 * 문자열의 바이트 배열길이를 반환한다. Usage: string.bytes()
 */
String.prototype.bytes = function() {
	var pattern = new RegExp("%u", "g");
	return this.length + (escape(this) + "%u").match(pattern).length - 1;
}

/**
 * 문자열을 숫자로 바꿔준다.
 */
String.prototype.toNumber = function(){
	return Number(this);
}

/**
 * 문자열을 해당 자리수 만큼의 실수로 변환한다. (인자로 0 입력시 정수형) 표시할수 있는 자리수+1에서 반올림한다. <br>
 * Usage: string.toDecimal(2) => 123.45
 *  -> 수정할것
 */
String.prototype.toDecimal = function() {
	var pattern = new RegExp("[^\\-0-9\\.]", "g");
	var value = this.replace(pattern, "");
	var temp = parseFloat(value, 10); // >.<
	var range = arguments.length > 0 ? Number(arguments[0]) : null;
	if (range == null) {
		return temp;
	} else if (range == 0) {
		return Math.round(temp);
	} else {
		range = Math.pow(10.0, range);
		return Math.round(temp * range) / range;
	}
}

/**
 * 문자열을 실수로 변환 후 3저리마다 ,를 찍어준다. Usage: string.toNumeric()
 * fix가 2라면 1234.2 -> 1,234.20 으로 변환한다.
 */
String.prototype.toNumeric = function(fix) {
	var extra = "";
	var value = this.toDecimal().toString();
	if(value=='NaN') return '0'; //이거 주의!
	var index = value.indexOf(".");
	if (index > 0) {
		extra = value.substring(index);
		value = value.substring(0, index);
		
		//검증안됨
		if(fix!=null){
			var added = fix - extra.length +1;
			if(added > 0) for(var i=0;i<added;i++) extra+= '0';
			else if(added < 0) extra = extra.substring(0,fix+1);
		}
		
	}
	var pattern = new RegExp("(\\-?[0-9]+)([0-9]{3})", "");
	while (pattern.test(value)) {
		value = value.replace(pattern, "$1,$2");
	}
	return value + extra;
	
	/*		var input = String(input); 
		    var reg = /(\-?\d+)(\d{3})($|\.\d+)/; 
		    if(reg.test(input)){ 
		        return input.replace(reg, function(str, p1,p2,p3){ 
		                return number_(p1) + "," + p2 + "" + p3; 
		            }     
		        ); 
		    }else{ 
		        return input; 
		    } 
		    */
}

/**
 * 문자열을 유선전화번호로 변환한다.
 * Usage: string.toPhone() string.toPhone(delimiter)
 */
String.prototype.toPhone = function() {
	var delimiter = arguments.length > 0 ? arguments[0] : "";
	var phoneNo = this.toDecimal();
	if (phoneNo.indexOf("02") == 0) {
		if (phoneNo.length > 9) {
			return phoneNo.substring(0, 2) + delimiter + phoneNo.substring(2, 6) + delimiter + phoneNo.substring(6);
		} else if (phoneNo.length > 5) {
			return phoneNo.substring(0, 2) + delimiter + phoneNo.substring(2, 5) + delimiter + phoneNo.substring(5);
		} else if (phoneNo.length > 2) {
			return phoneNo.substring(0, 2) + delimiter + phoneNo.substring(2);
		}
	} else {
		if (phoneNo.length > 10) {
			return phoneNo.substring(0, 3) + delimiter + phoneNo.substring(3, 7) + delimiter + phoneNo.substring(7);
		} else if (phoneNo.length > 6) {
			return phoneNo.substring(0, 3) + delimiter + phoneNo.substring(3, 6) + delimiter + phoneNo.substring(6);
		} else if (phoneNo.length > 3) {
			return phoneNo.substring(0, 3) + delimiter + phoneNo.substring(3);
		}
	}
}

/**
 * 문자열을 무선전화번호로 변환한다.
 * Usage: string.toMobile() string.toMobile(delimiter)
 */
String.prototype.toMobile = function() {
	var delimiter = arguments.length > 0 ? arguments[0] : "";
	var mobileNo = this.toDecimal();
	if (mobileNo.length > 10) {
		return mobileNo.substring(0, 3) + delimiter + mobileNo.substring(3, 7) + delimiter + mobileNo.substring(7);
	} else if (mobileNo.length > 6) {
		return mobileNo.substring(0, 3) + delimiter + mobileNo.substring(3, 6) + delimiter + mobileNo.substring(6);
	} else if (mobileNo.length > 3) {
		return mobileNo.substring(0, 3) + delimiter + mobileNo.substring(3);
	}
}

/**
 * String 문자열에 숫자를 더한다.
 */
String.prototype.plus = function(arg) {
	if (arg)
		return Number(this) + arg;
	else
		return Number(this) + 1;
}
 
/** 확장자를 리턴한다. */
String.prototype.getExt = function(arg) {
	if(!arg) arg = '.';
	var i = str.lastIndexOf('.');
	if(i == -1) return '';
	return this.substring(i+1,str.length);
}

/** 대소문자를 가리지 않고 두 문자를 비교한다. */
String.prototype.eq = function(arg) {
	return this.toUpperCase() == arg.toUpperCase();
}

/** span태그를 입혀서 리턴한다.
Ext.js에서 사용한다. */
String.prototype.toSpan = function(color,isBold){
	var list = ['color:'+color+';']; 
	if(isBold) list.push('font-weight:bold;');
	return '<span style="'+list.join('')+'" >'+this+'</span>';
}
String.prototype.toBold = function(){
	return '<b>'+this+'</b>';
}

/** Java의 Message을 따라한다. */
String.prototype.format = function(list) {
	var message = this;
	for(var i=0;i<list.length;i++) message = message.replaceAll('{'+i+'}',list[i]);
	return message;
}

/** 하나라도 같으면 true를 리턴한다. */
String.prototype.equals = function(param) {
	if(param == null) return false;
	if( !(param instanceof Array) ) param = [param];
	for(var i=0;i<param.length;i++) 
		if(this == param[i]) return true;
	return false;
}
/** jQuery용 DOM으로 만든다. */
String.prototype.dom = function() {
	if(this == '') return null;
	var target = $('#' + this);
	if(target.length == 0) return null;
	return target;
}
/** '123456'.splitSet(2) 하면 ['12','34','56'] 일케 나온다.
 * 반드시 나머지가 없어야 한다. -> 추후 수정.  */
String.prototype.splitSet = function(size) {
	var count = this.length / size ;
	var array = [];
	for(var i=0;i<this.length;i+=size){
		array.push(this.substring(i,i+size));
	}
	return array;
}

/** 문자열이 있는지 검사 : 필요하면 어퍼케이스 포함 */
String.prototype.contains = function(str) {
	var index = this.indexOf(str);
	if(index < 0) return false;
	return true;
}


// ============   벨리데이션 =============


/**
 * 문자열의 캐릭터 배열길이가 최소값과 최대값 사이인지 확인한다. Usage: string.isLength()
 * string.isLength(minimum) string.isLength(minimum, maximum)
 */
String.prototype.isLength = function() {
	var minimum = arguments.length > 0 ? arguments[0] : 0;
	var maximum = arguments.length > 1 ? arguments[1] : 0;
	if (minimum > 0 && this.length < minimum) {
		return false;
	}
	if (maximum > 0 && this.length > maximum) {
		return false;
	}
	return true;
}

/**
 * 문자열의 바이트 배열길이가 최소값과 최대값 사이인지 확인한다. Usage: string.isBytes()
 * string.isBytes(minimum) string.isBytes(minimum, maximum)
 */
String.prototype.isBytes = function() {
	var minimum = arguments.length > 0 ? arguments[0] : 0;
	var maximum = arguments.length > 1 ? arguments[1] : 0;
	if (minimum > 0 && this.bytes() < minimum) {
		return false;
	}
	if (maximum > 0 && this.bytes() > maximum) {
		return false;
	}
	return true;
}

/**
 * 문자열이 숫자형인지 확인한다. -> -부호가 안먹는듯?
 */
String.prototype.isNumeric = function(){
    var pattern = new RegExp("^[0-9]+$", "");
    return pattern.test(this);
}

/**
 * 문자열이 영어인지 확인한다. Usage: string.isAlpha() string.isAlpha(ignores)
 */
String.prototype.isAlpha = function() {
	var ignores = arguments.length > 0 ? arguments[0] : "";
	var pattern = new RegExp("^[a-zA-Z]+$", "");
	return pattern.test(this);
}

/**
 * 문자열이 영어와 숫자인지 확인한다. Usage: string.isAlphaNumeric()
 * string.isAlphaNumeric(ignores)
 */
String.prototype.isAlphaNumeric = function() {
	var ignores = arguments.length > 0 ? arguments[0] : "";
	var pattern = new RegExp("^[0-9a-zA-Z]+$", "");
	return pattern.test(this);
}

/** -와 _가 추가된 버전 */
String.prototype.isAlphaSymbol = function() {
	var ignores = arguments.length > 0 ? arguments[0] : "";
	var pattern = new RegExp("^[-a-zA-Z0-9_]+$", "");
	return pattern.test(this);
}

/**
 * 문자열이 주민등록번호인지 확인한다. Usage: string.isResRegNo() string.isResRegNo(delimiter)
 */
String.prototype.isResRegNo = function() {
	var delimiter = arguments.length > 0 ? arguments[0] : "";
	var pattern = new RegExp("[0-9]{2}[01]{1}[0-9]{1}[0123]{1}[0-9]{1}" + delimiter.meta() + "[1234]{1}[0-9]{6}$", "");
	var resRegNo = this.match(pattern);
	if (resRegNo == null) {
		return false;
	}
	resRegNo = resRegNo.toString().toDecimal();
	var year = resRegNo.substring(0, 2);
	switch (resRegNo.charAt(6)) {
	case "1":
	case "2":
		year = "19" + year;
		break;
	case "3":
	case "4":
		year = "20" + year;
		break;
	default:
		return false;
	}
	var month = resRegNo.substring(2, 4);
	var date = resRegNo.substring(4, 6);
	var yearMonthDate = new Date(parseInt(year, 10), parseInt(month, 10) - 1, parseInt(date, 10));
	if (yearMonthDate.getFullYear() != parseInt(year, 10)) {
		return false;
	}
	if (yearMonthDate.getMonth() != parseInt(month, 10) - 1) {
		return false;
	}
	if (yearMonthDate.getDate() != parseInt(date, 10)) {
		return false;
	}

	var sum = 0;
	var mod = [ 2, 3, 4, 5, 6, 7, 8, 9, 2, 3, 4, 5 ];
	for ( var i = 0; i < 12; i++) {
		sum = sum + (parseInt(resRegNo.charAt(i), 10) * mod[i]);
	}
	return (11 - sum % 11) % 10 == parseInt(resRegNo.charAt(12), 10);
}

/**
 * 문자열이 법인등록번호인지 확인한다. Usage: string.isCorRegNo() string.isCorRegNo(delimiter)
 * 전산화 되기 이전의 자료(고엽제 전우회..)는 이것이 안될수도 있으니 주의할것.
 */
String.prototype.isCorRegNo = function() {
	var delimiter = arguments.length > 0 ? arguments[0] : "";
	var pattern = new RegExp("[0-9]{6}" + delimiter.meta() + "[0-9]{7}$", "");
	var corRegNo = this.match(pattern);
	if (corRegNo == null) {
		return false;
	}
	corRegNo = corRegNo.toString();
	var sum = 0;
	var mod = [ 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2 ];
	for ( var i = 0; i < 12; i++) {
		sum = sum + (parseInt(corRegNo.charAt(i), 10) * mod[i]);
	}
	return (10 - sum % 10) % 10 == parseInt(corRegNo.charAt(12), 10);
}

/**
 * 문자열이 외국인등록번호인지 확인한다. Usage: string.isForRegNo() string.isForRegNo(delimiter)
 */
String.prototype.isForRegNo = function() {
	var delimiter = arguments.length > 0 ? arguments[0] : "";
	var pattern = new RegExp("[0-9]{2}[01]{1}[0-9]{1}[0123]{1}[0-9]{1}" + delimiter.meta()
			+ "[5678]{1}[0-9]{1}[02468]{1}[0-9]{2}[6789]{1}[0-9]{1}$", "");
	var forRegNo = this.match(pattern);
	if (forRegNo == null) {
		return false;
	}
	forRegNo = forRegNo.toString().toDecimal();
	var year = forRegNo.substring(0, 2);
	switch (forRegNo.charAt(6)) {
	case "5":
	case "6":
		year = "19" + year;
		break;
	case "7":
	case "8":
		year = "20" + year;
		break;
	default:
		return false;
	}
	var month = forRegNo.substring(2, 4);
	var date = forRegNo.substring(4, 6);
	var yearMonthDate = new Date(parseInt(year, 10), parseInt(month, 10) - 1, parseInt(date, 10));
	if (yearMonthDate.getFullYear() != parseInt(year, 10)) {
		return false;
	}
	if (yearMonthDate.getMonth() != parseInt(month, 10) - 1) {
		return false;
	}
	if (yearMonthDate.getDate() != parseInt(date, 10)) {
		return false;
	}
	if ((parseInt(forRegNo.charAt(7), 10) * 10 + parseInt(forRegNo.charAt(8), 10)) % 2 != 0) {
		return false;
	}
	var sum = 0;
	var mod = [ 2, 3, 4, 5, 6, 7, 8, 9, 2, 3, 4, 5 ];
	for ( var i = 0; i < 12; i++) {
		sum = sum + (parseInt(forRegNo.charAt(i), 10) * mod[i]);
	}
	return ((11 - sum % 11) % 10) + 2 == parseInt(forRegNo.charAt(12), 10);
}

/**
 * 문자열이 사업자등록번호인지 확인한다. Usage: string.isBizRegNo() string.isBizRegNo(delimiter)
 */
String.prototype.isBizRegNo = function() {
	var delimiter = arguments.length > 0 ? arguments[0] : "";
	var pattern = new RegExp("[0-9]{3}" + delimiter.meta() + "[0-9]{2}" + delimiter.meta() + "[0-9]{5}$", "");
	var bizRegNo = this.match(pattern);
	if (bizRegNo == null) {
		return false;
	}
	bizRegNo = bizRegNo.toString().toDecimal();
	var sum = parseInt(bizRegNo.charAt(0), 10);
	var mod = [ 0, 3, 7, 1, 3, 7, 1, 3 ];
	for ( var i = 1; i < 8; i++) {
		sum = sum + ((parseInt(bizRegNo.charAt(i), 10) * mod[i]) % 10);
	}
	sum = sum + Math.floor(parseInt(parseInt(bizRegNo.charAt(8), 10), 10) * 5 / 10);
	sum = sum + ((parseInt(bizRegNo.charAt(8), 10) * 5) % 10 + parseInt(bizRegNo.charAt(9), 10));
	return sum % 10 == 0;
}

/**
 * 문자열이 전자우편주소인지 확인한다. Usage: string.isEmail()
 */
String.prototype.isEmail = function() {
	var pattern = new RegExp("\\w+([\\-\\+\\.]\\w+)*@\\w+([\\-\\.]\\w+)*\\.[a-zA-Z]{2,4}$", "");
	return pattern.test(this);
}

/**
 * 문자열이 유선전화번호인지 확인한다. Usage: string.isPhone() string.isPhone(delimiter)
 */
String.prototype.isPhone = function() {
	var delimiter = arguments.length > 0 ? arguments[0] : "";
	var pattern = new RegExp("(02|0[3-9]{1}[0-9]{1})" + delimiter.meta() + "[1-9]{1}[0-9]{2,3}" + delimiter.meta()
			+ "[0-9]{4}$", "");
	return pattern.test(this);
}

/**
 * 문자열이 무선전화번호인지 확인한다. Usage: string.isMobile() string.isMobile(delimiter)
 */
String.prototype.isMobile = function() {
	var delimiter = arguments.length > 0 ? arguments[0] : "";
	var pattern = new RegExp("01[016789]" + delimiter.meta() + "[1-9]{1}[0-9]{2,3}" + delimiter.meta() + "[0-9]{4}$","");
	return pattern.test(this);
}
 