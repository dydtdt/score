# 班级学生成绩管理系统 · score

> Java EE 期末项目：**教师录入成绩 · 学生查询成绩 · 自动统计平均分与及格率**

技术栈：`Java 8` + `Servlet 4.0` + `JSP / JSTL` + `JDBC + DAO` + `Maven` + `Tomcat 9` + `H2 / MySQL`
前端为手写 HTML + CSS，无任何前端框架、无 CDN 依赖，克隆即可运行。

---

## 一、部署环境要求

| 项目 | 版本要求 | 说明 |
| --- | --- | --- |
| JDK | **1.8**（Java 8） | 项目按 Java 8 编译，`pom.xml` 已固定 `source/target = 1.8` |
| Web 容器 | **Tomcat 9.x** | 必须是 9.x！用的是 `javax.servlet` 包 |
| 构建工具 | Maven 3.6+ | IDEA 自带 Maven 即可 |
| 数据库 | 内置 H2（默认） | 零配置，随 war 发布，首次启动自动建表 |
| 数据库（可选） | MySQL 8.0 | 想交 MySQL 版本时切换，见「五、数据库配置」 |

> ⚠️ **不要用 Tomcat 10 / 11**：它们把 `javax.servlet.*` 换成了 `jakarta.servlet.*`，
> 本项目会直接 404 或报 `ClassNotFoundException`。请下载 Tomcat 9.x 的 Windows zip 版。

---

## 二、部署方式一：IDEA（推荐）

### 1. 打开项目

`File` → `Open` → 选择本仓库根目录下的 **`pom.xml`** → 弹窗选 **Open as Project**，
等待右下角 Maven 依赖下载完成。

### 2. 设置 JDK 8

`File` → `Project Structure` → `Project`

- `Project SDK`：选择 **1.8**
- `Project language level`：**8 - Lambdas, type annotations etc.**

### 3. 配置 Tomcat 9 本地服务器

1. `Run` → `Edit Configurations…` → 左上 `+` → **Tomcat Server** → **Local**
2. `Application server` → `Configure…` → 选中你解压的 Tomcat 9 目录（如 `D:\apache-tomcat-9.0.122`）
3. 确认 `Server` 页的 **HTTP port = 8080**（被占用可改 8081）
4. 切到 **`Deployment`** 页 → 点 `+` → **Artifact…** → 选择 **`grade-manager:war exploded`**
   （一定要选带 `exploded` 的那个，才能热更新）
5. 在 `Application context` 里把默认值改成：

```
/grade-manager
```

> 规则：必须以 `/` 开头，结尾不要加 `/`。

### 4. 启动并访问

点击绿色 ▶ 运行，控制台出现 `成绩管理系统启动完成，数据库连接：org.h2.Driver -> ...`
即表示初始化成功，然后浏览器打开：

```
http://localhost:8080/grade-manager/
```

首次启动会自动建表并写入演示数据（16 名学生 / 2 个班级 / 5 门课程 / 75 条成绩）。

---

## 三、部署方式二：命令行打包 + 手工丢进 Tomcat

```bash
# 1. 打包（在仓库根目录执行）
mvn clean package

# 2. 把 war 复制到 Tomcat 的 webapps 目录
#    Windows PowerShell：
Copy-Item target\grade-manager.war "D:\apache-tomcat-9.0.122\webapps\"

# 3. 启动 Tomcat
D:\apache-tomcat-9.0.122\bin\startup.bat

# 4. 访问（war 名即 context，所以路径就是 /grade-manager）
#    http://localhost:8080/grade-manager/
```

停止 Tomcat：`D:\apache-tomcat-9.0.122\bin\shutdown.bat`

---

## 四、演示账号

| 角色 | 账号 | 密码 | 权限 |
| --- | --- | --- | --- |
| 教师 | `admin` | `teacher123` | 管理学生 / 课程，录入成绩，查看全部统计 |
| 学生 | `210101`～`210108`、`210201`～`210208` | `123456` | 只能查看**本人**成绩、平均分、及格率、班级排名 |

> 新增学生时系统会自动创建登录账号（账号 = 学号，初始密码 `123456`）。
> 密码在库中以 **SHA-256 摘要**保存，不存明文。

---

## 五、数据库配置

配置文件：`src/main/resources/db.properties`

### 默认：H2 嵌入式数据库（开箱即用）

- 数据文件位置：`C:\Users\<你的用户名>\.grade-manager\gradedb.mv.db`
  （Linux / macOS 为 `~/.grade-manager`）
- 想换目录：启动参数加 `-Dgrade.data.dir=D:/your/path`
- **想恢复初始演示数据**：停掉 Tomcat → 删除上面那个 `.grade-manager` 目录 → 重新启动

### 可选：切换到 MySQL 8

1. 建库：

```sql
CREATE DATABASE grade_db DEFAULT CHARACTER SET utf8mb4;
```

2. 打开 `db.properties`，把 H2 那 4 行注释掉，启用 MySQL 那 4 行并填上密码：

```properties
db.driver=com.mysql.cj.jdbc.Driver
db.url=jdbc:mysql://localhost:3306/grade_db?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai&useSSL=false&allowPublicKeyRetrieval=true
db.username=root
db.password=你的密码
```

3. 重新部署启动即可 —— 表结构与演示数据会自动初始化（`db.autoInit=true`）。

> SQL 脚本 `src/main/resources/db/schema.sql`、`data.sql` 同时兼容 H2 与 MySQL，
> 也可以手工执行（注意 `data.sql` 只应在空库执行一次）。

---

## 六、功能一览

### 教师端

