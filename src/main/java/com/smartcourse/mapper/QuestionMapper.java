package com.smartcourse.mapper;

import com.smartcourse.pojo.entity.Question;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface QuestionMapper {
    /**
     * 新增题目信息
     * @param question 题目信息
     */
    void insert(Question question);
}
