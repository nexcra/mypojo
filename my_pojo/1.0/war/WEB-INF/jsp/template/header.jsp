<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="erwins.webapp.myApp.DefaultController"%>
<%@ page isELIgnored="false" %>

<script type="text/javascript">

Ext.require([ 'Ext.panel.Panel', 'Ext.Action', 'Ext.button.Button', 'Ext.window.MessageBox'
              ,'Ext.tip.*']);

Ext.onReady(function() {
	Ext.QuickTips.init();
	var tb = Ext.create('Ext.toolbar.Toolbar', {
		id : 'headerToolbar',
		renderTo : 'headerToolbarDiv',
		//width : 600,
		autoWidth:true,
		bodyPadding : 10,
		items : [
			{text:'<b>영감님의 테스트 서버입니다</b>',handler: function(){ window.location = '/'; },tooltip: 'Add user'}
			,'-'
			,{text:'매직더게더링',handler: function(){ window.location = '/rest/mtgo/page'; } }
			,{text:'테스트페이지',handler: function(){ window.location = '/rest/none/test'; } }
			,{text:'간단변환기',handler: function(){ window.location = '/rest/translator/page'; } }
			,{text:'맵라벨',handler: function(){ window.location = '/rest/mapLabel/page'; } }
			,{text:'사용자',handler: function(){ window.location = '/rest/user/page'; } }
			,'-'
			,'          '
			,'-'
		]
	});
	
	//채팅서버 관련
	var dialog = $( "#dialog" ).dialog({ autoOpen: false });
	/*
	$.bindEnter(chatSend,messageInput,function(){
		$.send('/rest/none/channel/chat',{message:messageInput.attr("value")},function(message){
			messageInput.attr("value","");
			$('#serverInfo').text('[' + message + '] : 명에게 메세지 전송');
		},chatSend);
	});*/
	var doChat = function(){
		$.send('/rest/none/channel/chat',{message:inputField.value},function(message){
			inputField.setValue('');
			helpMessage.setValue('[' + message + '] : 명에게 메세지 전송');
		});
	}
	var accessBtn = tb.add( {text:'채팅서버 접속',handler: function(){
		$.send('/rest/none/channel/create',null,function(token){
			GoogleAppEngine.init = function(){
				helpMessage.setValue('서버로 연결 중입니다..');
			}
			GoogleAppEngine.onopen = function(){
				helpMessage.setValue('정상 접속됨');
			}
			GoogleAppEngine.onerror = function(){
				$.send('/rest/none/channel/remove',{key:token},function(message){
					helpMessage.setValue('푸시서비스에 오류. 재연결 필요시 F5를 눌러주세요');
				});
			}
			GoogleAppEngine.start(token,function(message){
				dialog.append("<p>"+message.message+"</p>").dialog('open');
			});
			accessBtn.hide();
			inputField.show();
			sendBtn.show();
		});
	}});
	
	var inputField = tb.add( {xtype: 'textfield',name: 'inputMessage',displayed:false} ).hide();
	var sendBtn = tb.add( {text:'보내기',handler:doChat} ).hide();
	var helpMessage = tb.add({
		xtype: 'displayfield',name: 'displayfield',value: '<span style="color: red;font-weight: bold;" >서버와 연결되어있지 않습니다.</span>'
	});
	
	//여기는 후처리.
	tb.add('->');
	var roles = '${roles}';
	var nickname = '${nickname}';
	var googleEmail = '${googleEmail}';
	if(roles ==''){
		tb.add({text:'로그인하러가기',handler: function(){ window.location = '<%=DefaultController.LOGIN_URL%>'; }});
	}else{
		tb.add('['+googleEmail+'] ' + nickname);
		tb.add('-');
		tb.add({text:'로그아웃',handler: function(){ window.location = '<%=DefaultController.LOGOUT_URL%>'; }});
	}
	
});

</script>

<div id='headerToolbarDiv' ></div>

<div id="dialog" title="서버로부터의 메세지" style="font-size: 70%;" ></div>



