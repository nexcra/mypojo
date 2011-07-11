
<%@page import="erwins.webapp.myApp.Current"%>
<%@page import="erwins.webapp.myApp.DefaultController"%>
<%@page import="erwins.webapp.myApp.user.GoogleUser"%>
<%@page import="erwins.webapp.myApp.user.SessionInfo"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%
	SessionInfo info =  Current.getInfo();
	GoogleUser user =  info.getUser();
%>
<% if(info.isLogin()){ %>
	[<%=user.getGoogleEmail()%>] <%=user.getNickname() %> 님께서 로그인 하셨습니다.
	<a class="lintButton" href="/rest/admin/page">관리자 페이지(관리자만 가능)</a>
<%}else{ %>
	<h4>이 사이트의 일부 메뉴는 구글 계정이 있어야만 사용할 수 있습니다.</h4>
	<p>(현재 사이트에서는 절대로 ID나 비밀번호늘 물어보지 않습니다.)</p>
	<h4>브라우저내의 아무 탭에서나 구글에 로그인되있으면 사용가능합니다. 그게 아니라면 구글로 이동해서 로그인하셔야 합니다 .</h4>
	<p><a class="lintButton" href="<%=DefaultController.LOGIN_URL%>">로그인하러가기</a></p>
<%}%>