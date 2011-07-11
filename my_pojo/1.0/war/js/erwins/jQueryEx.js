/** btn에는 보통 이벤트로 this를 넘기면 된다.  추후 덕타입으로 교체할것.
 * ex) 
 * var doGoogleLogin =  $("#doGoogleLogin").button({ label: "Google 계정으로 로그인하러 가기." }).click(function(e){
		$.send('/platform/login',{openid_identifier:'google'},function(message){
			doGoogleLogin.button("option", "label", "로그인을 위해 Google로 이동하는 중입니다. 기다려 주세요.").button( "option", "disabled", false);
			window.location = message;
		},doGoogleLogin);
	});
*/
/** afterMessage가 있으면 보통 처리후 다른페이지로 이동한다.  */
$.send = function(url,data,callback,btn,afterMessage){
	if(btn){
		btn.btnName = btn.button("option", "label");
		btn.button("option", "label", "처리중입니다.").button( "option", "disabled", true );
	}
	$.ajax({
		type:'GET',url:url,dataType:'json',data:data,
		success:function(json,status){
			if(btn){
				if(afterMessage) btn.button("option", "label", afterMessage);
				else btn.button("option", "label", btn.btnName).button( "option", "disabled", false );
			}
			if(json.isSuccess) callback(json.message,json);
			else{
				if(btn) btn.button("option", "label", btn.btnName).button( "option", "disabled", false );
				alert("작업 실패\n" + json.message);
			}
		},error:function(xhr,status,error){
			alert('오류! \n'+status + error);
			if(btn) btn.button("option", "label", btn.btnName).button( "option", "disabled", false ); 
		}
	});
};
$.bindEnter = function(btn,input,func){
	btn.click(func);
	input.keyup(function(e){
		var keyCode =  e.keyCode;
		if(keyCode==13){
			func();
		}	
	});
};