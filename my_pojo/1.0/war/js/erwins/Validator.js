/**
 * jQuery와 PrototypeEx.js 가 있아야 한다.
 * required : 
 * max : 
 */

/** if(! new Validator().validate()) return; */
var Validator = function() {
	
}

Validator.prototype.validate = function() {
	var inputs = $("input");
    for(var i=0;i<inputs.length;i++){
    	var $e =  $(inputs[i]);
    	var value = $e.val();
    	var title = $e.attr("title");
    	if(title=='') title = "해당항목";
    	var type = $e.attr("type");
    	var options = $e.attr("op")==null ? new Array() : $e.attr("op").split(",");
    	
    	for(var j=0;j<options.length;j++){
    		var option = options[j]; 
    		if(option == 'email'){
    			if(value!='' &&  !value.isEmail()){
    				alert("[ "+title + " ] 은(는) E-MAIL형식이 와야 합니다.");
        			$e.focus();
        			return false;
    			}
    		}
    		if(option == 'mobile'){
    			if(value!='' && !value.isMobile()){
    				alert("[ "+title + " ] 은(는) 휴대전화번호 형식이 와야 합니다.");
    				$e.focus();
    				return false;
    			}
    		}
    		if(option == 'required'){
    			switch(type){
    			case 'text':
    			case 'password':
    			case 'select-one':
    			case 'select-multiple':
    				if(value==""){
            			alert("[ "+title + " ] 은(는) 필수입력항목입니다.");
            			$e.focus();
            			return false;
            		}
    				break;
    			case 'checkbox' :
    			case 'radio' :
    				var name = $e.attr("name");
    				if($('input[name="'+name+'"]:checked').length==0){
    					alert("[ "+title + " ] 은(는) 필수입력항목입니다.");
            			$e.focus();
    					return false;
    				}
    			}
    		}  		
    	}
    }
    var textareas = $("textarea");
    for(var i=0;i<textareas.length;i++){
    	var $e =  $(textareas[i]);
    	var value = $e.val();
    	var title = $e.attr("title");
    	if(title==null) title = "해당항목";
    	var options = $e.attr("op")==null ? ["max:500"] : $e.attr("op").split(",");
    	for(var j=0;j<options.length;j++){
    		var option = options[j]; 
    		if(option.startsWith("required")){
    			if(value==""){
        			alert("[ "+title + " ] 은(는) 필수입력항목입니다.");
        			$e.focus();
        			return false;
        		}
    		}else if(option.startsWith("max")){
    			option = option.split(":")[1].toNumber();
    			if(!value.isBytes(0,option)){
    				alert("[ "+title + " ] 에 너무많은값을 입력하셨습니다. \n현재 ["+value.bytes()+"], 최대치 [" + option + "]");
        			$e.focus();
        			return false;
    			}
    		}
    	}
    }
    return true;
}

