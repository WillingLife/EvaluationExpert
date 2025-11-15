package com.smartcourse.mapper;

import com.smartcourse.pojo.entity.ExamSection;
import com.smartcourse.pojo.vo.exam.SectionItemDTO;
import com.smartcourse.pojo.vo.exam.sql.SectionItemVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface ExamSectionMapper {

    List<SectionItemDTO> getSectionItems(@Param("examId") Long examId);
}
