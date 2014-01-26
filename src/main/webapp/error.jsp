<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>

<%
	String conPath = request.getContextPath();
%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html;charset=utf-8">
<title>后台管理</title>
<%-- 设置标题栏图标  --%>
<link rel="icon" href="<%=conPath %>/images/favicon.ico" type="image/x-icon"/>
<link rel="shortcut icon" href="<%=conPath %>/images/favicon.ico" type="image/x-icon"/>
<link rel="stylesheet" type="text/css" href="<%=conPath%>/images/login.css" />
</head>
<body>
<div class="wrapper">
	<%-- 页面主体容器 --%>
	<div id="bd">
		<div style="height:100%;margin: auto; color: red">
			<%=request.getAttribute("errMsg") %>
		</div>
	<%-- 清除浮动 --%>
		<div class="clearfloat"></div>
	</div>
	
	<div id="ft">
		<p>版权所有：安龙有限公司  粤ICP备917992753号</p>
	</div>
</div>
</body>
</html>
