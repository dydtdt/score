package com.grade.dao;

import com.grade.model.ClassStat;
import com.grade.model.CourseStat;
import com.grade.model.Overview;
import com.grade.model.ScoreRow;
import com.grade.model.StudentStat;
import com.grade.util.DataAccessException;
import com.grade.util.DbUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * 成绩表 DAO，同时承担平均分 / 及格率等统计查询（用 SQL 聚合函数实现）。
 */
public class ScoreDao {

    /**
     * 写入或更新一条成绩（先 UPDATE，没有命中再 INSERT，H2 与 MySQL 通用）。
     */
    public void saveOrUpdate(long studentId, long courseId, double score) {
        String update = "UPDATE t_score SET score = ?, update_time = CURRENT_TIMESTAMP WHERE student_id = ? AND course_id = ?";
        String insert = "INSERT INTO t_score (student_id, course_id, score, update_time) VALUES (?, ?, ?, CURRENT_TIMESTAMP)";
        try (Connection conn = DbUtil.getConnection()) {
            int updated;
            try (PreparedStatement ps = conn.prepareStatement(update)) {
                ps.setDouble(1, score);
                ps.setLong(2, studentId);
                ps.setLong(3, courseId);
                updated = ps.executeUpdate();
            }
            if (updated == 0) {
                try (PreparedStatement ps = conn.prepareStatement(insert)) {
                    ps.setLong(1, studentId);
                    ps.setLong(2, courseId);
                    ps.setDouble(3, score);
                    ps.executeUpdate();
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("保存成绩失败", e);
        }
    }

    public void deleteScore(long studentId, long courseId) {
        String sql = "DELETE FROM t_score WHERE student_id = ? AND course_id = ?";
        try (Connection conn = DbUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, studentId);
            ps.setLong(2, courseId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("删除成绩失败", e);
        }
    }

    public Double findScore(long studentId, long courseId) {
        String sql = "SELECT score FROM t_score WHERE student_id = ? AND course_id = ?";
        try (Connection conn = DbUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, studentId);
            ps.setLong(2, courseId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    double v = rs.getDouble(1);
                    return rs.wasNull() ? null : v;
                }
                return null;
            }
        } catch (SQLException e) {
            throw new DataAccessException("查询单条成绩失败", e);
        }
    }

