<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
     <%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<jsp:include page="/WEB-INF/views/common/header.jsp"></jsp:include>

<style>
	*{
		margin: 0;
    	padding: 0;
    	outline: none;
	}
	tbody{
		margin: 0;
	}
	.content-wrap{
		background-color : #010101;
		width : 900px;
		height : 300px;
	}
	.menubar{
		width: 400px;
	}
</style>
<table>
<td>
<div class=".menubar">
<jsp:include page="/WEB-INF/views/common/menubar.jsp"></jsp:include>
</div>
</td>
<td>
<div class="content-wrap">
	<font color="white">왜안돼</font>
</div>
</td>
</table>
<jsp:include page="/WEB-INF/views/common/footer.jsp"></jsp:include>

