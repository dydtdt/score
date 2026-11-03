<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<c:set var="pageTitle" value="统计分析"/>
<c:set var="pageHint" value="课程平均分与及格率、班级横向对比、学生综合排名"/>
<c:set var="active" value="stats"/>
<%@ include file="../common/header.jsp" %>

<div class="stat-grid">
    <div class="stat-card">
        <div class="label">📈 总体平均分</div>
        <div class="value primary">${overview.avgScoreText}</div>
        <div class="foot">共 ${overview.scoreCount} 条成绩记录</div>
    </div>
    <div class="stat-card">
        <div class="label">✅ 总体及格率</div>
        <div class="value green">${overview.passRateText}<small>%</small></div>
        <div class="foot">及格 ${overview.passCount} 条 / 不及格 ${overview.failCount} 条</div>
    </div>
    <div class="stat-card">
        <div class="label">👥 学生 / 班级</div>
        <div class="value">${overview.studentCount}<small>人</small></div>
        <div class="foot">共 ${overview.classCount} 个班级</div>
    </div>
    <div class="stat-card">
        <div class="label">📚 课程数量</div>
        <div class="value">${overview.courseCount}<small>门</small></div>
        <div class="foot">平均分与及格率按课程统计</div>
    </div>
</div>

<!-- ==================== 课程统计 ==================== -->
<div class="card">
    <div class="card-header">
        <div>
            <h3>各课程平均分与及格率</h3>
            <div class="desc">及格线 60 分，及格率 = 及格人数 / 已录入人数</div>
        </div>
    </div>
    <c:choose>
        <c:when test="${empty courseStats}">
            <div class="empty"><div class="icon">📊</div><p>暂无课程数据</p></div>
        </c:when>
        <c:otherwise>
            <div class="table-wrap">
                <table class="data">
                    <thead>
                    <tr>
                        <th style="width:60px;">序号</th>
                        <th>课程名称</th>
                        <th class="num">学分</th>
                        <th class="num">已录人数</th>
                        <th class="num">平均分</th>
                        <th class="num">及格 / 不及格</th>
                        <th class="num">最高分</th>
                        <th class="num">最低分</th>
                        <th style="width:190px;">及格率</th>
                    </tr>
                    </thead>
                    <tbody>
                    <c:forEach items="${courseStats}" var="s" varStatus="st">
                        <tr>
                            <td class="muted">${st.index + 1}</td>
                            <td><c:out value="${s.cname}"/></td>
                            <td class="num">${s.creditText}</td>
                            <td class="num">${s.entered}</td>
                            <td class="num"><b class="mono">${s.avgScoreText}</b></td>
                            <td class="num">
                                <span class="badge badge-pass">${s.passCount}</span>
                                /
                                <span class="badge badge-fail">${s.failCount}</span>
                            </td>
                            <td class="num">${s.maxScoreText}</td>
                            <td class="num">${s.minScoreText}</td>
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

<!-- ==================== 班级统计 ==================== -->
<div class="card">
    <div class="card-header">
        <div>
            <h3>班级横向对比</h3>
            <div class="desc">把同一个班所有课程的成绩合并统计</div>
        </div>
    </div>
    <c:choose>
        <c:when test="${empty classStats}">
            <div class="empty"><div class="icon">🏫</div><p>暂无班级数据</p></div>
        </c:when>
        <c:otherwise>
            <div class="table-wrap">
                <table class="data">
                    <thead>
                    <tr>
                        <th>班级</th>
                        <th class="num">学生人数</th>
                        <th class="num">已录人次</th>
                        <th class="num">平均分</th>
                        <th class="num">及格人次</th>
                        <th style="width:190px;">及格率</th>
                    </tr>
                    </thead>
                    <tbody>
                    <c:forEach items="${classStats}" var="s">
                        <tr>
                            <td><c:out value="${s.className}"/></td>
                            <td class="num">${s.studentCount}</td>
                            <td class="num">${s.entered}</td>
                            <td class="num"><b class="mono">${s.avgScoreText}</b></td>
                            <td class="num">${s.passCount}</td>
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

<!-- ==================== 学生排名 ==================== -->
<div class="card">
    <div class="card-header">
        <div>
            <h3>学生综合排名</h3>
            <div class="desc">按已录入课程的<b>平均分</b>从高到低排名，及格率按门次计算</div>
        </div>
    </div>

    <form class="filter-bar" method="get" action="${ctx}/teacher/stats">
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
        <button class="btn btn-ghost" type="submit">筛选</button>
        <div class="grow"></div>
        <span class="hint-line">共 ${fn:length(studentStats)} 名学生</span>
    </form>

    <c:choose>
        <c:when test="${empty studentStats}">
            <div class="empty">
                <div class="icon">🏅</div>
                <p>暂无学生成绩数据</p>
            </div>
        </c:when>
        <c:otherwise>
            <div class="table-wrap">
                <table class="data">
                    <thead>
                    <tr>
                        <th class="center" style="width:80px;">排名</th>
                        <th>学号</th>
                        <th>姓名</th>
                        <th>班级</th>
                        <th class="num">已录 / 课程数</th>
                        <th class="num">平均分</th>
                        <th class="num">及格门次</th>
                        <th style="width:190px;">及格率</th>
                    </tr>
                    </thead>
                    <tbody>
                    <c:forEach items="${studentStats}" var="s">
                        <tr>
                            <td class="center">
                                <c:choose>
                                    <c:when test="${s.rank == 1}"><span class="badge badge-warn">🥇 1</span></c:when>
                                    <c:when test="${s.rank == 2}"><span class="badge badge-info">🥈 2</span></c:when>
                                    <c:when test="${s.rank == 3}"><span class="badge badge-warn">🥉 3</span></c:when>
                                    <c:when test="${s.rank > 0}"><span class="muted mono">${s.rank}</span></c:when>
                                    <c:otherwise><span class="muted">--</span></c:otherwise>
                                </c:choose>
                            </td>
                            <td class="mono"><c:out value="${s.sno}"/></td>
                            <td><c:out value="${s.sname}"/></td>
                            <td><c:out value="${s.className}"/></td>
                            <td class="num">${s.entered} / ${overview.courseCount}</td>
                            <td class="num"><b class="mono">${s.avgScoreText}</b></td>
                            <td class="num">${s.passCount}</td>
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

<%@ include file="../common/footer.jsp" %>
