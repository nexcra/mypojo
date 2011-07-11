<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">

<%@page import="erwins.webapp.myApp.Current"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>

<html>
<head>
	<link rel="shortcut icon" href="/favicon.ico" ><link> <!-- 쿠키로 캐싱된다. -->
	<meta http-equiv="Content-Type" content="text/html;charset=UTF-8"></meta>
	<meta name="google-site-verification" content="w5YK7DxEZSNBtPeYnc1zWh3lNEpxI0uOIC8yAmQtWPA" />
	<title><%=Current.menuName() %></title>
	<tiles:insertAttribute name="script" />
	<tiles:insertAttribute name="config" />
</head>
<body>

<tiles:insertAttribute name="header" />
<tiles:insertAttribute name="body" />
<tiles:insertAttribute name="footer" />

</body>
</html>