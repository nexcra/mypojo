<%@page import="erwins.webapp.myApp.user.GoogleUser"%>
<%@page import="erwins.webapp.myApp.user.SessionInfo"%>
<%@page import="erwins.webapp.myApp.Current"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="erwins.webapp.myApp.DefaultController"%>

<div class="header"><a href="/" style="font-weight: bold;font-size: xx-large;">영감님의 테스트서버</a>
<a class="lintButton" href="/rest/translator/page">간단변환기</a>
<a class="lintButton" href="/rest/mapLabel/page">맵라벨</a>
<a class="lintButton" href="/rest/user/page">사용자</a>
<%
	SessionInfo info =  Current.getInfo();
	GoogleUser user =  info.getUser();
%>
<% if(info.isLogin()){ %>
	<a class="lintButton" href="<%=DefaultController.LOGOUT_URL%>">로그아웃</a>
	<%if(user!=null){%>
		 [<%=user.getGoogleEmail() %>] <%=user.getNickname() %> 
	<%}%>
<%}%>

</div> 


