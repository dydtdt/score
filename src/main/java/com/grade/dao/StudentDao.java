package com.grade.dao;

import com.grade.model.Student;
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
 * 学生表 DAO。查询支持「关键字 + 班级」组合条件与分页。
 */
public class StudentDao {

    private static final String COLUMNS = "id, sno, sname, gender, class_name";

    public long count(String keyword, String className) {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM t_student WHERE 1 = 1");
        List<Object> params = new ArrayList<Object>();
        appendConditions(sql, params, keyword, className);
        try (Connection conn = DbUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            bind(ps, params);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getLong(1) : 0L;
            }
        } catch (SQLException e) {
            throw new DataAccessException("统计学生人数失败", e);
        }
    }

    public List<Student> findPage(String keyword, String className, int offset, int limit) {
        StringBuilder sql = new StringBuilder("SELECT " + COLUMNS + " FROM t_student WHERE 1 = 1");
        List<Object> params = new ArrayList<Object>();
        appendConditions(sql, params, keyword, className);
        sql.append(" ORDER BY class_name, sno LIMIT ? OFFSET ?");
        try (Connection conn = DbUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            bind(ps, params);
            ps.setInt(params.size() + 1, limit);
            ps.setInt(params.size() + 2, offset);
            List<Student> list = new ArrayList<Student>();
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(map(rs));
                }
            }
            return list;
        } catch (SQLException e) {
            throw new DataAccessException("分页查询学生失败", e);
        }
    }

    public List<Student> findAll() {
        String sql = "SELECT " + COLUMNS + " FROM t_student ORDER BY class_name, sno";
        List<Student> list = new ArrayList<Student>();
        try (Connection conn = DbUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(map(rs));
            }
            return list;
        } catch (SQLException e) {
            throw new DataAccessException("查询全部学生失败", e);
        }
    }

    /** 成绩录入页使用：某个班的学生，按学号排序 */
    public List<Student> findByClass(String className) {
        StringBuilder sql = new StringBuilder("SELECT " + COLUMNS + " FROM t_student");
        boolean hasClass = className != null && !className.trim().isEmpty();
        if (hasClass) {
            sql.append(" WHERE class_name = ?");
        }
        sql.append(" ORDER BY class_name, sno");
        try (Connection conn = DbUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            if (hasClass) {
                ps.setString(1, className.trim());
            }
            List<Student> list = new ArrayList<Student>();
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(map(rs));
                }
            }
            return list;
        } catch (SQLException e) {
            throw new DataAccessException("按班级查询学生失败", e);
        }
    }

    public Student findById(long id) {
        String sql = "SELECT " + COLUMNS + " FROM t_student WHERE id = ?";
        try (Connection conn = DbUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? map(rs) : null;
            }
        } catch (SQLException e) {
            throw new DataAccessException("按 id 查询学生失败", e);
        }
    }

    public boolean existsSno(String sno, Long excludeId) {
        String sql = "SELECT COUNT(*) FROM t_student WHERE sno = ? AND (? IS NULL OR id <> ?)";
        try (Connection conn = DbUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, sno);
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
            throw new DataAccessException("检查学号是否重复失败", e);
        }
    }

    /** 新增学生并返回自增主键 */
    public long insert(Student s) {
        String sql = "INSERT INTO t_student (sno, sname, gender, class_name) VALUES (?, ?, ?, ?)";
        try (Connection conn = DbUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, s.getSno());
            ps.setString(2, s.getSname());
            ps.setString(3, s.getGender());
            ps.setString(4, s.getClassName());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    long id = keys.getLong(1);
                    s.setId(id);
                    return id;
                }
            }
            throw new DataAccessException("新增学生后未能取得主键");
        } catch (SQLException e) {
            throw new DataAccessException("新增学生失败", e);
        }
    }

    public void update(Student s) {
        String sql = "UPDATE t_student SET sno = ?, sname = ?, gender = ?, class_name = ? WHERE id = ?";
        try (Connection conn = DbUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, s.getSno());
            ps.setString(2, s.getSname());
            ps.setString(3, s.getGender());
            ps.setString(4, s.getClassName());
            ps.setLong(5, s.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("修改学生失败", e);
        }
    }

    public void delete(long id) {
        String sql = "DELETE FROM t_student WHERE id = ?";
        try (Connection conn = DbUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("删除学生失败", e);
        }
    }

    public List<String> listClassNames() {
        String sql = "SELECT DISTINCT class_name FROM t_student ORDER BY class_name";
        List<String> list = new ArrayList<String>();
        try (Connection conn = DbUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(rs.getString(1));
            }
            return list;
        } catch (SQLException e) {
            throw new DataAccessException("查询班级列表失败", e);
        }
    }

    private void appendConditions(StringBuilder sql, List<Object> params, String keyword, String className) {
        if (keyword != null && !keyword.trim().isEmpty()) {
            String kw = "%" + keyword.trim() + "%";
            sql.append(" AND (sno LIKE ? OR sname LIKE ?)");
            params.add(kw);
            params.add(kw);
        }
        if (className != null && !className.trim().isEmpty()) {
            sql.append(" AND class_name = ?");
            params.add(className.trim());
        }
    }

    private void bind(PreparedStatement ps, List<Object> params) throws SQLException {
        for (int i = 0; i < params.size(); i++) {
            ps.setObject(i + 1, params.get(i));
        }
    }

    private Student map(ResultSet rs) throws SQLException {
        Student s = new Student();
        s.setId(rs.getLong("id"));
        s.setSno(rs.getString("sno"));
        s.setSname(rs.getString("sname"));
        s.setGender(rs.getString("gender"));
        s.setClassName(rs.getString("class_name"));
        return s;
    }
}
