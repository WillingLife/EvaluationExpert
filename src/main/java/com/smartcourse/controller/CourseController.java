package com.smartcourse.controller;

import com.smartcourse.pojo.vo.course.*;
import com.smartcourse.result.compat.Result;
import com.smartcourse.service.CourseService;
import com.smartcourse.utils.AliyunOSSOperator;
import com.smartcourse.utils.DocumentZipCreator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/course")
@CrossOrigin
public class CourseController {
    @Autowired
    private CourseService courseService;

    @Autowired
    AliyunOSSOperator aliyunOSSOperator;

    @GetMapping("/teacher/list")
    public Result<List<TeacherCourseVO>> getCourseList(@RequestParam("teacher_id") Long teacherId) {
        List<TeacherCourseVO> teacherCourseVOList = courseService.getCourseList(teacherId);
        return Result.success(teacherCourseVOList);
    }

    @GetMapping("/student/exam/list")
    public Result<List<StudentCourseVO>> getCourseExam(@RequestParam("student_id") Long studentId) {
        List<StudentCourseVO> teacherCourseVOList = courseService.getCourseExam(studentId);
        return Result.success(teacherCourseVOList);
    }

    @GetMapping("/student/task/list")
    public Result<List<StudentCourseTaskVO>> getCourseTask(@RequestParam("student_id") Long studentId) {
        List<StudentCourseTaskVO> teacherCourseVOList = courseService.getCourseTask(studentId);
        return Result.success(teacherCourseVOList);
    }

    @GetMapping("/teacher/listAll")
    public Result<List<CourseVO>> getListAll(@RequestParam("teacher_id") Long teacherId) {
        List<CourseVO> list = courseService.getListAll(teacherId);
        return Result.success(list);
    }

    @GetMapping("/teacher/examList")
    public Result<List<ExamListVO>> getExamList(@RequestParam("course_id") Long courseId) {
        List<ExamListVO> list = courseService.getExamList(courseId);
        return Result.success(list);
    }

    @GetMapping("/teacher/exam")
    public Result<List<ExamVO>> getExam(@RequestParam("course_id") Long courseId) {
        List<ExamVO> list = courseService.getExam(courseId);
        return Result.success(list);
    }

    @GetMapping("/teacher/exam/students")
    public Result<List<ExamStudentVO>> getStudents(@RequestParam("exam_id") Long examId, @RequestParam("class_id") Long classId) {
        List<ExamStudentVO> list = courseService.getStudents(examId, classId);
        return Result.success(list);
    }

    @CrossOrigin(origins = "http://localhost:5173")
    @GetMapping("/teacher/task/file/zip")
    public ResponseEntity<Resource> getTaskFile(@RequestParam("assignment_id") Long assignmentId) {
        try {
            List<String> list = courseService.getTaskFile(assignmentId);
            List<String> name = courseService.getTaskName(assignmentId);
            List<String> fileList = new ArrayList<>();
            for (String file : list) {
                fileList.add(aliyunOSSOperator.getUrl(file));
            }
            // 创建 ZIP
            DocumentZipCreator zipCreator = new DocumentZipCreator();
            byte[] zipData = zipCreator.createZipFromDocumentUrls(fileList, name);

            ByteArrayResource resource = new ByteArrayResource(zipData);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=documents.zip")
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .contentLength(zipData.length)
                    .body(resource);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
