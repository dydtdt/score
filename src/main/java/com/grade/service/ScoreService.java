package com.grade.service;

import com.grade.dao.CourseDao;
import com.grade.dao.ScoreDao;
import com.grade.dao.StudentDao;
import com.grade.model.Course;
import com.grade.model.ScoreRow;
import com.grade.model.Student;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 成绩业务逻辑：批量录入、查询。
 */
public class ScoreService {

    private final ScoreDao scoreDao = new ScoreDao();
    private final CourseDao courseDao = new CourseDao();
    private final StudentDao studentDao = new StudentDao();

    /** 成绩录入页的数据：某门课程下所有学生的当前分数 */
    public List<ScoreRow> rowsForCourse(long courseId, String className) {
        Course course = courseDao.findById(courseId);
        List<ScoreRow> rows = scoreDao.findRowsByCourse(courseId, className);
        for (ScoreRow row : rows) {
            row.setCourseId(courseId);
            if (course != null) {
                row.setCname(course.getCname());
                row.setCredit(course.getCredit());
            }
        }
        return rows;
    }

    /** 学生查询页的数据：某学生所有课程的成绩 */
    public List<ScoreRow> rowsForStudent(long studentId) {
        Student student = studentDao.findById(studentId);
        List<ScoreRow> rows = scoreDao.findRowsByStudent(studentId);
        for (ScoreRow row : rows) {
            if (student != null) {
                row.setSno(student.getSno());
                row.setSname(student.getSname());
                row.setClassName(student.getClassName());
            }
        }
        return rows;
    }

    /**
     * 批量保存成绩。
     * <p>规则：留空的输入框表示「删除该条成绩」；非数字或超出 0~100 的输入会被跳过并计入提示。
     *
     * @param courseId 课程 id
     * @param inputs   key = 学生 id，value = 页面原始输入
     * @return 面向用户的结果说明
     */
    public String saveBatch(long courseId, Map<Long, String> inputs) {
        if (courseDao.findById(courseId) == null) {
            return "课程不存在，保存失败";
        }
        int saved = 0;
        int cleared = 0;
        int invalid = 0;
        List<String> problems = new ArrayList<String>();
        for (Map.Entry<Long, String> entry : inputs.entrySet()) {
            long studentId = entry.getKey();
            String raw = entry.getValue() == null ? "" : entry.getValue().trim();
            if (raw.isEmpty()) {
                if (scoreDao.findScore(studentId, courseId) != null) {
                    scoreDao.deleteScore(studentId, courseId);
                    cleared++;
                }
                continue;
            }
            Double value = parseScore(raw);
            if (value == null) {
                invalid++;
                problems.add(raw);
                continue;
            }
            scoreDao.saveOrUpdate(studentId, courseId, value.doubleValue());
            saved++;
        }
        StringBuilder sb = new StringBuilder();
        sb.append("保存成功：更新 ").append(saved).append(" 条");
        if (cleared > 0) {
            sb.append("，清空 ").append(cleared).append(" 条");
        }
        if (invalid > 0) {
            sb.append("，跳过 ").append(invalid).append(" 条无效输入（").append(join(problems)).append("）");
        }
        return sb.toString();
    }

    private Double parseScore(String raw) {
        try {
            double v = Double.parseDouble(raw);
            if (Double.isNaN(v) || v < 0 || v > 100) {
                return null;
            }
            // 保留两位小数，避免出现 88.12345 这种分数
            return Math.round(v * 100.0) / 100.0;
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private String join(List<String> values) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < values.size() && i < 5; i++) {
            if (i > 0) {
                sb.append("、");
            }
            sb.append(values.get(i));
        }
        if (values.size() > 5) {
            sb.append(" 等");
        }
        return sb.toString();
    }
}
