package com.smartcourse.controller;

import com.smartcourse.pojo.dto.teacher.assignment.TeacherGetAssignmentDTO;
import com.smartcourse.pojo.dto.teacher.assignment.TeacherPolishAssignmentDTO;
import com.smartcourse.pojo.vo.teacher.assignment.TeacherGetAssignmentVO;
import com.smartcourse.result.compat.Result;
import com.smartcourse.service.TeacherAssignmentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("teacher/assignment")
@CrossOrigin
public class TeacherAssignmentController {
    private final TeacherAssignmentService teacherAssignmentService;

    @GetMapping("/get")
    public Result<TeacherGetAssignmentVO> getAssignment(TeacherGetAssignmentDTO dto) {
        TeacherGetAssignmentVO res = teacherAssignmentService.getAssignment(dto);
        return Result.success(res);
    }

    @PostMapping("/polish")
    public Result<String> polishAssignment(@RequestBody TeacherPolishAssignmentDTO dto){
        String res = teacherAssignmentService.polishAssignment(dto);
        return Result.success(res);
    }

}
