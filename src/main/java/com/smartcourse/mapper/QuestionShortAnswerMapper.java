package com.smartcourse.mapper;

import com.smartcourse.pojo.entity.QuestionShortAnswer;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface QuestionShortAnswerMapper {
    /**
     * 插入简答题答案与评分细则
     * @param answer 答案信息
     */
    void insert(QuestionShortAnswer answer);
}