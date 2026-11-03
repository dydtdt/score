package com.grade.dao;

import com.grade.model.User;
import com.grade.util.DataAccessException;
import com.grade.util.DbUtil;
import com.grade.util.PasswordUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * 用户表 DAO。
 */
public class UserDao {

    /** 学生账号的初始密码 */
    public static final String DEFAULT_STUDENT_PASSWORD = "123456";

    public User findByUsername(String username) {
        String sql = "SELECT id, username, password, role, student_id, display_name FROM t_user WHERE username = ?";
        try (Connection conn = DbUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? map(rs) : null;
            }
        } catch (SQLException e) {
            throw new DataAccessException("按用户名查询用户失败", e);
        }
    }

    public User findByStudentId(long studentId) {
        String sql = "SELECT id, username, password, role, student_id, display_name FROM t_user WHERE student_id = ?";
        try (Connection conn = DbUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? map(rs) : null;
            }
        } catch (SQLException e) {
            throw new DataAccessException("按学生查询用户失败", e);
        }
    }

    /** 新增学生时同步建一个登录账号（用户名＝学号，初始密码 123456） */
    public void insertStudentAccount(long studentId, String sno, String sname) {
        String sql = "INSERT INTO t_user (username, password, role, student_id, display_name) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DbUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, sno);
            ps.setString(2, PasswordUtil.sha256(DEFAULT_STUDENT_PASSWORD));
            ps.setString(3, User.ROLE_STUDENT);
            ps.setLong(4, studentId);
            ps.setString(5, sname);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("创建学生登录账号失败", e);
        }
    }

    /** 修改学生资料时同步账号信息 */
    public void updateStudentAccount(long studentId, String sno, String sname) {
        String sql = "UPDATE t_user SET username = ?, display_name = ? WHERE student_id = ?";
        try (Connection conn = DbUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, sno);
            ps.setString(2, sname);
            ps.setLong(3, studentId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("同步学生登录账号失败", e);
        }
    }

    public void deleteByStudentId(long studentId) {
        String sql = "DELETE FROM t_user WHERE student_id = ?";
        try (Connection conn = DbUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, studentId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("删除学生登录账号失败", e);
        }
    }

    public boolean usernameExists(String username, Long excludeUserId) {
        String sql = "SELECT COUNT(*) FROM t_user WHERE username = ? AND (? IS NULL OR id <> ?)";
        try (Connection conn = DbUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            if (excludeUserId == null) {
                ps.setNull(2, java.sql.Types.BIGINT);
                ps.setNull(3, java.sql.Types.BIGINT);
            } else {
                ps.setLong(2, excludeUserId);
                ps.setLong(3, excludeUserId);
            }
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getLong(1) > 0;
            }
        } catch (SQLException e) {
            throw new DataAccessException("检查用户名是否存在失败", e);
        }
    }

    public void updatePassword(long userId, String rawPassword) {
        String sql = "UPDATE t_user SET password = ? WHERE id = ?";
        try (Connection conn = DbUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, PasswordUtil.sha256(rawPassword));
            ps.setLong(2, userId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("修改密码失败", e);
        }
    }

    private User map(ResultSet rs) throws SQLException {
        User u = new User();
        u.setId(rs.getLong("id"));
        u.setUsername(rs.getString("username"));
        u.setPassword(rs.getString("password"));
        u.setRole(rs.getString("role"));
        long sid = rs.getLong("student_id");
        u.setStudentId(rs.wasNull() ? null : sid);
        u.setDisplayName(rs.getString("display_name"));
        return u;
    }
}
