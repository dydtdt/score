package com.grade.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 密码工具：统一使用 SHA-256 摘要存库，避免明文保存密码。
 */
public final class PasswordUtil {

    private PasswordUtil() {
    }

    public static String sha256(String raw) {
        if (raw == null) {
            return "";
        }
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] digest = md.digest(raw.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder(digest.length * 2);
            for (byte b : digest) {
                String hex = Integer.toHexString(b & 0xFF);
                if (hex.length() == 1) {
                    sb.append('0');
                }
                sb.append(hex);
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("当前 JVM 不支持 SHA-256", e);
        }
    }

    public static boolean matches(String raw, String storedHash) {
        return storedHash != null && storedHash.equalsIgnoreCase(sha256(raw));
    }
}