    /**
     * 成绩录入页：指定课程下，某个班（可为空＝全部）的所有学生及其当前分数。
     */
    public List<ScoreRow> findRowsByCourse(long courseId, String className) {
        StringBuilder sql = new StringBuilder(
                "SELECT st.id AS student_id, st.sno, st.sname, st.class_name, s.score "
                        + "FROM t_student st LEFT JOIN t_score s "
                        + "ON s.student_id = st.id AND s.course_id = ? WHERE 1 = 1");
        boolean hasClass = className != null && !className.trim().isEmpty();
        if (hasClass) {
            sql.append(" AND st.class_name = ?");
        }
        sql.append(" ORDER BY st.class_name, st.sno");
        List<ScoreRow> list = new ArrayList<ScoreRow>();
        try (Connection conn = DbUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            ps.setLong(1, courseId);
            if (hasClass) {
                ps.setString(2, className.trim());
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ScoreRow row = new ScoreRow();
                    row.setStudentId(rs.getLong("student_id"));
                    row.setSno(rs.getString("sno"));
                    row.setSname(rs.getString("sname"));
                    row.setClassName(rs.getString("class_name"));
                    double v = rs.getDouble("score");
                    row.setScore(rs.wasNull() ? null : v);
                    row.setCourseId(courseId);
                    list.add(row);
                }
            }
            return list;
        } catch (SQLException e) {
            throw new DataAccessException("查询成绩录入列表失败", e);
        }
    }

    /**
     * 学生查询页：某个学生所有课程的成绩（未录入的课程也会列出，便于提示）。
     */
    public List<ScoreRow> findRowsByStudent(long studentId) {
        String sql = "SELECT c.id AS course_id, c.cname, c.credit, s.score "
                + "FROM t_course c LEFT JOIN t_score s "
                + "ON s.course_id = c.id AND s.student_id = ? ORDER BY c.id";
        List<ScoreRow> list = new ArrayList<ScoreRow>();
        try (Connection conn = DbUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ScoreRow row = new ScoreRow();
                    row.setStudentId(studentId);
                    row.setCourseId(rs.getLong("course_id"));
                    row.setCname(rs.getString("cname"));
                    row.setCredit(rs.getDouble("credit"));
                    double v = rs.getDouble("score");
                    row.setScore(rs.wasNull() ? null : v);
                    list.add(row);
                }
            }
            return list;
        } catch (SQLException e) {
            throw new DataAccessException("查询学生成绩失败", e);
        }
    }

    /**
     * 每门课程的平均分、及格率、最高分、最低分。
     */
    public List<CourseStat> courseStats() {
        String sql = "SELECT c.id, c.cname, c.credit, "
                + "COUNT(s.id) AS entered, "
                + "SUM(CASE WHEN s.score >= 60 THEN 1 ELSE 0 END) AS pass_cnt, "
                + "AVG(s.score) AS avg_score, MAX(s.score) AS max_score, MIN(s.score) AS min_score "
                + "FROM t_course c LEFT JOIN t_score s ON s.course_id = c.id "
                + "GROUP BY c.id, c.cname, c.credit ORDER BY c.id";
        List<CourseStat> list = new ArrayList<CourseStat>();
        try (Connection conn = DbUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                CourseStat stat = new CourseStat();
                stat.setCourseId(rs.getLong("id"));
                stat.setCname(rs.getString("cname"));
                stat.setCredit(rs.getDouble("credit"));
                stat.setEntered(rs.getLong("entered"));
                stat.setPassCount(rs.getLong("pass_cnt"));
                stat.setAvgScore(readDouble(rs, "avg_score"));
                stat.setMaxScore(readDouble(rs, "max_score"));
                stat.setMinScore(readDouble(rs, "min_score"));
                list.add(stat);
            }
            return list;
        } catch (SQLException e) {
            throw new DataAccessException("统计课程成绩失败", e);
        }
    }

    /**
     * 每个班级的平均分与及格率。
     */
    public List<ClassStat> classStats() {
        String sql = "SELECT st.class_name, "
                + "COUNT(DISTINCT st.id) AS stu_cnt, "
                + "COUNT(s.id) AS entered, "
                + "SUM(CASE WHEN s.score >= 60 THEN 1 ELSE 0 END) AS pass_cnt, "
                + "AVG(s.score) AS avg_score "
                + "FROM t_student st LEFT JOIN t_score s ON s.student_id = st.id "
                + "GROUP BY st.class_name ORDER BY st.class_name";
        List<ClassStat> list = new ArrayList<ClassStat>();
        try (Connection conn = DbUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                ClassStat stat = new ClassStat();
                stat.setClassName(rs.getString("class_name"));
                stat.setStudentCount(rs.getLong("stu_cnt"));
                stat.setEntered(rs.getLong("entered"));
                stat.setPassCount(rs.getLong("pass_cnt"));
                stat.setAvgScore(readDouble(rs, "avg_score"));
                list.add(stat);
            }
            return list;
        } catch (SQLException e) {
            throw new DataAccessException("统计班级成绩失败", e);
        }
    }

    /**
     * 学生个人统计（平均分、及格率），按平均分从高到低排列并生成名次。
     *
     * @param className 为空表示全部班级
     */
    public List<StudentStat> studentStats(String className) {
        boolean hasClass = className != null && !className.trim().isEmpty();
        String sql = "SELECT st.id, st.sno, st.sname, st.class_name, "
                + "COUNT(s.id) AS entered, "
                + "SUM(CASE WHEN s.score >= 60 THEN 1 ELSE 0 END) AS pass_cnt, "
                + "AVG(s.score) AS avg_score "
                + "FROM t_student st LEFT JOIN t_score s ON s.student_id = st.id "
                + (hasClass ? "WHERE st.class_name = ? " : "")
                + "GROUP BY st.id, st.sno, st.sname, st.class_name "
                + "ORDER BY avg_score DESC, st.sno ASC";
        List<StudentStat> list = new ArrayList<StudentStat>();
        try (Connection conn = DbUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            if (hasClass) {
                ps.setString(1, className.trim());
            }
            try (ResultSet rs = ps.executeQuery()) {
                int rank = 0;
                while (rs.next()) {
                    StudentStat stat = new StudentStat();
                    stat.setStudentId(rs.getLong("id"));
                    stat.setSno(rs.getString("sno"));
                    stat.setSname(rs.getString("sname"));
                    stat.setClassName(rs.getString("class_name"));
                    stat.setEntered(rs.getLong("entered"));
                    stat.setPassCount(rs.getLong("pass_cnt"));
                    stat.setAvgScore(readDouble(rs, "avg_score"));
                    stat.setRank(stat.getEntered() > 0 ? ++rank : 0);
                    list.add(stat);
                }
            }
            return list;
        } catch (SQLException e) {
            throw new DataAccessException("统计学生成绩失败", e);
        }
    }

    /**
     * 首页总览。
     */
    public Overview overview() {
        Overview o = new Overview();
        try (Connection conn = DbUtil.getConnection()) {
            try (PreparedStatement ps = conn.prepareStatement("SELECT COUNT(*) FROM t_student");
                 ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    o.setStudentCount(rs.getLong(1));
                }
            }
            try (PreparedStatement ps = conn.prepareStatement("SELECT COUNT(*) FROM t_course");
                 ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    o.setCourseCount(rs.getLong(1));
                }
            }
            try (PreparedStatement ps = conn.prepareStatement("SELECT COUNT(DISTINCT class_name) FROM t_student");
                 ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    o.setClassCount(rs.getLong(1));
                }
            }
            String scoreSql = "SELECT COUNT(*), AVG(score), SUM(CASE WHEN score >= 60 THEN 1 ELSE 0 END) FROM t_score";
            try (PreparedStatement ps = conn.prepareStatement(scoreSql);
                 ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    o.setScoreCount(rs.getLong(1));
                    o.setAvgScore(readDouble(rs, 2));
                    o.setPassCount(rs.getLong(3));
                    o.setFailCount(o.getScoreCount() - o.getPassCount());
                }
            }
            return o;
        } catch (SQLException e) {
            throw new DataAccessException("查询总览数据失败", e);
        }
    }

    /** 读取可能为 NULL 的数值列，NULL 时返回 0 */
    private double readDouble(ResultSet rs, String column) throws SQLException {
        double v = rs.getDouble(column);
        return rs.wasNull() ? 0.0 : v;
    }

    private double readDouble(ResultSet rs, int index) throws SQLException {
        double v = rs.getDouble(index);
        return rs.wasNull() ? 0.0 : v;
    }
}
