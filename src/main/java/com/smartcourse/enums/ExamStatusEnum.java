package com.smartcourse.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum ExamStatusEnum {
    DRAFT("draft"),
    PUBLISHED("published"),
    PROGRESSING("progressing"),
    COMPLETED("completed"),
    GRADED("graded"),;
    private final String value;

    ExamStatusEnum(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    // 添加根据 value 获取枚举的方法
    public static ExamStatusEnum fromValue(String value) {
        for (ExamStatusEnum type : ExamStatusEnum.values()) {
            if (type.value.equals(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown question type: " + value);
    }
}
