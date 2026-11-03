<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<c:set var="pageTitle" value="学生管理"/>
<c:set var="pageHint" value="维护班级学生名单，新增学生时自动创建登录账号（账号=学号，初始密码 123456）"/>
<c:set var="active" value="students"/>
<%@ include file="../common/header.jsp" %>

<div class="card">
    <div class="card-header">
        <div>
            <h3>学生名单</h3>
            <div class="desc">共 ${page.total} 名学生</div>
        </div>
        <a class="btn btn-primary" href="${ctx}/teacher/students?action=new">➕ 新增学生</a>
    </div>

    <form class="filter-bar" method="get" action="${ctx}/teacher/students">
        <div class="form-group grow">
            <label for="keyword">关键字</label>
            <input type="search" id="keyword" name="keyword" value="<c:out value='${keyword}'/>"
                   placeholder="输入学号或姓名进行搜索">
        </div>
        <div class="form-group">
            <label for="className">班级</label>
            <select id="className" name="className">
                <option value="">全部班级</option>
                <c:forEach items="${classNames}" var="cn">
                    <option value="<c:out value='${cn}'/>" ${className == cn ? 'selected' : ''}>
                        <c:out value="${cn}"/>
                    </option>
                </c:forEach>
            </select>
        </div>
        <button class="btn btn-primary" type="submit">查询</button>
        <a class="btn btn-ghost" href="${ctx}/teacher/students">重置</a>
    </form>

    <c:choose>
        <c:when test="${page.total == 0}">
            <div class="empty">
                <div class="icon">🔍</div>
                <p>没有找到符合条件的学生</p>
                <p class="hint-line">换个关键字，或者清空条件重新查询</p>
            </div>
        </c:when>
        <c:otherwise>
            <div class="table-wrap">
                <table class="data">
                    <thead>
                    <tr>
                        <th style="width:60px;">序号</th>
                        <th>学号</th>
                        <th>姓名</th>
                        <th>性别</th>
                        <th>班级</th>
                        <th class="right" style="width:160px;">操作</th>
                    </tr>
                    </thead>
                    <tbody>
                    <c:forEach items="${page.list}" var="s" varStatus="st">
                        <tr>
                            <td class="muted">${(page.pageNum - 1) * page.pageSize + st.index + 1}</td>
                            <td class="mono"><c:out value="${s.sno}"/></td>
                            <td><c:out value="${s.sname}"/></td>
                            <td>
                                <span class="badge ${s.gender == '男' ? 'badge-info' : 'badge-warn'}">
                                    <c:out value="${s.gender}"/>
                                </span>
                            </td>
                            <td><c:out value="${s.className}"/></td>
                            <td class="right">
                                <div class="row-actions" style="justify-content:flex-end;">
                                    <a class="btn btn-ghost btn-sm" href="${ctx}/teacher/students?action=edit&id=${s.id}">编辑</a>
                                    <form method="post" action="${ctx}/teacher/students"
                                          onsubmit="return confirm('确定删除学生 ${s.sname}（${s.sno}）吗？该学生的所有成绩也会一并删除。');">
                                        <input type="hidden" name="action" value="delete">
                                        <input type="hidden" name="id" value="${s.id}">
                                        <button type="submit" class="btn btn-danger btn-sm">删除</button>
                                    </form>
                                </div>
                            </td>
                        </tr>
                    </c:forEach>
                    </tbody>
                </table>
            </div>

            <div class="pagination">
                <div>第 ${page.pageNum} / ${page.totalPages} 页，共 ${page.total} 条记录</div>
                <div class="pages">
                    <c:url var="firstUrl" value="/teacher/students">
                        <c:param name="keyword" value="${keyword}"/>
                        <c:param name="className" value="${className}"/>
                        <c:param name="page" value="1"/>
                    </c:url>
                    <c:url var="prevUrl" value="/teacher/students">
                        <c:param name="keyword" value="${keyword}"/>
                        <c:param name="className" value="${className}"/>
                        <c:param name="page" value="${page.prevPage}"/>
                    </c:url>
                    <c:url var="nextUrl" value="/teacher/students">
                        <c:param name="keyword" value="${keyword}"/>
                        <c:param name="className" value="${className}"/>
                        <c:param name="page" value="${page.nextPage}"/>
                    </c:url>
                    <c:url var="lastUrl" value="/teacher/students">
                        <c:param name="keyword" value="${keyword}"/>
                        <c:param name="className" value="${className}"/>
                        <c:param name="page" value="${page.totalPages}"/>
                    </c:url>

                    <c:choose>
                        <c:when test="${page.hasPrev}">
                            <a href="${firstUrl}">首页</a>
                            <a href="${prevUrl}">上一页</a>
                        </c:when>
                        <c:otherwise>
                            <span class="disabled">首页</span>
                            <span class="disabled">上一页</span>
                        </c:otherwise>
                    </c:choose>

                    <c:set var="startPage" value="${page.pageNum - 2 < 1 ? 1 : page.pageNum - 2}"/>
                    <c:set var="endPage" value="${startPage + 4 > page.totalPages ? page.totalPages : startPage + 4}"/>
                    <c:forEach begin="${startPage}" end="${endPage}" var="p">
                        <c:url var="pageUrl" value="/teacher/students">
                            <c:param name="keyword" value="${keyword}"/>
                            <c:param name="className" value="${className}"/>
                            <c:param name="page" value="${p}"/>
                        </c:url>
                        <c:choose>
                            <c:when test="${p == page.pageNum}">
                                <span class="current">${p}</span>
                            </c:when>
                            <c:otherwise>
                                <a href="${pageUrl}">${p}</a>
                            </c:otherwise>
                        </c:choose>
                    </c:forEach>

                    <c:choose>
                        <c:when test="${page.hasNext}">
                            <a href="${nextUrl}">下一页</a>
                            <a href="${lastUrl}">末页</a>
                        </c:when>
                        <c:otherwise>
                            <span class="disabled">下一页</span>
                            <span class="disabled">末页</span>
                        </c:otherwise>
                    </c:choose>
                </div>
            </div>
        </c:otherwise>
    </c:choose>
</div>

<%@ include file="../common/footer.jsp" %>
