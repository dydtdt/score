package com.grade.model;

import java.io.Serializable;

/**
 * 登录用户（教师 / 学生共用一张表，用 role 区分）。
 */
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    public static final String ROLE_TEACHER = "TEACHER";
    public static final String ROLE_STUDENT = "STUDENT";

    private Long id;
    private String username;
    private String password;
    private String role;
    private Long studentId;
    private String displayName;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public boolean isTeacher() {
        return ROLE_TEACHER.equals(role);
    }

    public boolean isStudent() {
        return ROLE_STUDENT.equals(role);
    }

    /** 用于登录后的首页跳转 */
    public String getHomePath() {
        return isTeacher() ? "/teacher/dashboard" : "/student/scores";
    }
}
