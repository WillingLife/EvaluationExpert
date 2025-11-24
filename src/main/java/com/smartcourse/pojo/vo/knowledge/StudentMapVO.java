package com.smartcourse.pojo.vo.knowledge;

import lombok.Data;

import java.util.List;

@Data
public class StudentMapVO {
    private List<ExamNodeVO> nodes;
    private List<EdgeVO> edges;
}
