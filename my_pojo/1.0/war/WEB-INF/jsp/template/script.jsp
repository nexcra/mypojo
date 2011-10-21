<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="erwins.webapp.myApp.SystemInfo"%>	
<style>
html, body {
	overflow: hidden;	/* Remove scroll bars on browser window */	
    font-size: 100%;
}
</style>

<!-- Google Cahnnel API  -->
<script type="text/javascript" src='/_ah/channel/jsapi<%= SystemInfo.isServer() ?  "" : "?key=dev" %>'></script>

<!-- jQuery + UI  -->
<script type="text/javascript" src="http://code.jquery.com/jquery-1.4.4.min.js"></script>
<script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/jqueryui/1.8.5/jquery-ui.min.js"></script>
<link type="text/css" href="/js/jQueryUI/css/ui-lightness/jquery-ui-1.8.5.custom.css" rel="stylesheet" />

<!-- EXT-JS  -->
<script src="/js/extjs/ext-all.js" type="text/javascript"></script>
<link rel="stylesheet" type="text/css" href="/js/extjs/resources/css/ext-all.css" />

<script src="/js/extjs/custom/example.js" type="text/javascript"></script>
<link rel="stylesheet" type="text/css" href="/js/extjs/custom/example.css" />

<!-- JSON 유틸 (구글 푸시서버 사용)  -->
<script src="/js/douglascrockford-JSON-js-633fe5a/json_parse.js" type="text/javascript"></script>
<script src="/js/douglascrockford-JSON-js-633fe5a/json2.js" type="text/javascript"></script>

<!-- 그리드~ -->
<script src="/js/jquery.jqGrid-3.8.2/js/jquery.jqGrid.min.js" type="text/javascript"></script>
<script src="/js/jquery.jqGrid-3.8.2/src/jquery.fmatter.js" type="text/javascript"></script>
<link rel="stylesheet" type="text/css" media="screen" href="/js/jquery.jqGrid-3.8.2/css/ui.jqgrid.css" />
<link rel="stylesheet" type="text/css" media="screen" href="/js/jquery.jqGrid-3.8.2/src/css/ui.multiselect.css" />

<!-- Flex -->
<script src="/Flex/AC_OETags.js" type="text/javascript" ></script>
<script src="/js/erwins/flex.js" type="text/javascript" ></script>

<!-- 내가만든거 + 오픈소스 -->
<script src="/js/erwins/Base64.js" type="text/javascript" ></script>
<script src="/js/erwins/AppEngine.js" type="text/javascript" ></script>
<script src="/js/erwins/jQueryEx.js" type="text/javascript" ></script>
<script src="/js/erwins/Tool.js" type="text/javascript" ></script>
<script src="/js/erwins/PrototypeEx.js" type="text/javascript" ></script>
<link rel="stylesheet" type="text/css" href="/js/erwins/defaultCss.css" />
