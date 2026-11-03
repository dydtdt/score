<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<c:set var="pageTitle" value="我的成绩"/>
<c:set var="pageHint" value="只能查看本人的成绩，平均分与及格率由系统自动计算"/>
<c:set var="active" value="my"/>
<%@ include file="../common/header.jsp" %>

<div class="card">
    <div class="card-header">
        <div>
            <h3>学生信息</h3>
            <div class="desc">数据由教务系统同步</div>
        </div>
    </div>
    <div class="card-body">
        <div class="form-grid">
            <div class="form-group">
                <label>学号</label>
                <div class="mono"><c:out value="${stat.sno}"/></div>
            </div>
            <div class="form-group">
                <label>姓名</label>
                <div><c:out value="${stat.sname}"/></div>
            </div>
            <div class="form-group">
                <label>班级</label>
                <div><c:out value="${stat.className}"/></div>
            </div>
            <div class="form-group">
                <label>班级排名</label>
                <div>
                    <c:choose>
                        <c:when test="${stat.rank > 0}">
                            <span class="badge badge-info">第 ${stat.rank} 名</span>
                        </c:when>
                        <c:otherwise><span class="muted">暂无排名</span></c:otherwise>
                    </c:choose>
                </div>
            </div>
        </div>
    </div>
</div>

<div class="stat-grid">
    <div class="stat-card">
        <div class="label">📈 平均分</div>
        <div class="value primary">${avgScore}</div>
        <div class="foot">已录入 ${enteredCount} 门课程的平均成绩</div>
    </div>
    <div class="stat-card">
        <div class="label">🎯 及格率</div>
        <div class="value ${passRate >= 60 ? 'green' : 'red'}">
            ${passRate}<small>%</small>
        </div>
        <div class="foot">及格 ${passCount} 门 / 已录 ${enteredCount} 门</div>
    </div>
    <div class="stat-card">
        <div class="label">📚 已录课程</div>
        <div class="value">${enteredCount}<small>/ ${totalCount}</small></div>
        <div class="foot">共 ${totalCount} 门课程，${totalCount - enteredCount} 门尚未录入</div>
    </div>
</div>

<div class="card">
    <div class="card-header">
        <div>
            <h3>各科成绩</h3>
            <div class="desc">60 分及以上为及格</div>
        </div>
    </div>
    <c:choose>
        <c:when test="${empty rows}">
            <div class="empty">
                <div class="icon">📭</div>
                <p>还没有课程数据</p>
            </div>
        </c:when>
        <c:otherwise>
            <div class="card-body">
                <div class="grade-list">
                    <c:forEach items="${rows}" var="row">
                        <div class="grade-item">
                            <div>
                                <div class="cname"><c:out value="${row.cname}"/></div>
                                <div class="meta">
                                    学分 ${row.creditText}
                                    <c:if test="${row.entered}">
                                        ·
                                        <c:choose>
                                            <c:when test="${row.passed}">
                                                <span class="badge badge-pass">及格</span>
                                            </c:when>
                                            <c:otherwise>
                                                <span class="badge badge-fail">不及格</span>
                                            </c:otherwise>
                                        </c:choose>
                                    </c:if>
                                </div>
                            </div>
                            <c:choose>
                                <c:when test="${row.entered}">
                                    <div class="score ${row.passed ? '' : 'fail'}">${row.scoreText}</div>
                                </c:when>
                                <c:otherwise>
                                    <div class="score none">未录入</div>
                                </c:otherwise>
                            </c:choose>
                        </div>
                    </c:forEach>
                </div>
            </div>

            <div class="table-wrap">
                <table class="data">
                    <thead>
                    <tr>
                        <th style="width:60px;">序号</th>
                        <th>课程名称</th>
                        <th class="num">学分</th>
                        <th class="num">成绩</th>
                        <th class="center">是否及格</th>
                    </tr>
                    </thead>
                    <tbody>
                    <c:forEach items="${rows}" var="row" varStatus="st">
                        <tr>
                            <td class="muted">${st.index + 1}</td>
                            <td><c:out value="${row.cname}"/></td>
                            <td class="num">${row.creditText}</td>
                            <td class="num">
                                <c:choose>
                                    <c:when test="${row.entered}">
                                        <b class="score-val ${row.passed ? '' : 'fail'}">${row.scoreText}</b>
                                    </c:when>
                                    <c:otherwise><span class="badge badge-none">未录入</span></c:otherwise>
                                </c:choose>
                            </td>
                            <td class="center">
                                <c:choose>
                                    <c:when test="${not row.entered}"><span class="muted">--</span></c:when>
                                    <c:when test="${row.passed}"><span class="badge badge-pass">及格</span></c:when>
                                    <c:otherwise><span class="badge badge-fail">不及格</span></c:otherwise>
                                </c:choose>
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
