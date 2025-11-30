package com.smartcourse.controller;

import com.smartcourse.pojo.dto.knowledge.*;
import com.smartcourse.pojo.vo.knowledge.*;
import com.smartcourse.result.compat.Result;
import com.smartcourse.service.KnowledgeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/teacher/knowledge")
@CrossOrigin
public class KnowledgeController {
    private final KnowledgeService knowledgeService;

    @PostMapping("/exam/student-map")
    public Result<StudentMapVO> studentMap(@RequestBody StudentMapDTO studentMapDTO) {
        return Result.success(knowledgeService.getStudentMap(studentMapDTO));
    }

    @PostMapping("/exam/class-map")
    public Result<ClassMapVO> classMap(@RequestBody ClassMapDTO classMapDTO) {
        return Result.success(knowledgeService.getClassMap(classMapDTO));
    }

    @GetMapping("/exam/course-map")
    public Result<CourseMapVO> courseMap(@RequestParam("exam_id") Long examId) {
        return Result.success(knowledgeService.getCourseMap(examId));
    }

    @GetMapping("course/map")
    public Result<MapVO> getCourseMap(@RequestParam("course_id") Long courseId) {
        return Result.success(knowledgeService.getMap(courseId));
    }

    @PostMapping("course")
    public Result<List<CourseMap>> getCourse(@RequestBody CourseDTO courseDTO) {
        return Result.success(knowledgeService.getCourse(courseDTO));
    }

    @PostMapping("class")
    public Result<List<ClassMap>> getClass(@RequestBody ClassDTO classDTO) {
        return Result.success(knowledgeService.getClazz(classDTO));
    }

    @PostMapping("student")
    public Result<List<StudentMap>> getStudent(@RequestBody StudentDTO studentDTO) {
        return Result.success(knowledgeService.getStudent(studentDTO));
    }
}
