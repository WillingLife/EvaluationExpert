package com.smartcourse.service;

import com.smartcourse.pojo.dto.teacher.assignment.TeacherGetAssignmentDTO;
import com.smartcourse.pojo.dto.teacher.assignment.TeacherPolishAssignmentDTO;
import com.smartcourse.pojo.vo.teacher.assignment.TeacherGetAssignmentVO;

public interface TeacherAssignmentService {
    TeacherGetAssignmentVO getAssignment(TeacherGetAssignmentDTO dto);

    String polishAssignment(TeacherPolishAssignmentDTO dto);
}
