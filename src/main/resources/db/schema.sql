-- =====================================================================
--  班级学生成绩管理系统 —— 建表脚本
--  说明：本脚本同时适用于 H2(MODE=MySQL) 与 MySQL 8.0。
--       应用启动时若检测到 t_student 表不存在，会自动执行本脚本，
--       因此一般情况下无需手工执行。
-- =====================================================================

-- 用户表：教师与学生共用，role 区分身份，student_id 关联学生档案
CREATE TABLE IF NOT EXISTS t_user (
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    username     VARCHAR(40)  NOT NULL,
    password     VARCHAR(80)  NOT NULL,
    role         VARCHAR(20)  NOT NULL,
    student_id   BIGINT       NULL,
    display_name VARCHAR(40)  NOT NULL,
    CONSTRAINT uk_user_username UNIQUE (username)
);

-- 学生表
CREATE TABLE IF NOT EXISTS t_student (
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    sno        VARCHAR(20) NOT NULL,
    sname      VARCHAR(30) NOT NULL,
    gender     VARCHAR(4)  NOT NULL,
    class_name VARCHAR(40) NOT NULL,
    CONSTRAINT uk_student_sno UNIQUE (sno)
);

-- 课程表
CREATE TABLE IF NOT EXISTS t_course (
    id     BIGINT AUTO_INCREMENT PRIMARY KEY,
    cname  VARCHAR(40) NOT NULL,
    credit DECIMAL(3,1) NOT NULL,
    CONSTRAINT uk_course_cname UNIQUE (cname)
);

-- 成绩表：一个学生 + 一门课程 只有一条记录
CREATE TABLE IF NOT EXISTS t_score (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    student_id  BIGINT       NOT NULL,
    course_id   BIGINT       NOT NULL,
    score       DECIMAL(5,2) NOT NULL,
    update_time TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_score_student_course UNIQUE (student_id, course_id),
    CONSTRAINT fk_score_student FOREIGN KEY (student_id) REFERENCES t_student (id) ON DELETE CASCADE,
    CONSTRAINT fk_score_course  FOREIGN KEY (course_id)  REFERENCES t_course (id)  ON DELETE CASCADE
);
