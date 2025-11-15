package com.smartcourse.service;

import com.smartcourse.pojo.dto.*;
import com.smartcourse.pojo.vo.AssignmentFeedbackVO;
import com.smartcourse.pojo.vo.AssignmentIdVO;
import com.smartcourse.pojo.vo.AssignmentScoreIdVO;
import com.smartcourse.pojo.vo.AssignmentScoreUploadVO;
import org.springframework.web.multipart.MultipartFile;

public interface AssignmentService {
    /**
     * 新增作业
     * @param assignmentAddDTO 作业新增信息
     * @return 作业ID
     */
    AssignmentIdVO addAssignment(AssignmentAddDTO assignmentAddDTO);

    /**
     * 修改作业
     * @param assignmentUpdateDTO 作业修改信息
     * @return 作业ID
     */
    AssignmentIdVO updateAssignment(AssignmentUpdateDTO assignmentUpdateDTO);

    /**
     * 教师评价学生作业
     * @param teacherGradeDTO 评分信息
     * @return 作业评分ID
     */
    AssignmentScoreIdVO gradeAssignment(TeacherGradeDTO teacherGradeDTO);

    /**
     * 学生提交作业
     * @param studentSubmitMetaDTO 提交元数据（学生ID、作业ID、文件信息等）
     * @param file 提交的作业文件（二进制流）
     * @return 作业评分ID与文件访问地址
     */
    AssignmentScoreUploadVO submitAssignment(StudentSubmitMetaDTO studentSubmitMetaDTO, MultipartFile file);

    /**
     * 学生查看作业个性化评语与建议
     * @param studentId 学生ID
     * @param assignmentId 作业ID
     * @return 反馈信息（维度项、AI评语、教师评语）
     */
    AssignmentFeedbackVO getFeedback(Long studentId, Long assignmentId);
}