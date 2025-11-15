package com.smartcourse.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum QuestionTypeEnum {
    SINGLE("single"),
    MULTIPLE("multiple"),
    FILL_BLANK("fill_blank"),
    SHORT_ANSWER("short_answer"),;
    private final String value;

    QuestionTypeEnum(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }
}
