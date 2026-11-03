package com.grade.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

/**
 * 数据库工具类。
 * <p>
 * 负责三件事：
 * <ol>
 *   <li>从 classpath 的 db.properties 读取连接参数，并把 ${dataDir} 占位符替换为真实目录；</li>
 *   <li>对外提供 {@link #getConnection()}；</li>
 *   <li>应用启动时按需执行建表脚本与演示数据脚本（幂等，不会重复导入）。</li>
 * </ol>
 * 默认使用 H2 嵌入式数据库，因此整个项目无需安装任何数据库服务器即可运行；
 * 若把 db.properties 改成 MySQL，本类逻辑完全不用改。
 */
public final class DbUtil {

    private static final String CONFIG_FILE = "db.properties";
    private static final String SCHEMA_SCRIPT = "db/schema.sql";
    private static final String DATA_SCRIPT = "db/data.sql";

    private static String url;
    private static String username;
    private static String password;
    private static String driver;
    private static boolean autoInit;

    static {
        try {
            loadConfig();
        } catch (Exception e) {
            throw new ExceptionInInitializerError("数据库配置加载失败：" + e.getMessage());
        }
    }

    private DbUtil() {
    }

    private static void loadConfig() throws IOException, ClassNotFoundException {
        Properties props = new Properties();
        try (InputStream in = DbUtil.class.getClassLoader().getResourceAsStream(CONFIG_FILE)) {
            if (in == null) {
                throw new IOException("classpath 根目录下找不到 " + CONFIG_FILE);
            }
            props.load(new InputStreamReader(in, StandardCharsets.UTF_8));
        }
        driver = props.getProperty("db.driver", "org.h2.Driver").trim();
        url = resolveDataDir(props.getProperty("db.url", "").trim());
        username = props.getProperty("db.username", "").trim();
        password = props.getProperty("db.password", "").trim();
        autoInit = Boolean.parseBoolean(props.getProperty("db.autoInit", "true").trim());
        Class.forName(driver);
    }

    /**
     * 把配置里的 ${dataDir} 替换成真实目录（默认 ~/.grade-manager），H2 的数据文件就落在这里。
     */
    private static String resolveDataDir(String raw) {
        if (raw == null || !raw.contains("${dataDir}")) {
            return raw;
        }
        String dir = System.getProperty("grade.data.dir");
        if (dir == null || dir.trim().isEmpty()) {
            dir = System.getProperty("user.home") + File.separator + ".grade-manager";
        }
        File dataDir = new File(dir);
        if (!dataDir.exists() && !dataDir.mkdirs()) {
            throw new IllegalStateException("无法创建数据目录：" + dataDir.getAbsolutePath());
        }
        return raw.replace("${dataDir}", dataDir.getAbsolutePath().replace('\\', '/'));
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, username, password);
    }

    /**
     * 应用启动时调用：表不存在则建表；表为空则导入演示数据。
     * 重复启动不会产生任何副作用。
     */
    public static synchronized void initIfNeeded() {
        if (!autoInit) {
            return;
        }
        if (!tableExists("t_student")) {
            runScript(SCHEMA_SCRIPT);
        }
        if (countRows("t_student") == 0) {
            runScript(DATA_SCRIPT);
        }
    }

    public static String describe() {
        return driver + " -> " + url;
    }

    private static boolean tableExists(String table) {
        try (Connection conn = getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM " + table)) {
            return rs.next();
        } catch (SQLException e) {
            return false;
        }
    }

    private static long countRows(String table) {
        try (Connection conn = getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM " + table)) {
            return rs.next() ? rs.getLong(1) : 0L;
        } catch (SQLException e) {
            throw new DataAccessException("统计表行数失败：" + table, e);
        }
    }

    /**
     * 逐个分号执行 SQL 脚本（自动跳过 -- 注释行）。
     */
    private static void runScript(String resource) {
        String script = readResource(resource);
        StringBuilder clean = new StringBuilder();
        for (String line : script.split("\\r?\\n")) {
            String trimmed = line.trim();
            if (trimmed.isEmpty() || trimmed.startsWith("--")) {
                continue;
            }
            clean.append(line).append('\n');
        }
        try (Connection conn = getConnection(); Statement st = conn.createStatement()) {
            for (String sql : clean.toString().split(";")) {
                if (sql.trim().isEmpty()) {
                    continue;
                }
                st.execute(sql);
            }
        } catch (SQLException e) {
            throw new DataAccessException("执行初始化脚本失败：" + resource, e);
        }
    }

    private static String readResource(String resource) {
        try (InputStream in = DbUtil.class.getClassLoader().getResourceAsStream(resource)) {
            if (in == null) {
                throw new IllegalStateException("找不到脚本文件：" + resource);
            }
            java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
            byte[] buf = new byte[4096];
            int len;
            while ((len = in.read(buf)) != -1) {
                out.write(buf, 0, len);
            }
            return new String(out.toByteArray(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new IllegalStateException("读取脚本文件失败：" + resource, e);
        }
    }
}
