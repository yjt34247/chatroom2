<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>聊天室登录</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
<div class="login-container">
    <h2>欢迎来到聊天室</h2>
    <form id="loginForm" method="POST" action="${pageContext.request.contextPath}/login">
        <input type="text" id="username" name="username" placeholder="请输入用户名" required maxlength="20">
        <button type="submit">进入聊天室</button>
    </form>

    <div id="errorMsg">
        ${param.error == 'empty' ? '<p class="error">用户名不能为空</p>' : ''}
        ${param.error == 'exists' ? '<p class="error">用户名已存在，请换一个</p>' : ''}
        ${param.error == 'system' ? '<p class="error">系统错误，请重试</p>' : ''}
    </div>
</div>
</body>
</html>