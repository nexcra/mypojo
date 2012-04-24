/** Ext.js를 사용하기 위한 도구 */   
var ExtUtil = new Object();

/** 라디오의 경우 클릭직후와 라디오 포커스가 옮겨간 후 2가지 체인지 이벤트가 일어난다.
 * 우리가 원하는건 마지막 이벤트 임으로 2개가 다중 선택된 상태인 첫번째 이벤트는 스킵해준다. */ 
ExtUtil.extRadioChange = function(callback){
	return function(field,newValue,oldValue,eOpts){
	   var key; 
	   for(var k in newValue) key = k;
	   var value = newValue[key];
	   if(value instanceof Array) return; //2개가 오면 스킵
	   callback(field,newValue,oldValue,eOpts,value);
	};
};