package com.smartcourse.controller;

import com.smartcourse.pojo.dto.StudentSubmitMetaDTO;
import com.smartcourse.pojo.dto.StudentAssignmentListDTO;
import com.smartcourse.pojo.vo.AssignmentFeedbackVO;
import com.smartcourse.pojo.vo.AssignmentScoreUploadVO;
import com.smartcourse.pojo.vo.AssignmentListItemVO;
import com.smartcourse.pojo.vo.AssignmentVO;
import com.smartcourse.result.Result;
import com.smartcourse.service.AssignmentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/student")
@CrossOrigin
public class StudentController {

    private final AssignmentService assignmentService;

    /**
     * 学生提交作业
     *
     * @param studentSubmitMetaDTO 提交元数据（学生ID、作业ID等）
     * @param file 提交的作业文件（二进制流）
     * @return 作业评分ID与文件访问地址
     */
    @PostMapping("/assignment/submit")
    public Result submit(@RequestPart("meta") StudentSubmitMetaDTO studentSubmitMetaDTO,
                         @RequestPart("file") MultipartFile file) {
        log.info("学生提交作业 meta：{} file:{}", studentSubmitMetaDTO, file.getOriginalFilename());
        AssignmentScoreUploadVO assignmentScoreUploadVO = assignmentService.submitAssignment(studentSubmitMetaDTO, file);
        return Result.success(assignmentScoreUploadVO);
    }

    /**
     * 学生查看作业个性化评语与建议
     *
     * @param studentId    学生ID（请求头）
     * @param assignmentId 作业ID（请求头）
     * @return 评语维度、AI评语与教师评语
     */
    @GetMapping("/assignment/feedback")
    public Result feedback(@RequestHeader("student_id") Long studentId,
                           @RequestHeader("assignment_id") Long assignmentId) {
        log.info("学生查看作业评语 studentId:{} assignmentId:{}", studentId, assignmentId);
        AssignmentFeedbackVO feedbackVO = assignmentService.getFeedback(studentId, assignmentId);
        return Result.success(feedbackVO);
    }

    /**
     * 学生查询某课程的作业列表
     *
     * @param dto 查询条件（student_id, course_id）
     * @return 作业列表
     */
    @GetMapping("/assignment/list")
    public Result listAssignments(StudentAssignmentListDTO dto) {
        log.info("学生查询作业列表：{}", dto);
        java.util.List<AssignmentListItemVO> items = assignmentService.listStudentAssignments(dto);
        return Result.success(items);
    }


    @GetMapping("/assignment")
    public Result getAssignment(@RequestParam("assignment_id") Long assignmentId) {
        AssignmentVO assignmentVO = assignmentService.getAssignment(assignmentId);
        return Result.success(assignmentVO);
    }
}