package com.smartcourse.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum QuestionTypeEnum {
    SINGLE("single"),
    MULTIPLE("multiple"),
    FILL_BLANK("fill_blank"),
    SHORT_ANSWER("short_answer");
    private final String value;

    QuestionTypeEnum(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    // 添加根据 value 获取枚举的方法
    public static QuestionTypeEnum fromValue(String value) {
        for (QuestionTypeEnum type : QuestionTypeEnum.values()) {
            if (type.value.equals(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown question type: " + value);
    }
}
