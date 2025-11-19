package com.smartcourse.mapper;

import com.smartcourse.pojo.entity.ExamSection;
import com.smartcourse.pojo.vo.exam.SectionItemDTO;
import com.smartcourse.pojo.vo.exam.StudentScoreSectionVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ExamSectionMapper {

    List<SectionItemDTO> getSectionItems(@Param("examId") Long examId);

    int insetSections(@Param("sections") List<ExamSection> sections);

    List<StudentScoreSectionVO> getById(Long examId);

    @Select("select multiple_strategy from evaluation_expert.exam_section where id = #{sectionId}")
    String get(Long sectionId);

    @Select("select multiple_strategy_conf from evaluation_expert.exam_section where id = #{sectionId}")
    String getConf(Long sectionId);
}
