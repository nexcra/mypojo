// 프로젝트별로 달라지는 초기
/** 
 * datepicker의 한글화. 크기조절 CSS를 입히기도 한다.
 *  .ui-datepicker { width: 25em; padding: .2em .2em 0; font-size:68.5%;} */
jQuery(function($){
	 $.datepicker.regional['ko'] = {
			 closeText: '닫기',
			 prevText: '이전달',
			 nextText: '다음달',
			 currentText: '오늘',
			 monthNames: ['1월','2월','3월','4월','5월','6월','7월','8월','9월','10월','11월','12월'],
			 monthNamesShort: ['1월','2월','3월','4월','5월','6월','7월','8월','9월','10월','11월','12월'],
			 dayNames: ['일','월','화','수','목','금','토'],
			 dayNamesShort: ['일','월','화','수','목','금','토'],
			 dayNamesMin: ['일','월','화','수','목','금','토'],
			 weekHeader: 'Wk',
			 dateFormat: 'yy.mm.dd',
			 firstDay: 0,
			 isRTL: false,
			 duration:200,
			 showAnim:'show',
			 showMonthAfterYear: false,
			 changeYear: true, 
			 yearSuffix: '년'};
	 $.datepicker.setDefaults($.datepicker.regional['ko']);
});