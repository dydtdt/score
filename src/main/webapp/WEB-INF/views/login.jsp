<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>登录 · 班级学生成绩管理系统</title>
    <link rel="stylesheet" href="${ctx}/css/style.css">
</head>
<body class="login-page">
<div class="login-card">
    <div class="brand">
        <div class="logo">成</div>
        <h1>班级学生成绩管理系统</h1>
        <p>教师录入成绩 · 学生查询成绩 · 平均分与及格率统计</p>
    </div>

    <c:if test="${not empty error}">
        <div class="alert alert-error"><c:out value="${error}"/></div>
    </c:if>

    <form method="post" action="${ctx}/login" autocomplete="off">
        <div class="form-group" style="margin-bottom:14px;">
            <label for="username">账号</label>
            <input type="text" id="username" name="username" value="<c:out value='${username}'/>"
                   placeholder="教师账号 admin，学生账号为学号" required autofocus>
        </div>
        <div class="form-group" style="margin-bottom:20px;">
            <label for="password">密码</label>
            <input type="password" id="password" name="password" placeholder="请输入密码" required>
        </div>
        <button type="submit" class="btn btn-primary" style="width:100%;padding:11px;">登 录</button>
    </form>

    <div class="login-tips">
        <div><b>教师演示账号：</b>admin / teacher123</div>
        <div><b>学生演示账号：</b>210101 / 123456（其余学号密码相同）</div>
        <div>教师可录入成绩并查看统计，学生只能查看自己的成绩。</div>
    </div>

    <div class="login-footer">Java EE 期末项目 · Servlet + JSP + JSTL + JDBC</div>
</div>
</body>
</html>
