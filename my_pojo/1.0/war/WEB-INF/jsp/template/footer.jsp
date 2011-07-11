<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="erwins.webapp.myApp.user.SessionInfo"%>
<%@page import="erwins.webapp.myApp.Current"%>
<%
	SessionInfo info =  Current.getInfo();
%>


<div>
	<input id="chatConnect" type="button">
	<input id="chatMessage" type="text" >
	<input id="chatSend" type="button">
	<span id='serverInfo' style="color: red;font-weight: bold;" >서버와 연결되어있지 않습니다.</span>
</div>

<div id="dialog" title="서버로부터의 메세지" style="font-size: 70%;" >
	
</div>

<script type="text/javascript">
$(function() {
	var login = <%=info.isLogin()%> ;
	var dialog = $( "#dialog" ).dialog({ autoOpen: false });
	var chatSend =  $("#chatSend").button({label: "보내기"}).button( "option", "disabled", true );
	var chatConnect =  $("#chatConnect").button({label: "채팅서버접속하기"}).click(function(e){
		$.send('/rest/none/channel/create',null,function(token){
			GoogleAppEngine.init = function(){
				$('#serverInfo').text('서버로 연결 중입니다..');	
			}
			GoogleAppEngine.onopen = function(){
				$('#serverInfo').attr({style:'color: blue;font-weight: bold;'}).text('서버에 정상적으로 접속되었습니다.');
			}
			GoogleAppEngine.onerror = function(){
				$.send('/rest/none/channel/remove',{key:token},function(message){
					$('#serverInfo').text('푸시서비스에 오류. 재연결 필요시 F5를 눌러주세요');
				});
			}
			GoogleAppEngine.start(token,function(message){
				dialog.append("<p>"+message.message+"</p>").dialog('open');
			});
			chatConnect.button("option", "label", "접속됨").button( "option", "disabled", true );
			chatSend.button( "option", "disabled", false );
		},chatConnect);
	});
	var messageInput = $("#chatMessage");
	$.bindEnter(chatSend,messageInput,function(){
		$.send('/rest/none/channel/chat',{message:messageInput.attr("value")},function(message){
			messageInput.attr("value","");
			$('#serverInfo').text('[' + message + '] : 명에게 메세지 전송');
		},chatSend);
	});
	/*
	var messageInput = $("#chatMessage");
	var chatSend =  $("#chatSend").button({label: "보내기"}).click(function(e){
		$.send('none/channel/chat',{message:messageInput.attr("value")},function(message){
			messageInput.attr("value","");
		},chatSend);
	});
	*/
});
</script>

