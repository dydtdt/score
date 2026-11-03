package com.grade.service;

import com.grade.dao.UserDao;
import com.grade.model.User;
import com.grade.util.PasswordUtil;

/**
 * 登录认证。
 */
public class AuthService {

    private final UserDao userDao = new UserDao();

    /**
     * @return 认证通过返回用户对象，用户名不存在或密码错误返回 null
     */
    public User login(String username, String password) {
        if (username == null || username.trim().isEmpty() || password == null || password.isEmpty()) {
            return null;
        }
        User user = userDao.findByUsername(username.trim());
        if (user == null) {
            return null;
        }
        return PasswordUtil.matches(password, user.getPassword()) ? user : null;
    }
}
