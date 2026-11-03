package com.grade.model;

import java.io.Serializable;

/**
 * 教师首页的总览数字。
 */
public class Overview implements Serializable {

    private static final long serialVersionUID = 1L;

    private long studentCount;
    private long courseCount;
    private long scoreCount;
    private long classCount;
    private double avgScore;
    private long passCount;
    private long failCount;

    public long getStudentCount() {
        return studentCount;
    }

    public void setStudentCount(long studentCount) {
        this.studentCount = studentCount;
    }

    public long getCourseCount() {
        return courseCount;
    }

    public void setCourseCount(long courseCount) {
        this.courseCount = courseCount;
    }

    public long getScoreCount() {
        return scoreCount;
    }

    public void setScoreCount(long scoreCount) {
        this.scoreCount = scoreCount;
    }

    public long getClassCount() {
        return classCount;
    }

    public void setClassCount(long classCount) {
        this.classCount = classCount;
    }

    public double getAvgScore() {
        return avgScore;
    }

    public void setAvgScore(double avgScore) {
        this.avgScore = avgScore;
    }

    public long getPassCount() {
        return passCount;
    }

    public void setPassCount(long passCount) {
        this.passCount = passCount;
    }

    public long getFailCount() {
        return failCount;
    }

    public void setFailCount(long failCount) {
        this.failCount = failCount;
    }

    /** 总体及格率（百分数） */
    public double getPassRate() {
        if (scoreCount == 0) {
            return 0.0;
        }
        return Math.round(passCount * 1000.0 / scoreCount) / 10.0;
    }

    /** 页面显示用：平均分保留一位小数 */
    public String getAvgScoreText() {
        return com.grade.util.NumFmt.one(avgScore);
    }

    /** 页面显示用：及格率保留一位小数 */
    public String getPassRateText() {
        return com.grade.util.NumFmt.one(getPassRate());
    }
}
