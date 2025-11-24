package com.smartcourse.pojo.vo.knowledge;

import lombok.Data;

import java.util.List;

@Data
public class ClassMapVO {
    private List<ClassNodeVO> nodes;
    private List<EdgeVO> edges;
}
