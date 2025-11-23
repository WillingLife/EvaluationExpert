package com.smartcourse.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartcourse.pojo.dto.dify.base.streaming.DifyStreamEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.SynchronousSink;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class DifyStreamHelper {
    private final ObjectMapper objectMapper;

    /**
     * 通用方法：解析流并过滤指定事件
     *
     * @param rawStream    Declarative Client 返回的原始 String 流
     * @param targetEvents 你想要保留的事件类型，例如 "text_chunk", "workflow_finished"
     * @return 过滤后的对象流
     */
    public Flux<DifyStreamEvent> filterEvents(Flux<String> rawStream, String... targetEvents) {
        Set<String> eventSet = Arrays.stream(targetEvents).collect(Collectors.toSet());

        return rawStream
                .handle((String json, SynchronousSink<DifyStreamEvent> sink) -> {
                    try {
                        DifyStreamEvent event = objectMapper.readValue(json, DifyStreamEvent.class);
                        if (event != null) {
                            sink.next(event);
                        }
                    } catch (Exception e) {
                        // 忽略错误
                        log.warn("JSON解析失败，跳过该帧: {}", json, e);
                    }
                })
                // 现在这里的 event 就能被正确识别为 DifyStreamEvent 了
                .filter(event -> {
                    if (event.getEvent() == null) return false;
                    return eventSet.isEmpty() || eventSet.contains(event.getEvent());
                });
    }
}
