<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>
</head>
<body>

	<h1>用于上传各类营业时间汇总的图片</h1>
	<form action="../life/upload_general" method="post"
		enctype="multipart/form-data">
		token<input type="text" name="token"
			value="20161060137:20190407:20190507" />
		<table>
			<tr>
				<td>图片<input type="file" name="file" accept="image/*" /></td>
			</tr>
			<tr>
				<td colspan="2"><input type="submit" value="提交" /></td>
			</tr>
		</table>
	</form>
</body>
</html>