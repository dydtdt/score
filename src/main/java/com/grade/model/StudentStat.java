package com.grade.model;

import java.io.Serializable;

/**
 * 学生个人的统计结果：综合平均分、及格率、班级排名。
 */
public class StudentStat implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long studentId;
    private String sno;
    private String sname;
    private String className;
    private long entered;
    private long passCount;
    private double avgScore;
    private int rank;

    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    public String getSno() {
        return sno;
    }

    public void setSno(String sno) {
        this.sno = sno;
    }

    public String getSname() {
        return sname;
    }

    public void setSname(String sname) {
        this.sname = sname;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public long getEntered() {
        return entered;
    }

    public void setEntered(long entered) {
        this.entered = entered;
    }

    public long getPassCount() {
        return passCount;
    }

    public void setPassCount(long passCount) {
        this.passCount = passCount;
    }

    public double getAvgScore() {
        return avgScore;
    }

    public void setAvgScore(double avgScore) {
        this.avgScore = avgScore;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public double getPassRate() {
        if (entered == 0) {
            return 0.0;
        }
        return Math.round(passCount * 1000.0 / entered) / 10.0;
    }

    public String getAvgScoreText() {
        return com.grade.util.NumFmt.one(avgScore);
    }

    public String getPassRateText() {
        return com.grade.util.NumFmt.one(getPassRate());
    }
}
