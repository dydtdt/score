package com.grade.web;

import com.grade.model.User;
import com.grade.service.AuthService;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * 登录 / 展示登录页。
 */
public class LoginServlet extends BaseServlet {

    private static final long serialVersionUID = 1L;

    private final AuthService authService = new AuthService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        User user = currentUser(req);
        if (user != null) {
            redirect(req, resp, user.getHomePath());
            return;
        }
        forward(req, resp, "login.jsp");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String username = trimParam(req, "username");
        String password = req.getParameter("password") == null ? "" : req.getParameter("password");

        User user = authService.login(username, password);
        if (user == null) {
            req.setAttribute("error", "用户名或密码不正确，请重新输入");
            req.setAttribute("username", username);
            forward(req, resp, "login.jsp");
            return;
        }

        // 重新建立会话，避免会话固定攻击
        HttpSession oldSession = req.getSession(false);
        if (oldSession != null) {
            oldSession.invalidate();
        }
        HttpSession session = req.getSession(true);
        session.setAttribute(SESSION_USER, user);
        session.setMaxInactiveInterval(60 * 60);
        redirect(req, resp, user.getHomePath());
    }
}
