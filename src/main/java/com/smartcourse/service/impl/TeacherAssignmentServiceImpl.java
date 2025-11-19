package com.smartcourse.service.impl;

import com.smartcourse.exception.IllegalOperationException;
import com.smartcourse.mapper.AssignmentMapper;
import com.smartcourse.mapper.AssignmentScoreMapper;
import com.smartcourse.pojo.dto.teacher.assignment.TeacherGetAssignmentDTO;
import com.smartcourse.pojo.entity.AssignmentScore;
import com.smartcourse.pojo.vo.AssignmentVO;
import com.smartcourse.pojo.vo.teacher.assignment.TeacherGetAssignmentVO;
import com.smartcourse.service.TeacherAssignmentService;
import com.smartcourse.utils.AliyunOSSOperator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TeacherAssignmentServiceImpl implements TeacherAssignmentService {
    private final AssignmentScoreMapper  assignmentScoreMapper;
    private final AssignmentMapper assignmentMapper;
    private final AliyunOSSOperator aliyunOSSOperator;

    @Override
    @Transactional
    public TeacherGetAssignmentVO getAssignment(TeacherGetAssignmentDTO dto) {
        List<AssignmentScore> list = assignmentScoreMapper.selectByAssignmentAndStudent(dto.getAssignmentId(), dto.getStudentId());

        AssignmentScore latest = list.stream()
                .max(Comparator.comparing(AssignmentScore::getSubmitNo))
                .orElse(null);
        if(latest == null) {
            throw new IllegalOperationException("operation fail");
        }
        AssignmentVO assignment = assignmentMapper.getAssignment(dto.getAssignmentId());
        String url = aliyunOSSOperator.getUrl(latest.getSubmitFileUrl());
        return TeacherGetAssignmentVO.builder().name(assignment.getName())
                .description(assignment.getDescription())
                .assignmentUrl(url)
                .build();
    }
}
