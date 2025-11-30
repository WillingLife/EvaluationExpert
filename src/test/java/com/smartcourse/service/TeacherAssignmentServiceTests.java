package com.smartcourse.service;

import com.smartcourse.pojo.vo.teacher.assignment.TeacherAssignmentDetectVO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class TeacherAssignmentServiceTests {
    @Autowired
    private TeacherAssignmentService teacherAssignmentService;
    @Test
    public void teacherAssignmentServiceTest(){
        TeacherAssignmentDetectVO teacherAssignmentDetectVO = teacherAssignmentService.detectAssignment(8L);
        System.out.println(teacherAssignmentDetectVO);

    }
}
