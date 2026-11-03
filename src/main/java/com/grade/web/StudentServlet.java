package com.grade.web;

import com.grade.model.Student;
import com.grade.service.StudentService;
import com.grade.util.Page;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * 教师后台：学生的查询 / 新增 / 修改 / 删除。
 */
public class StudentServlet extends BaseServlet {

    private static final long serialVersionUID = 1L;

    private final StudentService studentService = new StudentService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String action = trimParam(req, "action");
        if ("new".equals(action)) {
            showForm(req, resp, new Student(), true);
            return;
        }
        if ("edit".equals(action)) {
            long id = longParam(req, "id", -1);
            Student student = studentService.findById(id);
            if (student == null) {
                flash(req, "error", "要编辑的学生不存在");
                redirect(req, resp, "/teacher/students");
                return;
            }
            showForm(req, resp, student, false);
            return;
        }
        showList(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String action = trimParam(req, "action");
        if ("delete".equals(action)) {
            long id = longParam(req, "id", -1);
            Student student = studentService.findById(id);
            if (student == null) {
                flash(req, "error", "要删除的学生不存在");
            } else {
                studentService.delete(id);
                flash(req, "success", "已删除学生 " + student.getSname() + "（" + student.getSno() + "）及其成绩");
            }
            redirect(req, resp, "/teacher/students");
            return;
        }
        if ("save".equals(action)) {
            save(req, resp);
            return;
        }
        redirect(req, resp, "/teacher/students");
    }

    private void save(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        long id = longParam(req, "id", -1);
        boolean isNew = id <= 0;

        Student student = new Student();
        student.setId(isNew ? null : Long.valueOf(id));
        student.setSno(trimParam(req, "sno"));
        student.setSname(trimParam(req, "sname"));
        student.setGender(trimParam(req, "gender"));
        student.setClassName(trimParam(req, "className"));

        String error = studentService.save(student, isNew);
        if (error != null) {
            req.setAttribute("error", error);
            showForm(req, resp, student, isNew);
            return;
        }
        flash(req, "success", isNew ? "新增学生成功，登录账号为学号，初始密码 123456"
                : "学生信息已更新");
        redirect(req, resp, "/teacher/students");
    }

    private void showForm(HttpServletRequest req, HttpServletResponse resp, Student student, boolean isNew)
            throws ServletException, IOException {
        req.setAttribute("student", student);
        req.setAttribute("isNew", Boolean.valueOf(isNew));
        req.setAttribute("classNames", studentService.listClassNames());
        req.setAttribute("active", "students");
        forward(req, resp, "teacher/student-form.jsp");
    }

    private void showList(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String keyword = trimParam(req, "keyword");
        String className = trimParam(req, "className");
        int pageNum = Page.normalize(trimParam(req, "page"), 1, 1, 10000);
        int pageSize = 10;

        Page<Student> page = studentService.search(keyword, className, pageNum, pageSize);
        List<String> classNames = studentService.listClassNames();

        req.setAttribute("page", page);
        req.setAttribute("keyword", keyword);
        req.setAttribute("className", className);
        req.setAttribute("classNames", classNames);
        req.setAttribute("active", "students");
        forward(req, resp, "teacher/student-list.jsp");
    }
}
