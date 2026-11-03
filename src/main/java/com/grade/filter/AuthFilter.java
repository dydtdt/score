package com.grade.filter;

import com.grade.model.User;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * 登录拦截 + 角色控制。
 * <ul>
 *   <li>未登录访问受保护页面 → 跳转登录页；</li>
 *   <li>学生访问 /teacher/** 或教师访问 /student/** → 跳回自己的首页。</li>
 * </ul>
 */
public class AuthFilter implements Filter {

    /** 完全等值放行的路径（登录页、根路径、静态资源入口等） */
    private static final String[] PUBLIC_EXACT = {
            "/", "/index.jsp", "/login", "/logout", "/favicon.ico"
    };

    /** 前缀放行的路径（静态资源目录） */
    private static final String[] PUBLIC_PREFIX = {
            "/css/", "/js/", "/images/", "/fonts/"
    };

    @Override
    public void init(FilterConfig filterConfig) {
        // 无初始化参数
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;

        String path = req.getRequestURI().substring(req.getContextPath().length());
        if (path.isEmpty()) {
            path = "/";
        }

        if (isPublic(path)) {
            chain.doFilter(request, response);
            return;
        }

        HttpSession session = req.getSession(false);
        User user = session == null ? null : (User) session.getAttribute("loginUser");
        if (user == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        if (path.startsWith("/teacher/") && !user.isTeacher()) {
            resp.sendRedirect(req.getContextPath() + user.getHomePath());
            return;
        }
        if (path.startsWith("/student/") && !user.isStudent()) {
            resp.sendRedirect(req.getContextPath() + user.getHomePath());
            return;
        }

        chain.doFilter(request, response);
    }

    private boolean isPublic(String path) {
        for (String exact : PUBLIC_EXACT) {
            if (exact.equals(path)) {
                return true;
            }
        }
        for (String prefix : PUBLIC_PREFIX) {
            if (path.startsWith(prefix)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void destroy() {
        // 无需释放资源
    }
}
