package com.smartcourse.service;

import com.smartcourse.pojo.dto.teacher.assignment.AssignmentCompareGetDTO;
import com.smartcourse.pojo.dto.teacher.assignment.TeacherGetAssignmentDTO;
import com.smartcourse.pojo.vo.teacher.assignment.AssignmentDetectCompareVO;
import com.smartcourse.pojo.vo.teacher.assignment.TaskStudentListVO;
import com.smartcourse.pojo.dto.teacher.assignment.TeacherPolishAssignmentDTO;
import com.smartcourse.pojo.vo.teacher.assignment.TeacherAssignmentDetectVO;
import com.smartcourse.pojo.vo.teacher.assignment.TeacherGetAssignmentVO;

import java.util.List;

public interface TeacherAssignmentService {
    TeacherGetAssignmentVO getAssignment(TeacherGetAssignmentDTO dto);

    List<TaskStudentListVO> getStudents(Integer assignmentId);

    String polishAssignment(TeacherPolishAssignmentDTO dto);

    TeacherAssignmentDetectVO detectAssignment(Long assignmentId);

    AssignmentDetectCompareVO getAssignmentCompare(AssignmentCompareGetDTO dto);
}
