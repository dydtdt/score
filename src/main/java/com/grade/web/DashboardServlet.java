package com.grade.web;

import com.grade.model.Overview;
import com.grade.service.CourseService;
import com.grade.service.StatService;
import com.grade.service.StudentService;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 教师工作台：总览数字 + 班级统计概览。
 */
public class DashboardServlet extends BaseServlet {

    private static final long serialVersionUID = 1L;

    private final StatService statService = new StatService();
    private final StudentService studentService = new StudentService();
    private final CourseService courseService = new CourseService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        Overview overview = statService.overview();
        req.setAttribute("overview", overview);
        req.setAttribute("classStats", statService.classStats());
        req.setAttribute("courseStats", statService.courseStats());
        req.setAttribute("classNames", studentService.listClassNames());
        req.setAttribute("courseCount", courseService.findAll().size());
        req.setAttribute("active", "dashboard");
        forward(req, resp, "teacher/dashboard.jsp");
    }
}
