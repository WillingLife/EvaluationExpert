package com.smartcourse.mapper;

import com.smartcourse.pojo.entity.QuestionFillBlank;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface QuestionFillBlankMapper {

    /**
     * 批量插入填空题表
     * @param blanks 填空题信息
     */
    void insertBatch(List<QuestionFillBlank> blanks);

    List<QuestionFillBlank> selectByQuestionId(Long questionId);

    void deleteByQuestionId(Long questionId);

    List<QuestionFillBlank> selectByQuestionIds(List<Long> questionIds);
}