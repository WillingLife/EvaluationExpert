package com.smartcourse.pojo.vo.learn;

import lombok.Data;

import java.util.List;

@Data
public class StudentTaskVO {
    private List<TaskVO> records;
    private Integer total;
    private Integer size;
    private Integer current;
}
