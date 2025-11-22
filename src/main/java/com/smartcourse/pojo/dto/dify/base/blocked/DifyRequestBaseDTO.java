package com.smartcourse.pojo.dto.dify.base.blocked;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DifyRequestBaseDTO<T> {
    private String user;
    private T inputs;
    private String responseMode = "blocking";

    public DifyRequestBaseDTO(String user, T inputs) {
        this.user = user;
        this.inputs = inputs;
    }
}
