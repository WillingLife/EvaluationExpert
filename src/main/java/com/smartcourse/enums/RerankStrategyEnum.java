package com.smartcourse.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum RerankStrategyEnum {
    WEIGHTED_FUSION("wighted_fusion"),
    WRRF("wrrf");
    private final String value;

    RerankStrategyEnum(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    // 添加根据 value 获取枚举的方法
    public static RerankStrategyEnum fromValue(String value) {
        for (RerankStrategyEnum type : RerankStrategyEnum.values()) {
            if (type.value.equals(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown question type: " + value);
    }
}
