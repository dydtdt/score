<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<c:set var="pageTitle" value="成绩录入"/>
<c:set var="pageHint" value="选择课程与班级，为每位学生填写分数；留空表示清空（未录入）该条成绩"/>
<c:set var="active" value="scores"/>
<%@ include file="../common/header.jsp" %>

<c:choose>
    <c:when test="${empty courses}">
        <div class="card">
            <div class="empty">
                <div class="icon">📚</div>
                <p>还没有课程，无法录入成绩</p>
                <p class="hint-line">请先到「课程管理」里新增课程</p>
                <p style="margin-top:14px;">
                    <a class="btn btn-primary" href="${ctx}/teacher/courses?action=new">去新增课程</a>
                </p>
            </div>
        </div>
    </c:when>
    <c:otherwise>

        <div class="stat-grid">
            <div class="stat-card">
                <div class="label">👥 应录人数</div>
                <div class="value">${summary.total}<small>人</small></div>
                <div class="foot">当前筛选范围内的学生</div>
            </div>
            <div class="stat-card">
                <div class="label">✅ 已录入</div>
                <div class="value green">${summary.entered}<small>条</small></div>
                <div class="foot">未录入 ${summary.missing} 条</div>
            </div>
            <div class="stat-card">
                <div class="label">📈 平均分</div>
                <div class="value primary">${summary.avg}</div>
                <div class="foot">仅统计已录入的成绩</div>
            </div>
            <div class="stat-card">
                <div class="label">🎯 及格率</div>
                <div class="value ${summary.passRate >= 60 ? 'green' : 'red'}">
                    ${summary.passRate}<small>%</small>
                </div>
                <div class="foot">及格 ${summary.passed} 人 / 已录 ${summary.entered} 人</div>
            </div>
        </div>

        <div class="card">
            <div class="card-header">
                <div>
                    <h3>成绩录入表</h3>
                    <div class="desc">
                        当前课程：<b><c:out value="${empty course ? '未选择' : course.cname}"/></b>
                        · 班级：<b><c:out value="${empty className ? '全部班级' : className}"/></b>
                    </div>
                </div>
            </div>

            <!-- 筛选：切换课程 / 班级时自动提交 -->
            <form class="filter-bar" method="get" action="${ctx}/teacher/scores">
                <div class="form-group">
                    <label for="courseId">课程</label>
                    <select id="courseId" name="courseId" onchange="this.form.submit()">
                        <c:forEach items="${courses}" var="c">
                            <option value="${c.id}" ${courseId == c.id ? 'selected' : ''}>
                                <c:out value="${c.cname}"/>
                            </option>
                        </c:forEach>
                    </select>
                </div>
                <div class="form-group">
                    <label for="className">班级</label>
                    <select id="className" name="className" onchange="this.form.submit()">
                        <option value="" ${empty className ? 'selected' : ''}>全部班级</option>
                        <c:forEach items="${classNames}" var="cn">
                            <option value="<c:out value='${cn}'/>" ${className == cn ? 'selected' : ''}>
                                <c:out value="${cn}"/>
                            </option>
                        </c:forEach>
                    </select>
                </div>
                <button class="btn btn-ghost" type="submit">查询</button>
                <div class="grow"></div>
                <span class="hint-line">成绩范围 0~100，可填小数（如 87.5）</span>
            </form>

            <form method="post" action="${ctx}/teacher/scores">
                <input type="hidden" name="courseId" value="${courseId}">
                <input type="hidden" name="className" value="<c:out value='${className}'/>">

                <c:choose>
                    <c:when test="${empty rows}">
                        <div class="empty">
                            <div class="icon">👥</div>
                            <p>该筛选条件下没有学生</p>
                            <p class="hint-line">请先在「学生管理」中添加学生</p>
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
                                    <th>班级</th>
                                    <th class="center" style="width:140px;">成绩</th>
                                    <th class="center" style="width:110px;">状态</th>
                                </tr>
                                </thead>
                                <tbody>
                                <c:forEach items="${rows}" var="row" varStatus="st">
                                    <tr>
                                        <td class="muted">${st.index + 1}</td>
                                        <td class="mono"><c:out value="${row.sno}"/></td>
                                        <td><c:out value="${row.sname}"/></td>
                                        <td><c:out value="${row.className}"/></td>
                                        <td class="center">
                                            <input class="score-input" type="text" inputmode="decimal"
                                                   maxlength="5" placeholder="--"
                                                   name="score_${row.studentId}"
                                                   value="<c:out value='${row.scoreText}'/>">
                                        </td>
                                        <td class="center">
                                            <c:choose>
                                                <c:when test="${not row.entered}">
                                                    <span class="badge badge-none">未录入</span>
                                                </c:when>
                                                <c:when test="${row.passed}">
                                                    <span class="badge badge-pass">及格</span>
                                                </c:when>
                                                <c:otherwise>
                                                    <span class="badge badge-fail">不及格</span>
                                                </c:otherwise>
                                            </c:choose>
                                        </td>
                                    </tr>
                                </c:forEach>
                                </tbody>
                            </table>
                        </div>

                        <div class="pagination">
                            <div>共 ${fn:length(rows)} 名学生，填写完成后点击右侧按钮保存</div>
                            <div>
                                <button class="btn btn-primary" type="submit">💾 保存成绩</button>
                            </div>
                        </div>
                    </c:otherwise>
                </c:choose>
            </form>
        </div>
    </c:otherwise>
</c:choose>

<%@ include file="../common/footer.jsp" %>
