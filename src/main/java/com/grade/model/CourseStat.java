package com.grade.model;

import java.io.Serializable;

/**
 * 单门课程的统计结果：已录入人数、平均分、及格率、最高分、最低分。
 * <p>getPassRate() 直接返回百分数（如 75.0 表示 75%），方便 JSP 里直接格式化显示。
 */
public class CourseStat implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long courseId;
    private String cname;
    private double credit;
    private long entered;
    private long passCount;
    private double avgScore;
    private double maxScore;
    private double minScore;

    public Long getCourseId() {
        return courseId;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }

    public String getCname() {
        return cname;
    }

    public void setCname(String cname) {
        this.cname = cname;
    }

    public double getCredit() {
        return credit;
    }

    public void setCredit(double credit) {
        this.credit = credit;
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

    public long getFailCount() {
        return entered - passCount;
    }

    public double getAvgScore() {
        return avgScore;
    }

    public void setAvgScore(double avgScore) {
        this.avgScore = avgScore;
    }

    public double getMaxScore() {
        return maxScore;
    }

    public void setMaxScore(double maxScore) {
        this.maxScore = maxScore;
    }

    public double getMinScore() {
        return minScore;
    }

    public void setMinScore(double minScore) {
        this.minScore = minScore;
    }

    /** 及格率（百分数，保留一位小数） */
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

    public String getMaxScoreText() {
        return com.grade.util.NumFmt.trim(maxScore);
    }

    public String getMinScoreText() {
        return com.grade.util.NumFmt.trim(minScore);
    }

    public String getCreditText() {
        return com.grade.util.NumFmt.one(credit);
    }
}
