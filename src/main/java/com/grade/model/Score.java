package com.grade.model;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * 成绩记录（对应 t_score 一行）。
 */
public class Score implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private Long studentId;
    private Long courseId;
    private double score;
    private Timestamp updateTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    public Long getCourseId() {
        return courseId;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public Timestamp getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Timestamp updateTime) {
        this.updateTime = updateTime;
    }

    public boolean isPassed() {
        return score >= 60;
    }
}
