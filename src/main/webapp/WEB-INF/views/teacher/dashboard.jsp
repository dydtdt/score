<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<c:set var="pageTitle" value="工作台"/>
<c:set var="pageHint" value="成绩录入情况与班级、课程的整体统计"/>
<c:set var="active" value="dashboard"/>
<%@ include file="../common/header.jsp" %>

<div class="stat-grid">
    <div class="stat-card">
        <div class="label">👨‍🎓 学生总数</div>
        <div class="value">${overview.studentCount}<small>人</small></div>
        <div class="foot">分布在 ${overview.classCount} 个班级</div>
    </div>
    <div class="stat-card">
        <div class="label">📚 课程数量</div>
        <div class="value">${overview.courseCount}<small>门</small></div>
        <div class="foot">可在「课程管理」中维护</div>
    </div>
    <div class="stat-card">
        <div class="label">✍️ 成绩记录</div>
        <div class="value">${overview.scoreCount}<small>条</small></div>
        <div class="foot">及格 ${overview.passCount} 条 / 不及格 ${overview.failCount} 条</div>
    </div>
    <div class="stat-card">
        <div class="label">📈 总体平均分</div>
        <div class="value primary">${overview.avgScoreText}</div>
        <div class="foot">全部已录入成绩的平均值</div>
    </div>
    <div class="stat-card">
        <div class="label">✅ 总体及格率</div>
        <div class="value green">${overview.passRateText}<small>%</small></div>
        <div class="foot">60 分及以上视为及格</div>
    </div>
</div>

<div class="two-col">
    <div class="card mt-0">
        <div class="card-header">
            <div>
                <h3>班级成绩概览</h3>
                <div class="desc">按班级统计的平均分与及格率</div>
            </div>
            <a class="btn btn-ghost btn-sm" href="${ctx}/teacher/stats">查看详细统计</a>
        </div>
        <c:choose>
            <c:when test="${empty classStats}">
                <div class="empty">
                    <div class="icon">📭</div>
                    <p>还没有学生数据</p>
                    <p class="hint-line">先去「学生管理」中添加学生吧</p>
                </div>
            </c:when>
            <c:otherwise>
                <div class="table-wrap">
                    <table class="data">
                        <thead>
                        <tr>
                            <th>班级</th>
                            <th class="num">人数</th>
                            <th class="num">已录/应录</th>
                            <th class="num">平均分</th>
                            <th>及格率</th>
                        </tr>
                        </thead>
                        <tbody>
                        <c:forEach items="${classStats}" var="s">
                            <tr>
                                <td><c:out value="${s.className}"/></td>
                                <td class="num">${s.studentCount}</td>
                                <td class="num">${s.entered} / ${s.studentCount * overview.courseCount}</td>
                                <td class="num">${s.avgScoreText}</td>
                                <td>
                                    <div class="rate-cell">
                                        <div class="bar ${s.passRate < 60 ? 'low' : ''}">
                                            <span style="width:${s.passRate}%"></span>
                                        </div>
                                        <span class="rate-text">${s.passRateText}%</span>
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

    <div class="card mt-0">
        <div class="card-header">
            <div>
                <h3>课程成绩概览</h3>
                <div class="desc">每门课的平均分、及格率与最高最低分</div>
            </div>
            <a class="btn btn-ghost btn-sm" href="${ctx}/teacher/scores">去录入成绩</a>
        </div>
        <c:choose>
            <c:when test="${empty courseStats}">
                <div class="empty">
                    <div class="icon">📚</div>
                    <p>还没有课程数据</p>
                </div>
            </c:when>
            <c:otherwise>
                <div class="table-wrap">
                    <table class="data">
                        <thead>
                        <tr>
                            <th>课程</th>
                            <th class="num">已录</th>
                            <th class="num">平均分</th>
                            <th class="num">最高/最低</th>
                            <th>及格率</th>
                        </tr>
                        </thead>
                        <tbody>
                        <c:forEach items="${courseStats}" var="s">
                            <tr>
                                <td><c:out value="${s.cname}"/></td>
                                <td class="num">${s.entered}</td>
                                <td class="num">${s.avgScoreText}</td>
                                <td class="num">
                                    ${s.maxScoreText}
                                    /
                                    ${s.minScoreText}
                                </td>
                                <td>
                                    <div class="rate-cell">
                                        <div class="bar ${s.passRate < 60 ? 'low' : ''}">
                                            <span style="width:${s.passRate}%"></span>
                                        </div>
                                        <span class="rate-text">${s.passRateText}%</span>
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
</div>

<div class="card">
    <div class="card-header">
        <div>
            <h3>快捷操作</h3>
            <div class="desc">常用入口</div>
        </div>
    </div>
    <div class="card-body">
        <a class="btn btn-primary" href="${ctx}/teacher/scores">✍️ 录入成绩</a>
        <a class="btn btn-ghost" href="${ctx}/teacher/students?action=new">➕ 新增学生</a>
        <a class="btn btn-ghost" href="${ctx}/teacher/courses">📚 课程管理</a>
        <a class="btn btn-ghost" href="${ctx}/teacher/stats">📊 统计分析</a>
    </div>
</div>

<%@ include file="../common/footer.jsp" %>
