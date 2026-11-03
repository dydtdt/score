package com.grade.model;

import java.io.Serializable;

/**
 * 课程。
 */
public class Course implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private String cname;
    private double credit;
    /** 非数据库字段：课程列表页展示「已录入成绩条数」时用到 */
    private long enteredCount;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public long getEnteredCount() {
        return enteredCount;
    }

    public void setEnteredCount(long enteredCount) {
        this.enteredCount = enteredCount;
    }

    /** 页面显示用的学分文本：4 -> "4.0" */
    public String getCreditText() {
        return com.grade.util.NumFmt.one(credit);
    }
}
