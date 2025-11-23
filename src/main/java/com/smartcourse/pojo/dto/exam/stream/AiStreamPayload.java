package com.smartcourse.pojo.dto.exam.stream;

import dev.langchain4j.service.spring.AiService;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AiStreamPayload {
    private String event;
    private Object data;
}
