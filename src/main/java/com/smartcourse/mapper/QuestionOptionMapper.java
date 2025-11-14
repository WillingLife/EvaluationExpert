package com.smartcourse.mapper;

import com.smartcourse.pojo.entity.QuestionOption;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface QuestionOptionMapper {

    /**
     * 批量插入选择题选项
     * @param options 选择题选项
     */
    void insertBatch(List<QuestionOption> options);
}