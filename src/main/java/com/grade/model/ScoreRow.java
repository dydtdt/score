package com.grade.model;

import java.io.Serializable;

/**
 * 成绩行视图对象：把「学生 + 课程 + 分数」拼成一行，供成绩录入页和学生查询页使用。
 * <p>score 为 null 表示这门课尚未录入成绩。
 */
public class ScoreRow implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long studentId;
    private String sno;
    private String sname;
    private String className;
    private Long courseId;
    private String cname;
    private double credit;
    private Double score;

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

    /** 页面显示用的学分文本：4 -> "4.0" */
    public String getCreditText() {
        return com.grade.util.NumFmt.one(credit);
    }

    public Double getScore() {
        return score;
    }

    public void setScore(Double score) {
        this.score = score;
    }

    /** 是否已录入 */
    public boolean isEntered() {
        return score != null;
    }

    /** 是否及格（未录入时返回 false） */
    public boolean isPassed() {
        return score != null && score >= 60;
    }

    /**
     * 页面显示用的分数字符串：88 而不是 88.0，87.5 保持原样，未录入返回空串。
     */
    public String getScoreText() {
        if (score == null) {
            return "";
        }
        double v = score.doubleValue();
        if (v == Math.rint(v)) {
            return String.valueOf((long) v);
        }
        return java.math.BigDecimal.valueOf(v).stripTrailingZeros().toPlainString();
    }
}
