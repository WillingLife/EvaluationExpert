package com.smartcourse.mapper;

import com.smartcourse.pojo.entity.ExamSection;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ExamSectionMapper {

    @Select("select * from evaluation_expert.exam_section where exam_id = #{examId}")
    List<ExamSection> getById(Long examId);
}
