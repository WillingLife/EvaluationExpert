package com.smartcourse.pojo.vo.knowledge;

import lombok.Data;

import java.util.List;

@Data
public class CourseMapVO {
    private List<CourseNodeVO> nodes;
    private List<EdgeVO> edges;
}
