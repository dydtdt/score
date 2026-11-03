package com.grade.service;

import com.grade.dao.StudentDao;
import com.grade.dao.UserDao;
import com.grade.model.Student;
import com.grade.util.Page;

import java.util.List;

/**
 * 学生业务逻辑：查询、新增、修改、删除（同时维护对应的登录账号）。
 */
public class StudentService {

    private final StudentDao studentDao = new StudentDao();
    private final UserDao userDao = new UserDao();

    public Page<Student> search(String keyword, String className, int pageNum, int pageSize) {
        Page<Student> page = new Page<Student>(pageNum, pageSize);
        long total = studentDao.count(keyword, className);
        page.setTotal(total);
        int totalPages = page.getTotalPages();
        if (pageNum > totalPages) {
            page.setPageNum(totalPages);
        }
        page.setList(studentDao.findPage(keyword, className, page.getOffset(), pageSize));
        return page;
    }

    public List<Student> findAll() {
        return studentDao.findAll();
    }

    public List<Student> findByClass(String className) {
        return studentDao.findByClass(className);
    }

    public Student findById(long id) {
        return studentDao.findById(id);
    }

    public List<String> listClassNames() {
        return studentDao.listClassNames();
    }

    /**
     * 新增或修改学生。
     *
     * @param isNew true 表示新增
     * @return 校验不通过时返回错误信息，成功返回 null
     */
    public String save(Student student, boolean isNew) {
        String error = validate(student);
        if (error != null) {
            return error;
        }
        if (studentDao.existsSno(student.getSno(), isNew ? null : student.getId())) {
            return "学号 " + student.getSno() + " 已存在，请换一个";
        }
        if (isNew) {
            long id = studentDao.insert(student);
            userDao.insertStudentAccount(id, student.getSno(), student.getSname());
        } else {
            studentDao.update(student);
            userDao.updateStudentAccount(student.getId(), student.getSno(), student.getSname());
        }
        return null;
    }

    /** 删除学生（成绩与登录账号一并清理） */
    public void delete(long id) {
        userDao.deleteByStudentId(id);
        studentDao.delete(id);
    }

    private String validate(Student s) {
        if (s.getSno() == null || s.getSno().trim().isEmpty()) {
            return "学号不能为空";
        }
        if (!s.getSno().trim().matches("\\d{4,20}")) {
            return "学号必须是 4~20 位数字";
        }
        if (s.getSname() == null || s.getSname().trim().isEmpty()) {
            return "姓名不能为空";
        }
        if (s.getSname().trim().length() > 20) {
            return "姓名过长";
        }
        if (s.getGender() == null || s.getGender().trim().isEmpty()) {
            return "请选择性别";
        }
        if (s.getClassName() == null || s.getClassName().trim().isEmpty()) {
            return "班级不能为空";
        }
        s.setSno(s.getSno().trim());
        s.setSname(s.getSname().trim());
        s.setClassName(s.getClassName().trim());
        s.setGender(s.getGender().trim());
        return null;
    }
}
