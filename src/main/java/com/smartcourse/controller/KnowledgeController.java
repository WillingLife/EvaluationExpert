package com.smartcourse.controller;

import com.smartcourse.pojo.dto.knowledge.ClassMapDTO;
import com.smartcourse.pojo.dto.knowledge.StudentMapDTO;
import com.smartcourse.pojo.vo.knowledge.ClassMapVO;
import com.smartcourse.pojo.vo.knowledge.MapVO;
import com.smartcourse.pojo.vo.knowledge.StudentMapVO;
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

    @GetMapping("course/map")
    public Result<MapVO> getCourseMap(@RequestParam("course_id") Long courseId) {
        return Result.success(knowledgeService.getMap(courseId));
    }
}
