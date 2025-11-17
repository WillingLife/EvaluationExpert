package com.smartcourse.mapper;

import com.smartcourse.pojo.entity.ExamSection;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ExamItemMapper {

    int insertExamItemsByExamSections(@Param("sections") List<ExamSection> sections);
}
