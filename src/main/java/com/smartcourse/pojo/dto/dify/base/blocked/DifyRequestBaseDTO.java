package com.smartcourse.pojo.dto.dify.base.blocked;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class DifyRequestBaseDTO<T> {
    private String user;
    private T inputs;
    private String responseMode = "blocking";

    public DifyRequestBaseDTO(String user, T inputs) {
        this.user = user;
        this.inputs = inputs;
    }
}
