package com.smartcourse.utils;

import com.smartcourse.pojo.entity.QuestionOption;

import java.util.List;

public class TextExtractUtils {
    public static String extractOptions(List<QuestionOption> options) {
        StringBuilder result = new StringBuilder();
        for (QuestionOption option : options) {
            result.append(option.getContent()).append("\n");
        }
        if (!result.isEmpty()) {
            result.setLength(result.length() - 1); // 去掉最后一个换行符
        }
        return result.toString();
    }
}
