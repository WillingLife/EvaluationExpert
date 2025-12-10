package com.smartcourse.pojo.vo;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuestionQueryVO {
    private Long id;
    private Integer type;              // 题型：1单选、2多选、3填空、4简答
    private Long courseId;             // 课程ID（逻辑外键）
    private String stem;               // 题干/材料（可含富文本）
    private String analysis;           // 解析/答案说明
    private Integer difficulty;        // 难度（1~5）
    private Long teacherId;            // 教师
    private Integer active;            // 启用状态
    private Integer deleted;           // 逻辑删除标记
    private LocalDateTime createTime;  // 创建时间
    private LocalDateTime updateTime;  // 更新时间

    private JsonNode details;          // 根据传入的json字符串解析对象
}
