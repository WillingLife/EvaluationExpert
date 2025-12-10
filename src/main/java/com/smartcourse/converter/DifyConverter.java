package com.smartcourse.converter;

import com.fasterxml.jackson.databind.JsonNode;
import com.smartcourse.pojo.dto.dify.DifyPolishAssignmentDTO;
import com.smartcourse.pojo.dto.teacher.assignment.TeacherPolishAssignmentDTO;
import com.smartcourse.pojo.vo.dify.DifyExamGenQueryVO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface DifyConverter {

    @Mapping(target = "userInput",source = "assignment")
    DifyPolishAssignmentDTO polishAssignmentDTOToDifyDTO(TeacherPolishAssignmentDTO dto);
}
