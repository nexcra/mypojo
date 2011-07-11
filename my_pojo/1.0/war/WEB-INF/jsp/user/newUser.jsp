
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<script type="text/javascript">
$(function() {
	var saveBtn =  $("#saveBtn").button({ label: "저장" }).click(function(e){
		$.send('/rest/none/editNickname',{nickName:$("#nickName").attr("value")},function(message){
			location.href = "/rest/index";
		},saveBtn,"처리완료. 기다리세요");
	});	
});
</script>

<h4>구글 계정으로 로그인 되었으나 처음 접속하시는 분입니다. 사용하실 닉네임을 입력해 주세요.</h4> 
<div>
	닉네임 :  <input type="text" id="nickName" > <input type="button" id="saveBtn" >
</div>