/*var Validator = function() {
	
}
Validator.prototype.validate = function() {
	alert('asd'); 
}
*/
/**
 * arguments[0,1] 에 대한 default init
 *//*
Validator.prototype.init = function(id, message) {
	this.id = id;
	this.message = message; 
	this.obj = $(id);
	if (this.obj) {
		this.value = this.obj.value;
		// 라디오 등등 추가.
	} else {
		this.value = '';
	}
}

*//**
 * error를 던진다.
 *//*
Validator.prototype.error = function() {
	var error = new Error(this.message);
	error.id = this.id;
	throw error;
}

*//**
 * $(this.field) 대신에 $F(this.field)를 쓰면 안된다. null error!
 * 표준 브라우저는 null에 관대하지 않다.
 *//*
Validator.prototype.req = function() {
	this.init(arguments[0], arguments[1]);
	if (this.value == '')
		this.error();
}

*//**
 * 이것 외의 특수문자는 알아서 추가하자.. 정규식 이스케이프 활용할것.
 *//*
Validator.prototype.like = function() {
	this.init(arguments[0], arguments[1]);
	var keys = [ '%', '_' ];
	for ( var i = 0; i < keys.length; i++) {
		if (this.value.match(keys[i]))
			this.error();
	}
}

*//**
 * 최소값 지정
 * 최대값은 maxlength로 지정하자.
 *//*
Validator.prototype.ge = function() {
	this.init(arguments[0], arguments[1]);
	if (!this.value.isLength(arguments[2])) {
		this.error();
	}
}

*//**
 * textArea 등에 byte값을 넘어가는지 체크.
 *//*
Validator.prototype.bytes = function() {
	this.init(arguments[0], arguments[1]);
	if (!this.value.isBytes(arguments[2], arguments[3])) {
		this.error();
	}
}
*//**
 * text length값을 넘어가는지 체크.
 *//*
Validator.prototype.length = function() {
	this.init(arguments[0], arguments[1]);
	if (!this.value.isLength(arguments[2], arguments[3])) {
		this.error();
	}
}

*//**
 * 체크박스에 체크된 값이 있는지?
 *//*
Validator.prototype.checked = function() {
	this.init(arguments[0], arguments[1]);
	// asd
}

*//**
 * 숫자형인지?
 *//*
Validator.prototype.numeric = function() {
	this.init(arguments[0], arguments[1]);
	if (!this.value.isNumeric())
		this.error();
}
*//**
 * 정수형인지?
 *//*
Validator.prototype.integer = function() {
	this.init(arguments[0], arguments[1]);
	if (!this.value.isInteger())
		this.error();
}
*//**
 * 실수형인지?
 *//*
Validator.prototype.decimal = function() {
	this.init(arguments[0], arguments[1]);
	if (!this.value.isDecimal())
		this.error();
}
*//**
 * date형인지?
 * arguments[2] = pattern
 *//*
Validator.prototype.date = function() {
	this.init(arguments[0], arguments[1]);
	if (!this.value.isDate(arguments[2]))
		this.error();
}
*//**
 * 알파벳인지?
 * arguments[2] = ignores
 *//*
Validator.prototype.alpha = function() {
	this.init(arguments[0], arguments[1]);
	if (!this.value.isAlpha(arguments[2]))
		this.error();
}
*//**
 * 한글인지?
 * arguments[2] = ignores
 *//*
Validator.prototype.korean = function() {
	this.init(arguments[0], arguments[1]);
	if (!this.value.isKorean(arguments[2]))
		this.error();
}
*//**
 * 알파벳+숫자인지?
 * arguments[2] = ignores
 *//*
Validator.prototype.alphaNumeric = function() {
	this.init(arguments[0], arguments[1]);
	if (!this.value.isAlphaNumeric(arguments[2]))
		this.error();
}
*//**
 * 주민등록번호인지?
 * arguments[2] = delimiter
 *//*
Validator.prototype.resRegNo = function() {
	this.init(arguments[0], arguments[1]);
	if (!this.value.isResRegNo(arguments[2]))
		this.error();
}
*//**
 * 법인등록번호인지?
 * arguments[2] = delimiter
 *//*
Validator.prototype.corRegNo = function() {
	this.init(arguments[0], arguments[1]);
	if (!this.value.isCorRegNo(arguments[2]))
		this.error();
}
*//**
 * 외국인등록번호인지?
 * arguments[2] = delimiter
 *//*
Validator.prototype.forRegNo = function() {
	this.init(arguments[0], arguments[1]);
	if (!this.value.isForRegNo(arguments[2]))
		this.error();
}
*//**
 * 사업자등록번호인지?
 * arguments[2] = delimiter
 *//*
Validator.prototype.bizRegNo = function() {
	this.init(arguments[0], arguments[1]);
	if (!this.value.isBizRegNo(arguments[2]))
		this.error();
}
*//**
 * 유선전화인지?
 * arguments[2] = delimiter
 *//*
Validator.prototype.phone = function() {
	this.init(arguments[0], arguments[1]);
	if (!this.value.isPhone(arguments[2]))
		this.error();
}
*//**
 * 무선전화인지?
 * arguments[2] = delimiter
 *//*
Validator.prototype.mobile = function() {
	this.init(arguments[0], arguments[1]);
	if (!this.value.isMobile(arguments[2]))
		this.error();
}
*//**
 * 이메일인지?
 *//*
Validator.prototype.email = function() {
	this.init(arguments[0], arguments[1]);
	if (!this.value.isEmail())
		this.error();
}*/