package com.grade.web;

import com.grade.model.Course;
import com.grade.model.CourseStat;
import com.grade.service.CourseService;
import com.grade.service.StatService;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 教师后台：课程管理。
 */
public class CourseServlet extends BaseServlet {

    private static final long serialVersionUID = 1L;

    private final CourseService courseService = new CourseService();
    private final StatService statService = new StatService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String action = trimParam(req, "action");
        if ("new".equals(action)) {
            showForm(req, resp, new Course(), true);
            return;
        }
        if ("edit".equals(action)) {
            Course course = courseService.findById(longParam(req, "id", -1));
            if (course == null) {
                flash(req, "error", "要编辑的课程不存在");
                redirect(req, resp, "/teacher/courses");
                return;
            }
            showForm(req, resp, course, false);
            return;
        }
        showList(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String action = trimParam(req, "action");
        if ("delete".equals(action)) {
            String error = courseService.delete(longParam(req, "id", -1));
            if (error != null) {
                flash(req, "error", error);
            } else {
                flash(req, "success", "课程已删除");
            }
            redirect(req, resp, "/teacher/courses");
            return;
        }
        if ("save".equals(action)) {
            save(req, resp);
            return;
        }
        redirect(req, resp, "/teacher/courses");
    }

    private void save(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        long id = longParam(req, "id", -1);
        boolean isNew = id <= 0;

        Course course = new Course();
        course.setId(isNew ? null : Long.valueOf(id));
        course.setCname(trimParam(req, "cname"));
        course.setCredit(doubleParam(req, "credit", 0));

        String error = courseService.save(course, isNew);
        if (error != null) {
            req.setAttribute("error", error);
            showForm(req, resp, course, isNew);
            return;
        }
        flash(req, "success", isNew ? "新增课程成功" : "课程信息已更新");
        redirect(req, resp, "/teacher/courses");
    }

    private void showForm(HttpServletRequest req, HttpServletResponse resp, Course course, boolean isNew)
            throws ServletException, IOException {
        req.setAttribute("course", course);
        req.setAttribute("isNew", Boolean.valueOf(isNew));
        req.setAttribute("active", "courses");
        forward(req, resp, "teacher/course-form.jsp");
    }

    private void showList(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        List<Course> courses = courseService.findAll();
        Map<Long, CourseStat> statById = new HashMap<Long, CourseStat>();
        for (CourseStat stat : statService.courseStats()) {
            statById.put(stat.getCourseId(), stat);
        }
        for (Course course : courses) {
            CourseStat stat = statById.get(course.getId());
            course.setEnteredCount(stat == null ? 0L : stat.getEntered());
        }
        req.setAttribute("courses", courses);
        req.setAttribute("active", "courses");
        forward(req, resp, "teacher/course-list.jsp");
    }
}
