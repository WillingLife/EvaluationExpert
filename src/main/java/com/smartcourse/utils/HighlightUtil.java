package com.smartcourse.utils;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HighlightUtil {

    // 预编译正则，提高性能
    private static final Pattern EM_TAG_PATTERN = Pattern.compile("</?em>");
    private static final Pattern ELLIPSIS_PATTERN = Pattern.compile("\\.\\.\\.");

    /**
     * 将 ES 高亮片段替换到原文中
     *
     * @param source 原文
     * @param highlights 带 <em> 的高亮片段
     * @return 替换高亮后的文本
     */
    public static String applyHighlight(String source, List<String> highlights) {
        if (source == null || highlights == null || highlights.isEmpty()) {
            return source;
        }

        String result = source;

        // 使用 StringBuilder 避免频繁创建字符串
        for (String fragment : highlights) {
            String plain = removeEmTags(fragment);
            plain = removeEllipsis(plain).trim();

            if (plain.isEmpty()) {
                continue;
            }

            // 将 plain 替换为 fragment（带 <em>）
            result = replaceSafe(result, plain, fragment);
        }

        return result;
    }

    /**
     * 去除 <em> 标签
     */
    private static String removeEmTags(String text) {
        return EM_TAG_PATTERN.matcher(text).replaceAll("");
    }

    /**
     * 去除 "..." 片段
     */
    private static String removeEllipsis(String text) {
        return ELLIPSIS_PATTERN.matcher(text).replaceAll("");
    }

    /**
     * 安全替换字符串（用正则 Quote 处理特殊字符）
     */
    private static String replaceSafe(String source, String oldStr, String highlightStr) {
        if (!source.contains(oldStr)) {
            return source;
        }

        return source.replaceAll(
                Pattern.quote(oldStr),
                Matcher.quoteReplacement(highlightStr)
        );
    }
}