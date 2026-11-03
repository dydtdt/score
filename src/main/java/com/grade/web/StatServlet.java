package com.grade.web;

import com.grade.service.StatService;
import com.grade.service.StudentService;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 教师后台：统计分析（课程平均分 / 及格率、班级对比、学生排名）。
 */
public class StatServlet extends BaseServlet {

    private static final long serialVersionUID = 1L;

    private final StatService statService = new StatService();
    private final StudentService studentService = new StudentService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String className = trimParam(req, "className");

        req.setAttribute("className", className);
        req.setAttribute("classNames", studentService.listClassNames());
        req.setAttribute("overview", statService.overview());
        req.setAttribute("courseStats", statService.courseStats());
        req.setAttribute("classStats", statService.classStats());
        req.setAttribute("studentStats", statService.studentStats(className));
        req.setAttribute("active", "stats");
        forward(req, resp, "teacher/stat.jsp");
    }
}
