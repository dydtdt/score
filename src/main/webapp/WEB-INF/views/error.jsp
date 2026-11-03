<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" isErrorPage="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<c:set var="statusCode" value="${requestScope['javax.servlet.error.status_code']}"/>
<c:set var="errorMessage" value="${requestScope['javax.servlet.error.message']}"/>
<c:set var="exceptionObj" value="${requestScope['javax.servlet.error.exception']}"/>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>出错了 · 班级学生成绩管理系统</title>
    <link rel="stylesheet" href="${ctx}/css/style.css">
</head>
<body class="login-page">
<div class="login-card" style="max-width:560px;">
    <div class="brand">
        <div class="logo">!</div>
        <h1>页面出错了</h1>
        <p>
            <c:choose>
                <c:when test="${not empty statusCode}">HTTP ${statusCode}</c:when>
                <c:otherwise>请求处理失败</c:otherwise>
            </c:choose>
        </p>
    </div>

    <div class="alert alert-error">
        <c:choose>
            <c:when test="${not empty errorMessage}">
                <c:out value="${errorMessage}"/>
            </c:when>
            <c:otherwise>系统处理你的请求时遇到了问题，请稍后重试。</c:otherwise>
        </c:choose>
    </div>

    <c:if test="${not empty exceptionObj}">
        <div class="login-tips" style="background:#fef2f2;color:#991b1b;word-break:break-all;">
            <b>异常类型：</b><c:out value="${exceptionObj['class'].name}"/><br>
            <b>异常信息：</b><c:out value="${exceptionObj.message}"/>
        </div>
    </c:if>

    <div style="display:flex;gap:10px;margin-top:20px;">
        <a class="btn btn-primary" style="flex:1;" href="${ctx}/login">返回登录</a>
        <a class="btn btn-ghost" style="flex:1;" href="${ctx}/">返回首页</a>
    </div>
</div>
</body>
</html>
