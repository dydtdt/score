package com.grade.listener;

import com.grade.util.DbUtil;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 应用启动监听器：初始化数据库（建表 + 演示数据），并打印实际使用的连接串，方便排查问题。
 */
public class AppContextListener implements ServletContextListener {

    private static final Logger LOGGER = Logger.getLogger(AppContextListener.class.getName());

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        try {
            DbUtil.initIfNeeded();
            LOGGER.info("成绩管理系统启动完成，数据库连接：" + DbUtil.describe());
            sce.getServletContext().log("成绩管理系统启动完成，数据库连接：" + DbUtil.describe());
        } catch (RuntimeException e) {
            LOGGER.log(Level.SEVERE, "数据库初始化失败", e);
            sce.getServletContext().log("数据库初始化失败：" + e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        // H2 会在最后一个连接关闭时自行落盘，无需额外处理
    }
}
