-- =====================================================================
--  班级学生成绩管理系统 —— 演示数据
--  说明：本脚本只在 t_student 表为空（全新数据库）时自动执行一次。
--       学生/课程的 id 由自增主键顺序生成，因此请勿单独重排下面的插入顺序。
-- =====================================================================

DELETE FROM t_score;
DELETE FROM t_user;
DELETE FROM t_student;
DELETE FROM t_course;

-- 1. 学生（两个班，共 16 人）
INSERT INTO t_student (sno, sname, gender, class_name) VALUES ('210101', '张伟', '男', '计算机2101');
INSERT INTO t_student (sno, sname, gender, class_name) VALUES ('210102', '王芳', '女', '计算机2101');
INSERT INTO t_student (sno, sname, gender, class_name) VALUES ('210103', '李娜', '女', '计算机2101');
INSERT INTO t_student (sno, sname, gender, class_name) VALUES ('210104', '刘洋', '男', '计算机2101');
INSERT INTO t_student (sno, sname, gender, class_name) VALUES ('210105', '陈静', '女', '计算机2101');
INSERT INTO t_student (sno, sname, gender, class_name) VALUES ('210106', '杨帆', '男', '计算机2101');
INSERT INTO t_student (sno, sname, gender, class_name) VALUES ('210107', '赵磊', '男', '计算机2101');
INSERT INTO t_student (sno, sname, gender, class_name) VALUES ('210108', '黄敏', '女', '计算机2101');
INSERT INTO t_student (sno, sname, gender, class_name) VALUES ('210201', '周杰', '男', '计算机2102');
INSERT INTO t_student (sno, sname, gender, class_name) VALUES ('210202', '吴倩', '女', '计算机2102');
INSERT INTO t_student (sno, sname, gender, class_name) VALUES ('210203', '徐鹏', '男', '计算机2102');
INSERT INTO t_student (sno, sname, gender, class_name) VALUES ('210204', '孙丽', '女', '计算机2102');
INSERT INTO t_student (sno, sname, gender, class_name) VALUES ('210205', '马超', '男', '计算机2102');
INSERT INTO t_student (sno, sname, gender, class_name) VALUES ('210206', '朱婷', '女', '计算机2102');
INSERT INTO t_student (sno, sname, gender, class_name) VALUES ('210207', '胡军', '男', '计算机2102');
INSERT INTO t_student (sno, sname, gender, class_name) VALUES ('210208', '林霞', '女', '计算机2102');

-- 2. 课程（5 门）
INSERT INTO t_course (cname, credit) VALUES ('Java程序设计', 4.0);
INSERT INTO t_course (cname, credit) VALUES ('数据库原理', 3.5);
INSERT INTO t_course (cname, credit) VALUES ('计算机网络', 3.0);
INSERT INTO t_course (cname, credit) VALUES ('数据结构', 4.0);
INSERT INTO t_course (cname, credit) VALUES ('软件工程', 3.0);

-- 3. 登录账号（密码统一为 SHA-256 摘要：教师 teacher123 / 学生 123456）
INSERT INTO t_user (username, password, role, student_id, display_name) VALUES
('admin', 'cde383eee8ee7a4400adf7a15f716f179a2eb97646b37e089eb8d6d04e663416', 'TEACHER', NULL, '王建国');
INSERT INTO t_user (username, password, role, student_id, display_name)
SELECT sno, '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', 'STUDENT', id, sname FROM t_student;

-- 4. 成绩（少量记录故意留空，用于演示
INSERT INTO t_score (student_id, course_id, score) VALUES
(1, 1, 73),
(1, 2, 56),
(1, 3, 64),
(1, 4, 64),
(1, 5, 49),
(2, 2, 79),
(2, 3, 76),
(2, 4, 76),
(2, 5, 72),
(3, 1, 74),
(3, 2, 68),
(3, 3, 76),
(3, 4, 65),
(3, 5, 61),
(4, 1, 98),
(4, 3, 89),
(4, 4, 89),
(4, 5, 85),
(5, 1, 89),
(5, 2, 83),
(5, 3, 80),
(5, 4, 80),
(5, 5, 76),
(6, 1, 57),
(6, 2, 51),
(6, 4, 59),
(6, 5, 44),
(7, 1, 100),
(7, 2, 86),
(7, 3, 94),
(7, 4, 94),
(7, 5, 90),
(8, 1, 73),
(8, 2, 67),
(8, 3, 75),
(8, 5, 60),
(9, 1, 75),
(9, 2, 69),
(9, 3, 77),
(9, 4, 77),
(9, 5, 62),
(10, 1, 89),
(10, 2, 83),
(10, 3, 80),
(10, 4, 80),
(11, 1, 51),
(11, 2, 45),
(11, 3, 53),
(11, 4, 42),
(11, 5, 38),
(12, 1, 100),
(12, 2, 85),
(12, 3, 93),
(12, 4, 93),
(12, 5, 78),
(13, 1, 83),
(13, 2, 77),
(13, 3, 74),
(13, 4, 74),
(13, 5, 70),
(14, 1, 69),
(14, 2, 63),
(14, 3, 71),
(14, 4, 60),
(14, 5, 56),
(15, 1, 97),
(15, 2, 80),
(15, 3, 88),
(15, 4, 88),
(15, 5, 84),
(16, 1, 65),
(16, 2, 59),
(16, 3, 56),
(16, 4, 56),
(16, 5, 52);

