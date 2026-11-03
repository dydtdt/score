<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<c:set var="pageTitle" value="课程管理"/>
<c:set var="pageHint" value="维护课程信息；已录入成绩的课程需要先清空成绩才能删除"/>
<c:set var="active" value="courses"/>
<%@ include file="../common/header.jsp" %>

<div class="card">
    <div class="card-header">
        <div>
            <h3>课程列表</h3>
            <div class="desc">共 ${fn:length(courses)} 门课程</div>
        </div>
        <a class="btn btn-primary" href="${ctx}/teacher/courses?action=new">➕ 新增课程</a>
    </div>

    <c:choose>
        <c:when test="${empty courses}">
            <div class="empty">
                <div class="icon">📚</div>
                <p>还没有课程</p>
                <p class="hint-line">点击右上角「新增课程」添加一门课程</p>
            </div>
        </c:when>
        <c:otherwise>
            <div class="table-wrap">
                <table class="data">
                    <thead>
                    <tr>
                        <th style="width:60px;">序号</th>
                        <th>课程名称</th>
                        <th class="num">学分</th>
                        <th class="num">已录成绩</th>
                        <th class="right" style="width:230px;">操作</th>
                    </tr>
                    </thead>
                    <tbody>
                    <c:forEach items="${courses}" var="c" varStatus="st">
                        <tr>
                            <td class="muted">${st.index + 1}</td>
                            <td><c:out value="${c.cname}"/></td>
                            <td class="num">${c.creditText}</td>
                            <td class="num">
                                <span class="badge ${c.enteredCount > 0 ? 'badge-pass' : 'badge-none'}">${c.enteredCount} 条</span>
                            </td>
                            <td class="right">
                                <div class="row-actions" style="justify-content:flex-end;">
                                    <a class="btn btn-ghost btn-sm" href="${ctx}/teacher/scores?courseId=${c.id}">录入成绩</a>
                                    <a class="btn btn-ghost btn-sm" href="${ctx}/teacher/courses?action=edit&id=${c.id}">编辑</a>
                                    <form method="post" action="${ctx}/teacher/courses"
                                          onsubmit="return confirm('确定删除课程《${c.cname}》吗？');">
                                        <input type="hidden" name="action" value="delete">
                                        <input type="hidden" name="id" value="${c.id}">
                                        <button type="submit" class="btn btn-danger btn-sm">删除</button>
                                    </form>
                                </div>
                            </td>
                        </tr>
                    </c:forEach>
                    </tbody>
                </table>
            </div>
        </c:otherwise>
    </c:choose>
</div>

<%@ include file="../common/footer.jsp" %>