| 菜单 | 功能 |
| --- | --- |
| 工作台 | 学生数、课程数、成绩记录数、总体平均分、总体及格率；班级/课程概览表（及格率进度条） |
| 学生管理 | 分页列表、按学号/姓名搜索、按班级筛选、新增、修改、删除（级联清理成绩与账号） |
| 课程管理 | 课程增删改查；已有成绩的课程会阻止删除，避免误删 |
| 成绩录入 | 选「课程 + 班级」整班批量录入；留空＝清空；自动校验 0~100，非法输入跳过并提示 |
| 统计分析 | 课程平均分/及格率/最高最低分、班级横向对比、学生综合排名（可切班级） |

### 学生端

| 菜单 | 功能 |
| --- | --- |
| 我的成绩 | 本人各科成绩（卡片 + 表格）、已录/未录状态、及格标记 |
| 个人统计 | 平均分、及格率、已录课程数、班级排名 |

---

## 七、目录结构

```
score/
├── pom.xml                          Maven 配置（war 打包，锁定 Java 8）
├── README.md
└── src/main/
    ├── java/com/grade/
    │   ├── model/                   实体与统计模型
    │   │   User / Student / Course / Score / ScoreRow
    │   │   CourseStat / ClassStat / StudentStat / Overview
    │   ├── dao/                     数据访问层（JDBC + PreparedStatement）
    │   │   UserDao / StudentDao / CourseDao / ScoreDao
    │   ├── service/                 业务层
    │   │   Auth / Student / Course / Score / Stat
    │   ├── web/                     Servlet 控制器
    │   │   BaseServlet / Login / Logout / Dashboard / Student
    │   │   Course / Score / Stat / MyScore
    │   ├── filter/                  EncodingFilter（UTF-8）、AuthFilter（登录 + 角色隔离）
    │   ├── listener/                AppContextListener（启动时初始化数据库）
    │   └── util/                    DbUtil / PasswordUtil / Page / NumFmt / DataAccessException
    ├── resources/
    │   ├── db.properties            数据库连接配置（H2 / MySQL 切换处）
    │   └── db/schema.sql, data.sql  建表脚本 / 演示数据（启动时自动执行）
    └── webapp/
        ├── index.jsp                入口：按登录状态跳转
        ├── css/style.css            全站样式
        └── WEB-INF/
            ├── web.xml              Servlet / Filter / Listener / JSP 编码 / 错误页配置
            └── views/               JSP 视图（login、teacher/*、student/*、error）
```

---

## 八、技术要点

- **Servlet 分层**：`BaseServlet` 封装转发、重定向、当前用户、一次性提示消息，7 个 Servlet 分管各模块
- **JSP + JSTL**：视图全部放在 `/WEB-INF/views`（不能直接输 URL 访问，只能经 Servlet 转发），
  用 `c:if / c:choose / c:forEach / c:url / c:out` 渲染；公共头尾用静态包含复用
- **Filter**：`EncodingFilter` 统一 UTF-8；`AuthFilter` 做登录拦截 + 角色隔离
  （学生访问 `/teacher/**` 会被打回自己的首页）
- **Listener**：`AppContextListener` 启动时初始化数据库并打印实际连接串
- **JDBC + DAO**：全部 `PreparedStatement`（防 SQL 注入）+ try-with-resources
- **统计**：平均分 = `AVG(score)`；及格率 = `SUM(CASE WHEN score >= 60 THEN 1 ELSE 0 END) / 已录人次`
- **分页**：通用 `Page<T>` + `LIMIT ? OFFSET ?`，翻页时保留搜索条件
- **安全**：登录后重建会话、密码 SHA-256、`/WEB-INF` 保护视图、自定义 404/500 错误页

---

## 九、常见问题

**Q1：启动报 `Address localhost:8080 is already in use`**
8080 被占用。改 Tomcat 的 `conf/server.xml` 里 `<Connector port="8080">`，或在 IDEA 的
`Server` 页把 HTTP port 改成 8081（访问地址的端口也要跟着改）。

**Q2：编译报「无效的目标发行版: 24」**
说明 Maven 的全局 `settings.xml` 里有 profile 把 `maven.compiler.source/target` 顶掉了。
本项目 `pom.xml` 已在 `maven-compiler-plugin` 里写死 `1.8` 以覆盖它；若 IDEA 仍报错，
去 `Maven` 面板 → `Profiles` 取消勾选相关 JDK profile，或把模块 `Language level` 设为 8。

**Q3：Tomcat 10 / 11 部署后 404 或报 `jakarta.servlet` 相关错误**
容器版本不对。本项目基于 `javax.servlet`，请换成 **Tomcat 9.x**。

**Q4：页面中文乱码**
工程已统一 UTF-8：`EncodingFilter` 设请求编码、每个 JSP（含被静态包含的片段）声明
`pageEncoding="UTF-8"`、`web.xml` 里用 `<jsp-config>` 兜底。若控制台日志乱码，是终端编码问题，
不影响页面。

**Q5：想清空成绩 / 重新演示**
删除 `C:\Users\<用户名>\.grade-manager` 目录后重启，会重新生成演示数据。

---

## 十、需求对照

| 需求 | 实现位置 |
| --- | --- |
| 教师录入成绩 | `/teacher/scores` → `ScoreServlet` + `ScoreService.saveBatch()`（批量、校验、可清空） |
| 学生查询成绩 | `/student/scores` → `MyScoreServlet`（按会话中的 studentId 取数，只能看本人） |
| 平均分 | SQL `AVG(score)`：`ScoreDao.courseStats() / classStats() / studentStats() / overview()` |
| 及格率 | SQL `SUM(CASE WHEN score >= 60 THEN 1 ELSE 0 END)`，页面用进度条 + 百分比展示 |
