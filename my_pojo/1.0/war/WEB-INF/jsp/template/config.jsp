<%@page import="erwins.util.web.WebUtil"%>
<%@page import="erwins.webapp.myApp.SystemInfo"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<script type="text/javascript">
$(function() {
	$(".lintButton" ).button();
});
/**
 * 다른 스크립트나 Flex등에서 사용된다.
 */
function isServer(){
	return <%=SystemInfo.isServer()%>;	
}
var requestParameter = <%= WebUtil.requestedJSON(request)%> ;
</script>