package com.smartcourse.service.impl;

import com.smartcourse.converter.DifyConverter;
import com.smartcourse.exception.IllegalOperationException;
import com.smartcourse.infra.http.dify.DifyClientGateway;
import com.smartcourse.mapper.AssignmentMapper;
import com.smartcourse.mapper.AssignmentScoreMapper;
import com.smartcourse.pojo.dto.dify.base.blocked.DifyCompletionResponse;
import com.smartcourse.pojo.dto.dify.DifyPolishAssignmentDTO;
import com.smartcourse.pojo.dto.dify.base.blocked.DifyRequestBaseDTO;
import com.smartcourse.pojo.dto.teacher.assignment.TeacherGetAssignmentDTO;
import com.smartcourse.pojo.dto.teacher.assignment.TeacherPolishAssignmentDTO;
import com.smartcourse.pojo.entity.AssignmentScore;
import com.smartcourse.pojo.vo.AssignmentVO;
import com.smartcourse.pojo.vo.dify.DifyPolishAssignmentVO;
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
    private final DifyConverter  difyConverter;
    private final AssignmentScoreMapper  assignmentScoreMapper;
    private final AssignmentMapper assignmentMapper;
    private final AliyunOSSOperator aliyunOSSOperator;
    private final DifyClientGateway difyClientGateway;

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

    @Override
    public String polishAssignment(TeacherPolishAssignmentDTO dto) {
        DifyPolishAssignmentDTO difyDTO = difyConverter.polishAssignmentDTOToDifyDTO(dto);
        DifyCompletionResponse<DifyPolishAssignmentVO> response = difyClientGateway.polishAssignmentClient()
                .polishAssignment(new DifyRequestBaseDTO<>("user", difyDTO));

        return response.getData().getOutputs().getResult();
    }
}
