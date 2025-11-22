package com.smartcourse.utils;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.smartcourse.pojo.dto.ElasticSearchKnowledgeDTO;
import com.smartcourse.pojo.dto.question.QuestionElasticSearchQueryDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.DigestUtils;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Slf4j
public class Md5Calculator {
    /**
     * 1. 定义一个 Mix-in 接口（通常定义为 private static 即可）
     * 这里的字段名必须和原 DTO 中的字段名一致
     */
    abstract static class QuestionQueryMD5MixIn {
        @JsonIgnore // 只在这个 MixIn 里标记忽略，不影响原 DTO
        private Integer pageNumber;
    }

    // 使用 JsonMapper.builder() 构建不可变的 ObjectMapper
    private static final ObjectMapper HASH_MAPPER = JsonMapper.builder()
            // 1. 【关键】开启字段按字母顺序排序 (解决 {"a":1, "b":2} vs {"b":2, "a":1} 问题)
            .enable(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY)
            // 2. 设置蛇形命名 (保持和你类的注解一致)
            .propertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE)
            // 3. 忽略 null 值 (可选：通常用于去重时，认为 null 和不传是一样的)
            .serializationInclusion(JsonInclude.Include.NON_NULL)
            // 4. 禁用日期转时间戳 (保证日期格式的可读性和一致性，看业务需求)
            .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
            .addMixIn(QuestionElasticSearchQueryDTO.class, QuestionQueryMD5MixIn.class)
            .build();

    /**
     * 计算查询对象的 MD5 摘要
     */
    public static String calculateQuestionESDtoMd5(QuestionElasticSearchQueryDTO dto) {
        if (dto == null) {
            return null;
        }
        try {
            // 步骤 1: 手动处理 List 内部顺序 (Jackson 无法自动根据 ID 排序 List)
            if (dto.getKnowledge() != null && !dto.getKnowledge().isEmpty()) {
                // 浅拷贝列表，防止修改原对象的 List 顺序影响其他逻辑
                List<ElasticSearchKnowledgeDTO> sortedList = new ArrayList<>(dto.getKnowledge());

                // 根据 knowledgeId 升序排列
                sortedList.sort(Comparator.comparing(
                        ElasticSearchKnowledgeDTO::getKnowledgeId,
                        Comparator.nullsLast(Comparator.naturalOrder())
                ));

                // 临时替换 DTO 中的 List (注意：这里修改了 DTO 引用，
                // 如果该 DTO 后续还要用于非 Hash 逻辑且对顺序敏感，建议先深拷贝整个 DTO)
                dto.setKnowledge(sortedList);
            }

            // 步骤 2: 序列化成标准化的 JSON 字符串
            String jsonString = HASH_MAPPER.writeValueAsString(dto);

            // 步骤 3: 计算 MD5
            return DigestUtils.md5DigestAsHex(jsonString.getBytes(StandardCharsets.UTF_8));

        } catch (JsonProcessingException e) {
            log.error("MD5 fingerprint generation failed", e);
            // 这里可以选择抛异常，或者返回 null/空串，视业务容错要求而定
            throw new RuntimeException("Failed to generate query hash", e);
        }
    }
}
