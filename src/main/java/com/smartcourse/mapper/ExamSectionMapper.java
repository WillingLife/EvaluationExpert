package com.smartcourse.mapper;

import com.smartcourse.pojo.entity.ExamSection;
import com.smartcourse.pojo.vo.exam.SectionItemDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ExamSectionMapper {

    List<SectionItemDTO> getSectionItems(@Param("examId") Long examId);

    int insetSections(@Param("sections") List<ExamSection> sections);
}
