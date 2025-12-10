package com.smartcourse.pojo.vo.exam;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class SectionItemDTO {
    // ExamSection 字段
    private Long id;
    private Long examId;
    private String title;
    private String questionType;
    private String description;
    private BigDecimal choiceScore;
    private Integer orderNo;
    private String note;
    private BigDecimal choiceNegativeScore;
    private String multipleStrategy;
    private String multipleStrategyConf;
    private Long creator;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private Boolean deleted;

    // ExamItem 字段
    private Long questionId;
}