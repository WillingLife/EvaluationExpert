package com.smartcourse.pojo.dto.dify.base.streaming;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.Data;

/**
 * 定义一个流时间最上方内容
 */
@Data
public class DifyStreamEvent {
    // 对应 JSON 第一层的 "event" (例如 text_chunk, workflow_finished)
    private String event;

    // 对应 JSON 第一层的 "task_id"
    @JsonProperty("task_id")
    private String taskId;

    // 对应 JSON 第一层的 "data" 对象，里面的内容根据 event 变化
    // 使用 JsonNode 可以灵活处理 text_chunk 的 "text" 和 workflow_finished 的 "outputs"
    private JsonNode data;
}