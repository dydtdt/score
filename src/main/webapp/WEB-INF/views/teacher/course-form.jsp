<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<c:set var="pageTitle" value="${isNew ? '新增课程' : '编辑课程'}"/>
<c:set var="pageHint" value="课程名称不能与已有课程重复"/>
<c:set var="active" value="courses"/>
<%@ include file="../common/header.jsp" %>

<c:if test="${not empty error}">
    <div class="alert alert-error"><c:out value="${error}"/></div>
</c:if>

<div class="card" style="max-width:640px;">
    <div class="card-header">
        <div>
            <h3>${isNew ? '新增课程信息' : '修改课程信息'}</h3>
        </div>
    </div>
    <div class="card-body">
        <form method="post" action="${ctx}/teacher/courses">
            <input type="hidden" name="action" value="save">
            <c:if test="${not isNew}">
                <input type="hidden" name="id" value="${course.id}">
            </c:if>

            <div class="form-grid">
                <div class="form-group">
                    <label for="cname">课程名称<span class="req">*</span></label>
                    <input type="text" id="cname" name="cname" value="<c:out value='${course.cname}'/>"
                           placeholder="例如 Java程序设计" maxlength="30" required>
                </div>
                <div class="form-group">
                    <label for="credit">学分<span class="req">*</span></label>
                    <input type="number" id="credit" name="credit" step="0.5" min="0" max="20"
                           value="<c:out value='${course.credit}'/>" required>
                    <span class="help">0~20 之间，可以填 0.5 的倍数</span>
                </div>
            </div>

            <div class="form-actions">
                <button type="submit" class="btn btn-primary">保存</button>
                <a class="btn btn-ghost" href="${ctx}/teacher/courses">返回列表</a>
            </div>
        </form>
    </div>
</div>

<%@ include file="../common/footer.jsp" %>
