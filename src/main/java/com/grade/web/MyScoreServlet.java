package com.grade.web;

import com.grade.model.ScoreRow;
import com.grade.model.StudentStat;
import com.grade.model.User;
import com.grade.service.ScoreService;
import com.grade.service.StatService;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * 学生端：查询本人各科成绩、平均分、及格率与班级排名。
 */
public class MyScoreServlet extends BaseServlet {

    private static final long serialVersionUID = 1L;

    private final ScoreService scoreService = new ScoreService();
    private final StatService statService = new StatService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        User user = currentUser(req);
        if (user == null || user.getStudentId() == null) {
            flash(req, "error", "当前账号没有关联学生档案，请使用学生账号登录");
            redirect(req, resp, "/login");
            return;
        }

        long studentId = user.getStudentId().longValue();
        List<ScoreRow> rows = scoreService.rowsForStudent(studentId);
        StudentStat stat = statService.statOfStudent(studentId);

        int entered = 0;
        int passed = 0;
        double sum = 0;
        for (ScoreRow row : rows) {
            if (row.isEntered()) {
                entered++;
                sum += row.getScore().doubleValue();
                if (row.isPassed()) {
                    passed++;
                }
            }
        }

        req.setAttribute("rows", rows);
        req.setAttribute("stat", stat);
        req.setAttribute("enteredCount", Integer.valueOf(entered));
        req.setAttribute("passCount", Integer.valueOf(passed));
        req.setAttribute("totalCount", Integer.valueOf(rows.size()));
        req.setAttribute("avgScore", Double.valueOf(entered == 0 ? 0 : Math.round(sum * 10 / entered) / 10.0));
        req.setAttribute("passRate", Double.valueOf(entered == 0 ? 0 : Math.round(passed * 1000.0 / entered) / 10.0));
        req.setAttribute("active", "my");
        forward(req, resp, "student/my-scores.jsp");
    }
}
