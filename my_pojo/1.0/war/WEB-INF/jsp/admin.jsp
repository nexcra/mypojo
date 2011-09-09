
<%@page import="erwins.webapp.myApp.admin.Career"%>
<%@page import="erwins.webapp.myApp.admin.AdminController"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<script type="text/javascript">
$(function() {
	var refreshBtn =  $("#refreshBtn").button({ label: "캐시 삭제" }).click(function(e){
		$.send('/rest/admin/refresh',null,function(message){
			refreshBtn.button("option", "label", "캐시가 삭제되었습니다");
		},refreshBtn);
	});
});
</script>
<input id="refreshBtn" type="button" >
<a href="/rest/admin/user/download" class="lintButton">사용자정보 다운</a>
<a href="/rest/admin/trx/download" class="lintButton">TRX정보 다운</a>

<div>
<form action="<%=request.getAttribute("trxUpload")%>" method="post" enctype="multipart/form-data">
	TRX
	<input type="file" name="trx">
	<input type="submit" value="Submit">
</form>
</div>
<div>
<form action="<%=request.getAttribute("userUpload")%>" method="post" enctype="multipart/form-data">
	SysUser
	<input type="file" name="sysUser">
	<input type="submit" value="Submit">
</form>
</div>
<div>
<%=Career.career()%>
</div>

