<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<c:set var="pageTitle" value="${isNew ? '新增学生' : '编辑学生'}"/>
<c:set var="pageHint" value="学号必须是 4~20 位数字，且不能与已有学生重复"/>
<c:set var="active" value="students"/>
<%@ include file="../common/header.jsp" %>

<c:if test="${not empty error}">
    <div class="alert alert-error"><c:out value="${error}"/></div>
</c:if>

<div class="card" style="max-width:760px;">
    <div class="card-header">
        <div>
            <h3>${isNew ? '新增学生信息' : '修改学生信息'}</h3>
            <div class="desc">
                <c:choose>
                    <c:when test="${isNew}">保存后会同时创建一个登录账号，账号为学号，初始密码 123456</c:when>
                    <c:otherwise>修改学号或姓名时，对应的登录账号信息会同步更新</c:otherwise>
                </c:choose>
            </div>
        </div>
    </div>
    <div class="card-body">
        <form method="post" action="${ctx}/teacher/students">
            <input type="hidden" name="action" value="save">
            <c:if test="${not isNew}">
                <input type="hidden" name="id" value="${student.id}">
            </c:if>

            <div class="form-grid">
                <div class="form-group">
                    <label for="sno">学号<span class="req">*</span></label>
                    <input type="text" id="sno" name="sno" value="<c:out value='${student.sno}'/>"
                           placeholder="例如 210109" maxlength="20" required>
                    <span class="help">4~20 位数字，同时也作为学生登录账号</span>
                </div>
                <div class="form-group">
                    <label for="sname">姓名<span class="req">*</span></label>
                    <input type="text" id="sname" name="sname" value="<c:out value='${student.sname}'/>"
                           placeholder="例如 张三" maxlength="20" required>
                </div>
                <div class="form-group">
                    <label for="gender">性别<span class="req">*</span></label>
                    <select id="gender" name="gender" required>
                        <option value="男" ${student.gender == '男' ? 'selected' : ''}>男</option>
                        <option value="女" ${student.gender == '女' ? 'selected' : ''}>女</option>
                    </select>
                </div>
                <div class="form-group">
                    <label for="className">班级<span class="req">*</span></label>
                    <input type="text" id="className" name="className" list="classList"
                           value="<c:out value='${student.className}'/>"
                           placeholder="例如 计算机2101" maxlength="40" required>
                    <datalist id="classList">
                        <c:forEach items="${classNames}" var="cn">
                            <option value="<c:out value='${cn}'/>"></option>
                        </c:forEach>
                    </datalist>
                    <span class="help">可以直接输入新班级名称</span>
                </div>
            </div>

            <div class="form-actions">
                <button type="submit" class="btn btn-primary">保存</button>
                <a class="btn btn-ghost" href="${ctx}/teacher/students">返回列表</a>
            </div>
        </form>
    </div>
</div>

<%@ include file="../common/footer.jsp" %>
