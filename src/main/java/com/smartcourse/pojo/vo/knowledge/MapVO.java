package com.smartcourse.pojo.vo.knowledge;

import lombok.Data;

import java.util.List;

@Data
public class MapVO {
    List<NodeVO> nodes;
    List<EdgeVO> edges;
}
