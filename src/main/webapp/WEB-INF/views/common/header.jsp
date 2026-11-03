<%-- 公共布局头部（静态包含：<%@ include file="../common/header.jsp" %>）
     注意：被静态包含的文件不会继承主页面 page 指令里的编码，这里必须自己声明 pageEncoding，
     否则 Jasper 会按 ISO-8859-1 解码本文件，导致侧边栏、标题等中文字面量变成乱码。
     使用前请在页面里先设置 pageTitle、pageHint、active 三个变量。 --%>
<%@ page pageEncoding="UTF-8" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title><c:out value="${pageTitle}"/> · 班级学生成绩管理系统</title>
    <link rel="stylesheet" href="${ctx}/css/style.css">
</head>
<body>
<div class="layout">
    <aside class="sidebar">
        <div class="brand">
            <div class="logo">成</div>
            <div>
                <div class="title">班级成绩管理</div>
                <div class="subtitle">Java EE 期末项目</div>
            </div>
        </div>

        <div class="nav-label">功能菜单</div>
        <c:choose>
            <c:when test="${sessionScope.loginUser.teacher}">
                <a class="nav-item ${active == 'dashboard' ? 'active' : ''}" href="${ctx}/teacher/dashboard">
                    <span class="ico">🏠</span>工作台
                </a>
                <a class="nav-item ${active == 'students' ? 'active' : ''}" href="${ctx}/teacher/students">
                    <span class="ico">👨‍🎓</span>学生管理
                </a>
                <a class="nav-item ${active == 'courses' ? 'active' : ''}" href="${ctx}/teacher/courses">
                    <span class="ico">📚</span>课程管理
                </a>
                <a class="nav-item ${active == 'scores' ? 'active' : ''}" href="${ctx}/teacher/scores">
                    <span class="ico">✍️</span>成绩录入
                </a>
                <a class="nav-item ${active == 'stats' ? 'active' : ''}" href="${ctx}/teacher/stats">
                    <span class="ico">📊</span>统计分析
                </a>
            </c:when>
            <c:otherwise>
                <a class="nav-item ${active == 'my' ? 'active' : ''}" href="${ctx}/student/scores">
                    <span class="ico">📄</span>我的成绩
                </a>
            </c:otherwise>
        </c:choose>

        <div class="spacer"></div>
        <div class="me">
            <c:out value="${sessionScope.loginUser.displayName}"/>
            <c:choose>
                <c:when test="${sessionScope.loginUser.teacher}">（教师）</c:when>
                <c:otherwise>（学生）</c:otherwise>
            </c:choose>
        </div>
    </aside>

    <div class="main">
        <header class="topbar">
            <div>
                <h2><c:out value="${pageTitle}"/></h2>
                <c:if test="${not empty pageHint}">
                    <div class="hint"><c:out value="${pageHint}"/></div>
                </c:if>
            </div>
            <div class="right">
                <span class="user-chip">
                    <span class="avatar"><c:out value="${fn:substring(sessionScope.loginUser.displayName, 0, 1)}"/></span>
                    <c:out value="${sessionScope.loginUser.displayName}"/>
                </span>
                <a class="btn btn-ghost btn-sm" href="${ctx}/logout">退出登录</a>
            </div>
        </header>

        <div class="content">
            <c:if test="${not empty sessionScope.flashMsg}">
                <div class="alert alert-${empty sessionScope.flashType ? 'info' : sessionScope.flashType}">
                    <c:out value="${sessionScope.flashMsg}"/>
                </div>
                <c:remove var="flashMsg" scope="session"/>
                <c:remove var="flashType" scope="session"/>
            </c:if>
