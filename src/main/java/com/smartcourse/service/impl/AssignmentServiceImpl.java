package com.smartcourse.service.impl;

import com.smartcourse.mapper.*;
import com.smartcourse.pojo.vo.*;
import com.smartcourse.utils.AliyunOSSOperator;
import com.smartcourse.constant.MessageConstant;
import com.smartcourse.constant.StatusConstant;
import com.smartcourse.pojo.dto.*;
import com.smartcourse.pojo.entity.Assignment;
import com.smartcourse.pojo.entity.AssignmentRemark;
import com.smartcourse.pojo.entity.AssignmentScore;
import com.smartcourse.pojo.entity.AssignmentDimensionRemark;
import com.smartcourse.service.AssignmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AssignmentServiceImpl implements AssignmentService {
    private final AssignmentMapper assignmentMapper;
    private final AssignmentScoreMapper assignmentScoreMapper;
    private final AssignmentRemarkMapper assignmentRemarkMapper;
    private final AssignmentDimensionRemarkMapper assignmentDimensionRemarkMapper;
    private final AliyunOSSOperator aliyunOSSOperator;

    /**
     * 新增作业
     *
     * @param assignmentAddDTO 作业新增信息
     * @return 作业ID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public AssignmentIdVO addAssignment(AssignmentAddDTO assignmentAddDTO) {
        // 构造作业实体
        Assignment assignment = new Assignment();
        BeanUtils.copyProperties(assignmentAddDTO, assignment);

        // 补全默认字段
        assignment.setCreator(assignmentAddDTO.getTeacherId());
        assignment.setStatus(StatusConstant.ASSIGNMENT_DRAFT);
        assignment.setVersion(1);
        assignment.setCreateTime(LocalDateTime.now());
        assignment.setUpdateTime(LocalDateTime.now());
        assignment.setDeleted(false);

        // 写入数据库
        assignmentMapper.insert(assignment);

        // 返回作业ID
        return AssignmentIdVO.builder().assignmentId(assignment.getId()).build();
    }

    /**
     * 修改作业
     *
     * @param assignmentUpdateDTO 作业修改信息
     * @return 作业ID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public AssignmentIdVO updateAssignment(AssignmentUpdateDTO assignmentUpdateDTO) {
        // 校验作业是否存在
        Assignment existingAssignment = assignmentMapper.selectById(assignmentUpdateDTO.getAssignmentId());
        if (existingAssignment == null) {
            throw new IllegalArgumentException(MessageConstant.ASSIGNMENT_NOT_EXIST);
        }

        // 构造更新实体
        Assignment assignment = new Assignment();
        BeanUtils.copyProperties(assignmentUpdateDTO, assignment);
        assignment.setId(assignmentUpdateDTO.getAssignmentId());
        assignment.setDeadline(assignmentUpdateDTO.getDeadline() != null ? assignmentUpdateDTO.getDeadline() : null);
        assignment.setUpdateTime(LocalDateTime.now());

        // 更新数据库
        assignmentMapper.update(assignment);
        // 返回作业ID
        return AssignmentIdVO.builder().assignmentId(assignment.getId()).build();
    }

    /**
     * 教师评价学生作业
     *
     * @param teacherGradeDTO 评分信息
     * @return 作业评分ID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public AssignmentScoreIdVO gradeAssignment(TeacherGradeAssignmentDTO teacherGradeDTO) {
        // 校验评分记录是否存在
        AssignmentScore assignmentScore = assignmentScoreMapper.selectById(teacherGradeDTO.getAssignmentScoreId());
        if (assignmentScore == null) {
            throw new IllegalArgumentException(MessageConstant.ASSIGNMENT_SCORE_NOT_EXIST);
        }

        // 更新评分信息（分数/状态/评分人/时间）
        assignmentScore.setScore(teacherGradeDTO.getScore() != null ? BigDecimal.valueOf(teacherGradeDTO.getScore()) : assignmentScore.getScore());
        assignmentScore.setStatus(StatusConstant.ASSIGNMENT_SCORE_GRADED);
        assignmentScore.setGrader(teacherGradeDTO.getTeacherId());
        assignmentScore.setUpdateTime(LocalDateTime.now());

        // 更新作业成绩记录
        assignmentScoreMapper.update(assignmentScore);

        // 记录教师评语
        AssignmentRemark assignmentRemark = new AssignmentRemark();
        assignmentRemark.setAssignmentScoreId(teacherGradeDTO.getAssignmentScoreId());
        assignmentRemark.setTeacherRemark(teacherGradeDTO.getTeacherRemark());
        assignmentRemark.setCreateTime(LocalDateTime.now());
        assignmentRemark.setUpdateTime(LocalDateTime.now());
        assignmentRemark.setDeleted(false);

        // 插入作业评价记录
        assignmentRemarkMapper.insert(assignmentRemark);

        // 返回评分ID
        return AssignmentScoreIdVO.builder().assignmentScoreId(assignmentScore.getId()).build();
    }

    /**
     * 学生提交作业
     *
     * @param studentSubmitMetaDTO 提交元数据
     * @param file                 作业文件
     * @return 评分ID与文件URL
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public AssignmentScoreUploadVO submitAssignment(StudentSubmitMetaDTO studentSubmitMetaDTO, MultipartFile file) {
        // 校验作业是否存在
        Assignment assignment = assignmentMapper.selectById(studentSubmitMetaDTO.getAssignmentId());
        if (assignment == null) {
            throw new IllegalArgumentException(MessageConstant.ASSIGNMENT_NOT_EXIST);
        }

        // 计算提交次数并校验上限
        Integer maxSubmitNo = assignmentScoreMapper.selectMaxSubmitNo(studentSubmitMetaDTO.getAssignmentId(), studentSubmitMetaDTO.getStudentId());
        int nextSubmitNo = (maxSubmitNo == null ? 0 : maxSubmitNo) + 1;
        if (assignment.getSubmitLimit() != null && nextSubmitNo > assignment.getSubmitLimit()) {
            throw new IllegalStateException(MessageConstant.ASSIGNMENT_SUBMIT_LIMIT_EXCEEDED);
        }

        // 上传文件到 OSS，获取访问地址
        String uploadFileName = studentSubmitMetaDTO.getFileName();
        String fileName;

        try {
            fileName = aliyunOSSOperator.upload(file.getBytes(), uploadFileName);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // 构造提交记录并入库
        AssignmentScore assignmentScoreRecord = new AssignmentScore();
        assignmentScoreRecord.setAssignmentId(studentSubmitMetaDTO.getAssignmentId());
        assignmentScoreRecord.setStudentId(studentSubmitMetaDTO.getStudentId());
        assignmentScoreRecord.setStatus(StatusConstant.ASSIGNMENT_SCORE_SUBMITTED);
        assignmentScoreRecord.setSubmitNo(nextSubmitNo);
        assignmentScoreRecord.setSubmitFileUrl(fileName);
        assignmentScoreRecord.setCreateTime(LocalDateTime.now());
        assignmentScoreRecord.setUpdateTime(LocalDateTime.now());
        assignmentScoreRecord.setDeleted(false);

        assignmentScoreMapper.deleted(studentSubmitMetaDTO.getAssignmentId(), studentSubmitMetaDTO.getStudentId());
        assignmentScoreMapper.insert(assignmentScoreRecord);

        // 返回评分ID与文件URL
        return AssignmentScoreUploadVO.builder()
                .assignmentScoreId(assignmentScoreRecord.getId())
                .fileUrl(null)
                .build();
    }

    /**
     * 获取作业反馈
     *
     * @param studentId    学生ID
     * @param assignmentId 作业ID
     * @return 反馈信息
     */
    @Override
    public AssignmentFeedbackVO getFeedback(Long studentId, Long assignmentId) {
        // 查询最新一次提交
        List<AssignmentScore> assignmentScores = assignmentScoreMapper.selectByAssignmentAndStudent(assignmentId, studentId);
        if (assignmentScores.isEmpty()) {
            return null;
        }
        AssignmentScore latestScore = assignmentScores.get(0);

        // 查询评语及维度备注
        AssignmentRemark assignmentRemark = assignmentRemarkMapper.selectByScoreId(latestScore.getId());
        List<AssignmentDimensionRemark> dimensionRemarks = new ArrayList<>();
        if (assignmentRemark != null) {
            dimensionRemarks = assignmentDimensionRemarkMapper.selectByAssignmentRemarkId(assignmentRemark.getId());
        }

        // 组装反馈VO
        List<AssignmentFeedbackVO.DimensionItem> dimensionItems = new ArrayList<>();
        for (AssignmentDimensionRemark dimensionRemark : dimensionRemarks) {
            dimensionItems.add(AssignmentFeedbackVO.DimensionItem.builder()
                    .name("维度#" + dimensionRemark.getDimensionId())
                    .score(dimensionRemark.getScore() != null ? dimensionRemark.getScore().intValue() : null)
                    .remark(dimensionRemark.getRemark())
                    .build());
        }

        // 返回反馈信息
        return AssignmentFeedbackVO.builder()
                .assignmentScoreId(latestScore.getId())
                .dimensions(dimensionItems)
                .aiRemark(assignmentRemark != null ? assignmentRemark.getAiRemark() : null)
                .teacherRemark(assignmentRemark != null ? assignmentRemark.getTeacherRemark() : null)
                .build();
    }

    /**
     * 教师按课程查询作业列表
     *
     * @param teacherAssignmentListDTO 查询条件
     * @return 作业列表
     */
    @Override
    public List<AssignmentListItemVO> listTeacherAssignments(TeacherAssignmentListDTO teacherAssignmentListDTO) {
        List<Assignment> assignments = assignmentMapper.selectByTeacherAndCourse(teacherAssignmentListDTO.getTeacherId(), teacherAssignmentListDTO.getCourseId());
        List<AssignmentListItemVO> items = new ArrayList<>();
        for (Assignment assignment : assignments) {
            items.add(AssignmentListItemVO.builder()
                    .assignmentId(assignment.getId())
                    .name(assignment.getName())
                    .status(assignment.getStatus())
                    .deadline(assignment.getDeadline())
                    .build());
        }
        return items;
    }

    /**
     * 学生按课程查询作业列表
     *
     * @param studentAssignmentListDTO 查询条件
     * @return 作业列表
     */
    @Override
    public List<AssignmentListItemVO> listStudentAssignments(StudentAssignmentListDTO studentAssignmentListDTO) {
        List<Assignment> assignments = assignmentMapper.selectByCourse(studentAssignmentListDTO.getCourseId());
        List<AssignmentListItemVO> items = new ArrayList<>();
        for (Assignment assignment : assignments) {
            items.add(AssignmentListItemVO.builder()
                    .assignmentId(assignment.getId())
                    .name(assignment.getName())
                    .status(assignment.getStatus())
                    .deadline(assignment.getDeadline())
                    .build());
        }
        return items;
    }

    /**
     * 教师删除作业（逻辑删除）
     *
     * @param assignmentDeleteDTO 删除条件
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteAssignment(AssignmentDeleteDTO assignmentDeleteDTO) {
        Assignment assignment = assignmentMapper.selectById(assignmentDeleteDTO.getAssignmentId());
        if (assignment == null) {
            throw new IllegalArgumentException(MessageConstant.ASSIGNMENT_NOT_EXIST);
        }
        if (assignmentDeleteDTO.getCourseId() != null && assignment.getCourseId() != null
                && !assignmentDeleteDTO.getCourseId().equals(assignment.getCourseId())) {
            throw new IllegalArgumentException(MessageConstant.ASSIGNMENT_NOT_EXIST);
        }
        if (assignmentDeleteDTO.getTeacherId() != null && assignment.getCreator() != null
                && !assignmentDeleteDTO.getTeacherId().equals(assignment.getCreator())) {
            throw new IllegalArgumentException(MessageConstant.ASSIGNMENT_NOT_EXIST);
        }

        Assignment update = new Assignment();
        update.setId(assignment.getId());
        update.setDeleted(true);
        update.setUpdateTime(LocalDateTime.now());
        assignmentMapper.update(update);
    }

    @Override
    public AssignmentVO getAssignment(Long assignmentId) {
        return assignmentMapper.getAssignment(assignmentId);
    }
}