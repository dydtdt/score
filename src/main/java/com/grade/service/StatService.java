package com.grade.service;

import com.grade.dao.ScoreDao;
import com.grade.dao.StudentDao;
import com.grade.model.ClassStat;
import com.grade.model.CourseStat;
import com.grade.model.Overview;
import com.grade.model.Student;
import com.grade.model.StudentStat;

import java.util.List;

/**
 * 统计服务：平均分、及格率、排名。
 */
public class StatService {

    private final ScoreDao scoreDao = new ScoreDao();
    private final StudentDao studentDao = new StudentDao();

    public Overview overview() {
        return scoreDao.overview();
    }

    public List<CourseStat> courseStats() {
        return scoreDao.courseStats();
    }

    public List<ClassStat> classStats() {
        return scoreDao.classStats();
    }

    public List<StudentStat> studentStats(String className) {
        return scoreDao.studentStats(className);
    }

    /**
     * 单个学生的统计（含班级内排名）。学生登录后看自己的页面时使用。
     */
    public StudentStat statOfStudent(long studentId) {
        Student student = studentDao.findById(studentId);
        if (student == null) {
            return null;
        }
        List<StudentStat> list = scoreDao.studentStats(student.getClassName());
        for (StudentStat stat : list) {
            if (stat.getStudentId() != null && stat.getStudentId().longValue() == studentId) {
                return stat;
            }
        }
        StudentStat empty = new StudentStat();
        empty.setStudentId(studentId);
        empty.setSno(student.getSno());
        empty.setSname(student.getSname());
        empty.setClassName(student.getClassName());
        return empty;
    }
}
