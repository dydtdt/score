package com.grade.service;

import com.grade.dao.CourseDao;
import com.grade.model.Course;

import java.util.List;

/**
 * 课程业务逻辑。
 */
public class CourseService {

    private final CourseDao courseDao = new CourseDao();

    public List<Course> findAll() {
        return courseDao.findAll();
    }

    public Course findById(long id) {
        return courseDao.findById(id);
    }

    /**
     * 新增或修改课程。
     *
     * @return 校验不通过时返回错误信息，成功返回 null
     */
    public String save(Course course, boolean isNew) {
        if (course.getCname() == null || course.getCname().trim().isEmpty()) {
            return "课程名称不能为空";
        }
        course.setCname(course.getCname().trim());
        if (course.getCname().length() > 30) {
            return "课程名称过长";
        }
        if (course.getCredit() < 0 || course.getCredit() > 20) {
            return "学分应在 0~20 之间";
        }
        if (courseDao.existsCname(course.getCname(), isNew ? null : course.getId())) {
            return "课程 " + course.getCname() + " 已存在";
        }
        if (isNew) {
            courseDao.insert(course);
        } else {
            courseDao.update(course);
        }
        return null;
    }

    /** 删除课程；已录入成绩的课程不允许直接删除，避免误清历史数据 */
    public String delete(long id) {
        Course course = courseDao.findById(id);
        if (course == null) {
            return "课程不存在";
        }
        long count = courseDao.countScores(id);
        if (count > 0) {
            return "课程《" + course.getCname() + "》下已录入 " + count + " 条成绩，请先清空成绩再删除";
        }
        courseDao.delete(id);
        return null;
    }
}
