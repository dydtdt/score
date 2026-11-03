package com.grade.dao;

import com.grade.model.Course;
import com.grade.util.DataAccessException;
import com.grade.util.DbUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * 课程表 DAO。
 */
public class CourseDao {

    public List<Course> findAll() {
        String sql = "SELECT id, cname, credit FROM t_course ORDER BY id";
        List<Course> list = new ArrayList<Course>();
        try (Connection conn = DbUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(map(rs));
            }
            return list;
        } catch (SQLException e) {
            throw new DataAccessException("查询课程列表失败", e);
        }
    }

    public Course findById(long id) {
        String sql = "SELECT id, cname, credit FROM t_course WHERE id = ?";
        try (Connection conn = DbUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? map(rs) : null;
            }
        } catch (SQLException e) {
            throw new DataAccessException("按 id 查询课程失败", e);
        }
    }

    public boolean existsCname(String cname, Long excludeId) {
        String sql = "SELECT COUNT(*) FROM t_course WHERE cname = ? AND (? IS NULL OR id <> ?)";
        try (Connection conn = DbUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, cname);
            if (excludeId == null) {
                ps.setNull(2, java.sql.Types.BIGINT);
                ps.setNull(3, java.sql.Types.BIGINT);
            } else {
                ps.setLong(2, excludeId);
                ps.setLong(3, excludeId);
            }
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getLong(1) > 0;
            }
        } catch (SQLException e) {
            throw new DataAccessException("检查课程名是否重复失败", e);
        }
    }

    public long insert(Course c) {
        String sql = "INSERT INTO t_course (cname, credit) VALUES (?, ?)";
        try (Connection conn = DbUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, c.getCname());
            ps.setDouble(2, c.getCredit());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    long id = keys.getLong(1);
                    c.setId(id);
                    return id;
                }
            }
            throw new DataAccessException("新增课程后未能取得主键");
        } catch (SQLException e) {
            throw new DataAccessException("新增课程失败", e);
        }
    }

    public void update(Course c) {
        String sql = "UPDATE t_course SET cname = ?, credit = ? WHERE id = ?";
        try (Connection conn = DbUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, c.getCname());
            ps.setDouble(2, c.getCredit());
            ps.setLong(3, c.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("修改课程失败", e);
        }
    }

    public void delete(long id) {
        String sql = "DELETE FROM t_course WHERE id = ?";
        try (Connection conn = DbUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("删除课程失败", e);
        }
    }

    /** 该课程已录入多少条成绩，删除课程前用来提示 */
    public long countScores(long courseId) {
        String sql = "SELECT COUNT(*) FROM t_score WHERE course_id = ?";
        try (Connection conn = DbUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, courseId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getLong(1) : 0L;
            }
        } catch (SQLException e) {
            throw new DataAccessException("统计课程成绩条数失败", e);
        }
    }

    private Course map(ResultSet rs) throws SQLException {
        Course c = new Course();
        c.setId(rs.getLong("id"));
        c.setCname(rs.getString("cname"));
        c.setCredit(rs.getDouble("credit"));
        return c;
    }
}
