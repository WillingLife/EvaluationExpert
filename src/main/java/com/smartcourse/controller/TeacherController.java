package com.smartcourse.controller;

import com.smartcourse.pojo.dto.*;
import com.smartcourse.pojo.vo.QuestionQueryVO;
import com.smartcourse.pojo.vo.AssignmentIdVO;
import com.smartcourse.pojo.vo.AssignmentScoreIdVO;
import com.smartcourse.pojo.vo.AssignmentListItemVO;
import com.smartcourse.service.AssignmentService;
import com.smartcourse.result.PageResult;
import com.smartcourse.result.Result;
import com.smartcourse.service.QuestionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/teacher")
@CrossOrigin
public class TeacherController {

    private final QuestionService questionService;
    private final AssignmentService assignmentService;

    /**
     * 教师新增题目
     * @param questionAddDTO 题目信息
     * @return 操作结果
     */
    @PostMapping("/question/add")
    public Result addQuestion(@RequestBody QuestionAddDTO questionAddDTO) {
        log.info("教师新增题目：{}", questionAddDTO);
        questionService.addQuestion(questionAddDTO);

        return Result.success();
    }

    /**
     * 分页查询题目数据
     * @param questionQueryDTO 查询条件
     * @return 题目数据
     */
    @GetMapping("/question/query")
    public Result page(QuestionQueryDTO questionQueryDTO) {
        log.info("教师查看题目：{}", questionQueryDTO);
        PageResult<QuestionQueryVO> pageResult = questionService.page(questionQueryDTO);

        return Result.success(pageResult);
    }

    /**
     * 根据ID查询题目详情
     * @param id 题目ID
     * @return 题目详情
     */
    @GetMapping("/question/{id}")
    public Result get(@PathVariable Long id) {
        log.info("教师查看题目详情：{}", id);
        QuestionQueryVO questionVO = questionService.get(id);

        return Result.success(questionVO);
    }

    /**
     * 修改题目
     * @param questionUpdateDTO 题目修改信息
     * @return 操作结果
     */
    @PutMapping("/question/update")
    public Result updateQuestion(@RequestBody QuestionUpdateDTO questionUpdateDTO) {
        log.info("教师修改题目：{}", questionUpdateDTO);
        questionService.update(questionUpdateDTO);

        return Result.success();
    }

    /**
     * 根据ID删除题目（逻辑删除）
     * @param id 题目ID
     * @return 操作结果
     */
    @DeleteMapping("/question/delete")
    public Result deleteQuestion(@RequestParam Long id) {
        log.info("教师删除题目：{}", id);
        questionService.deleteQuestion(id);

        return Result.success();
    }

    /**
     * 教师新增作业
     * @param assignmentAddDTO 作业新增信息
     * @return 作业ID
     */
    @PostMapping("/assignment/add")
    public Result addAssignment(@RequestBody AssignmentAddDTO assignmentAddDTO) {
        log.info("教师新增作业：{}", assignmentAddDTO);
        AssignmentIdVO assignmentIdVO = assignmentService.addAssignment(assignmentAddDTO);

        return Result.success(assignmentIdVO);
    }

    /**
     * 教师修改作业
     * @param assignmentUpdateDTO 作业修改信息
     * @return 作业ID
     */
    @PostMapping("/assignment/update")
    public Result updateAssignment(@RequestBody AssignmentUpdateDTO assignmentUpdateDTO) {
        log.info("教师修改作业：{}", assignmentUpdateDTO);
        AssignmentIdVO assignmentIdVO = assignmentService.updateAssignment(assignmentUpdateDTO);

        return Result.success(assignmentIdVO);
    }

    /**
     * 教师评价学生作业
     * @param teacherGradeDTO 评分信息
     * @return 作业评分ID
     */
    @PostMapping("/assignment/grade")
    public Result gradeAssignment(@RequestBody TeacherGradeDTO teacherGradeDTO) {
        log.info("教师评价作业：{}", teacherGradeDTO);
        AssignmentScoreIdVO assignmentScoreIdVO = assignmentService.gradeAssignment(teacherGradeDTO);

        return Result.success(assignmentScoreIdVO);
    }

    /**
     * 教师查询某课程的作业列表
     * @param dto 查询条件（teacher_id, course_id）
     * @return 作业列表
     */
    @GetMapping("/assignment/list")
    public Result listAssignments(TeacherAssignmentListDTO dto) {
        log.info("教师查询作业列表：{}", dto);
        java.util.List<AssignmentListItemVO> items = assignmentService.listTeacherAssignments(dto);
        return Result.success(items);
    }

    /**
     * 教师删除作业（逻辑删除）
     * @param dto 删除条件（teacher_id, course_id, assignment_id）
     * @return 操作结果
     */
    @DeleteMapping("/assignment/delete")
    public Result deleteAssignment(@RequestBody AssignmentDeleteDTO dto) {
        log.info("教师删除作业：{}", dto);
        assignmentService.deleteAssignment(dto);
        return Result.success();
    }
}
