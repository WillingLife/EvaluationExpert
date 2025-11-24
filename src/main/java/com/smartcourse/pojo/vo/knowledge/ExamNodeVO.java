package com.smartcourse.pojo.vo.knowledge;

import lombok.Data;

@Data
public class ExamNodeVO {
    private Long id;
    private Double allScore;
    private Double getScore;
    private Double singleAmount;
    private Double multipleAmount;
    private Double fillBlankAmount;
    private Double shortAnswerAmount;
}
