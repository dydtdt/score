package com.grade.web;

import com.grade.model.Course;
import com.grade.model.ScoreRow;
import com.grade.service.CourseService;
import com.grade.service.ScoreService;
import com.grade.service.StudentService;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 教师后台：成绩录入（按「课程 + 班级」筛选学生，一次提交整班成绩）。
 */
public class ScoreServlet extends BaseServlet {

    private static final long serialVersionUID = 1L;

    private static final Logger LOGGER = Logger.getLogger(ScoreServlet.class.getName());

    private final ScoreService scoreService = new ScoreService();
    private final CourseService courseService = new CourseService();
    private final StudentService studentService = new StudentService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        List<Course> courses = courseService.findAll();
        long courseId = longParam(req, "courseId", courses.isEmpty() ? -1 : courses.get(0).getId().longValue());
        String className = trimParam(req, "className");

        Course course = courseId > 0 ? courseService.findById(courseId) : null;
        List<ScoreRow> rows = course == null ? java.util.Collections.<ScoreRow>emptyList()
                : scoreService.rowsForCourse(courseId, className);

        req.setAttribute("courses", courses);
        req.setAttribute("classNames", studentService.listClassNames());
        req.setAttribute("courseId", Long.valueOf(courseId));
        req.setAttribute("course", course);
        req.setAttribute("className", className);
        req.setAttribute("rows", rows);
        req.setAttribute("summary", summarize(rows));
        req.setAttribute("active", "scores");
        forward(req, resp, "teacher/score-entry.jsp");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        long courseId = longParam(req, "courseId", -1);
        String className = trimParam(req, "className");

        if (courseId <= 0) {
            flash(req, "error", "请先选择要录入成绩的课程");
            redirect(req, resp, "/teacher/scores");
            return;
        }

        // 页面上的输入框形如 score_12 => 学生 id 为 12 的成绩
        Map<Long, String> inputs = new LinkedHashMap<Long, String>();
        java.util.Enumeration<String> names = req.getParameterNames();
        while (names.hasMoreElements()) {
            String name = names.nextElement();
            if (name.startsWith("score_")) {
                try {
                    long studentId = Long.parseLong(name.substring("score_".length()));
                    inputs.put(Long.valueOf(studentId), req.getParameter(name));
                } catch (NumberFormatException e) {
                    LOGGER.log(Level.FINE, "忽略无法解析的成绩参数：" + name);
                }
            }
        }

        String message = scoreService.saveBatch(courseId, inputs);
        flash(req, "success", message);
        redirect(req, resp, "/teacher/scores?courseId=" + courseId + "&className=" + encode(className));
    }

    /** 统计当前筛选结果：已录入 / 平均分 / 及格率，显示在录入页顶部 */
    private Map<String, Object> summarize(List<ScoreRow> rows) {
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
        Map<String, Object> summary = new LinkedHashMap<String, Object>();
        summary.put("total", Integer.valueOf(rows.size()));
        summary.put("entered", Integer.valueOf(entered));
        summary.put("missing", Integer.valueOf(rows.size() - entered));
        summary.put("avg", Double.valueOf(entered == 0 ? 0 : Math.round(sum * 10 / entered) / 10.0));
        summary.put("passRate", Double.valueOf(entered == 0 ? 0 : Math.round(passed * 1000.0 / entered) / 10.0));
        summary.put("passed", Integer.valueOf(passed));
        return summary;
    }

    private String encode(String value) {
        try {
            return URLEncoder.encode(value == null ? "" : value, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            return "";
        }
    }
}
