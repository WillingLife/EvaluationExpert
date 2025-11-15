package com.smartcourse.mapper;

import com.smartcourse.pojo.entity.Exam;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface ExamMapper {

    @Select("select * from evaluation_expert.exam where id = #{examId}")
    Exam getById(Long examId);
}
