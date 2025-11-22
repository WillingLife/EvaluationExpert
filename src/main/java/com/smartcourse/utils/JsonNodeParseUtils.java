package com.smartcourse.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartcourse.pojo.vo.dify.DifyExamGenQueryVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JsonNodeParseUtils {
    private final ObjectMapper objectMapper;

    DifyExamGenQueryVO difyStreamNodeToExamGenQueryVO(JsonNode jsonNode) {
        JsonNode outputNode = jsonNode.path("output");
        if (outputNode == null) {
            return null;
        }
        try {
            return objectMapper.treeToValue(outputNode, DifyExamGenQueryVO.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

    }
}
