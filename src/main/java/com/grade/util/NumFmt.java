package com.grade.util;

import java.util.Locale;

/**
 * 数字显示格式化工具。
 * <p>页面上的「平均分」「及格率」「学分」等数字统一在这里格式化，
 * 避免依赖 JSP 标签库的本地化格式（不同容器/语言环境下表现不一致）。
 */
public final class NumFmt {

    private NumFmt() {
    }

    /** 保留一位小数，例如 72.85333 -> "72.9"，60 -> "60.0" */
    public static String one(double value) {
        return String.format(Locale.US, "%.1f", Double.valueOf(value));
    }

    /** 最多两位小数，且去掉多余的 0，例如 96.0 -> "96"，87.50 -> "87.5" */
    public static String trim(double value) {
        if (value == Math.rint(value)) {
            return String.valueOf((long) value);
        }
        return java.math.BigDecimal.valueOf(value).stripTrailingZeros().toPlainString();
    }
}
