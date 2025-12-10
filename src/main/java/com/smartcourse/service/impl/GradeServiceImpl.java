package com.smartcourse.service.impl;

import com.smartcourse.converter.ExamItemConverter;
import com.smartcourse.infra.http.dify.DifyClientGateway;
import com.smartcourse.mapper.ExamItemMapper;
import com.smartcourse.mapper.ExamScoreItemMapper;
import com.smartcourse.pojo.dto.dify.base.blocked.DifyCompletionResponse;
import com.smartcourse.pojo.dto.dify.DifyGradeShortQuestionDTO;
import com.smartcourse.pojo.dto.dify.DifyGradeQuestionResponse;
import com.smartcourse.pojo.dto.dify.base.blocked.DifyRequestBaseDTO;
import com.smartcourse.pojo.entity.ExamScoreItem;
import com.smartcourse.pojo.vo.exam.sql.GradeShortQuestionSqlVO;
import com.smartcourse.service.GradeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


@Slf4j
@Service
@RequiredArgsConstructor
public class GradeServiceImpl implements GradeService {
    private final ExamItemMapper examItemMapper;
    private final DifyClientGateway difyClientGateway;
    private final ExamItemConverter examItemConverter;
    private final ExamScoreItemMapper examScoreItemMapper;

    @Override
    public void gradeShortQuestion(Long scoreId, Long examItemId) {
        GradeShortQuestionSqlVO sqlVO = examItemMapper.getStudentAnswer(scoreId, examItemId);
        if(sqlVO == null) {
            log.warn("跳过score_id{},exam_item_id{}",scoreId,examItemId);
            return;
        }
        DifyGradeShortQuestionDTO dto = examItemConverter.gradeShortQuestionSqlVOToDifyDTO(sqlVO);
        // 远程调用
        DifyCompletionResponse<DifyGradeQuestionResponse> responseDTO = difyClientGateway.gradeShortQuestionClient()
                .gradeShortQuestion(new DifyRequestBaseDTO<>("user", dto));
        ExamScoreItem examScoreItem = ExamScoreItem.builder()
                .id(sqlVO.getExamScoreItemId())
                .aiScore(responseDTO.getData().getOutputs().getScore())
                .build();
        examScoreItemMapper.updateExamScoreItemSelective(examScoreItem);
    }
}
