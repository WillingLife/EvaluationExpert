package com.smartcourse.pojo.dto.dify;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DifyRequestBaseDTO<T> {
    private String user;
    private T inputs;

}
