package com.grade.model;

import java.io.Serializable;

/**
 * 班级维度的统计结果：人数、已录入人次、平均分、及格率。
 */
public class ClassStat implements Serializable {

    private static final long serialVersionUID = 1L;

    private String className;
    private long studentCount;
    private long entered;
    private long passCount;
    private double avgScore;

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public long getStudentCount() {
        return studentCount;
    }

    public void setStudentCount(long studentCount) {
        this.studentCount = studentCount;
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
