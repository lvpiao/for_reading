<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>
</head>
<body>
	<h1>上传失物招领信息</h1>
	<form action="../life/upload_losted" method="post"
		enctype="multipart/form-data">
		token<input type="text" name="token"
			value="20161060137:20190407:20190507" /> 物品数字信息 <input type="text"
			name="numberInfo" value="962464" /> 失主姓名 <input type="text"
			name="ownerName" value="962464" /> 物品描述 <input type="text"
			name="itemDescription" value="962464" /> 拾取地点 <input type="text"
			name="pickUpAddress" value="962464" />
		<table>
			<tr>
				<td>物品图片<input type="file" name="file" accept="image/*" /></td>
			</tr>
			<tr>
				<td colspan="2"><input type="submit" value="提交" /></td>
			</tr>
		</table>
	</form>
</body>
</html>