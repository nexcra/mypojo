
<%@page import="erwins.util.web.WebUtil"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
if(WebUtil.isMobile(request)){
	//out.print("모바일은 기둘"); 
}
response.sendRedirect("/rest/index");
%>

