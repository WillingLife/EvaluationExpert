package com.smartcourse.service.impl;

import com.smartcourse.converter.DifyConverter;
import com.smartcourse.exception.IllegalOperationException;
import com.smartcourse.infra.http.dify.DifyClientGateway;
import com.smartcourse.mapper.AssignmentMapper;
import com.smartcourse.mapper.AssignmentScoreMapper;
import com.smartcourse.pojo.dto.AssignmentSimilarityRequestDTO;
import com.smartcourse.pojo.dto.dify.DifyPolishAssignmentDTO;
import com.smartcourse.pojo.dto.dify.base.blocked.DifyCompletionResponse;
import com.smartcourse.pojo.dto.dify.base.blocked.DifyRequestBaseDTO;
import com.smartcourse.pojo.dto.teacher.assignment.AssignmentCompareGetDTO;
import com.smartcourse.pojo.dto.teacher.assignment.TeacherGetAssignmentDTO;
import com.smartcourse.pojo.dto.teacher.assignment.TeacherPolishAssignmentDTO;
import com.smartcourse.pojo.entity.AssignmentScore;
import com.smartcourse.pojo.vo.AssignmentVO;
import com.smartcourse.pojo.vo.dify.DifyPolishAssignmentVO;
import com.smartcourse.pojo.vo.teacher.assignment.*;
import com.smartcourse.repository.mongo.AssignmentDetectRepository;
import com.smartcourse.service.AssignmentSimilarityService;
import com.smartcourse.service.TeacherAssignmentService;
import com.smartcourse.utils.AliyunOSSOperator;
import com.smartcourse.utils.HttpClientUtil;
import com.smartcourse.utils.UnifiedDiffFormatter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class TeacherAssignmentServiceImpl implements TeacherAssignmentService {
    private final DifyConverter difyConverter;
    private final AssignmentScoreMapper assignmentScoreMapper;
    private final AssignmentMapper assignmentMapper;
    private final AliyunOSSOperator aliyunOSSOperator;
    private final DifyClientGateway difyClientGateway;
    private final AssignmentSimilarityService assignmentSimilarityService;
    private final AssignmentDetectRepository assignmentDetectRepository;

    @Override
    @Transactional
    public TeacherGetAssignmentVO getAssignment(TeacherGetAssignmentDTO dto) {
        List<AssignmentScore> list = assignmentScoreMapper.selectByAssignmentAndStudent(dto.getAssignmentId(), dto.getStudentId());

        AssignmentScore latest = list.stream()
                .max(Comparator.comparing(AssignmentScore::getSubmitNo))
                .orElse(null);
        if (latest == null) {
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
    public List<TaskStudentListVO> getStudents(Integer assignmentId) {
        return assignmentMapper.getStudents(assignmentId);
    }

    @Override
    public String polishAssignment(TeacherPolishAssignmentDTO dto) {
        DifyPolishAssignmentDTO difyDTO = difyConverter.polishAssignmentDTOToDifyDTO(dto);
        DifyCompletionResponse<DifyPolishAssignmentVO> response = difyClientGateway.polishAssignmentClient()
                .polishAssignment(new DifyRequestBaseDTO<>("user", difyDTO));

        return response.getData().getOutputs().getResult();
    }

    @Override
    public TeacherAssignmentDetectVO detectAssignment(Long assignmentId) {
        if (assignmentId == null) {
            throw new IllegalOperationException("assignment id is required");
        }
        TeacherAssignmentDetectVO detectVO = new TeacherAssignmentDetectVO();
        detectVO.setAssignmentId(assignmentId);

        List<TeacherAssignDetectItemVO> cachedSummary = assignmentDetectRepository.getSummary(assignmentId);
        if (!CollectionUtils.isEmpty(cachedSummary)) {
            detectVO.setList(cachedSummary);
            return detectVO;
        }

        List<TaskStudentListVO> students = getStudents(Math.toIntExact(assignmentId));
        if (CollectionUtils.isEmpty(students)) {
            List<TeacherAssignDetectItemVO> emptyList = Collections.emptyList();
            detectVO.setList(emptyList);
            persistDetectResult(assignmentId, emptyList, Collections.emptyList());
            return detectVO;
        }

        Map<Long, StudentAssignmentMeta> scoreMetaMap = new HashMap<>();
        Map<Long, String> scoreObjectMap = new HashMap<>();
        List<AssignmentSimilarityRequestDTO.AssignmentTextItem> requestItems = new ArrayList<>();

        for (TaskStudentListVO student : students) {
            Long studentId = student.getId();
            if (studentId == null) {
                continue;
            }
            AssignmentScore score = assignmentScoreMapper.getByAssignmentAndStudent(assignmentId, studentId);
            if (score == null || !StringUtils.hasText(score.getRawText())) {
                continue;
            }
            requestItems.add(new AssignmentSimilarityRequestDTO.AssignmentTextItem(score.getId(), score.getRawText()));
            scoreMetaMap.put(score.getId(), new StudentAssignmentMeta(studentId, student.getStudentName(), student.getClassName()));
            scoreObjectMap.put(score.getId(), score.getRawText());
        }

        if (requestItems.size() < 2) {
            List<TeacherAssignDetectItemVO> emptyList = Collections.emptyList();
            detectVO.setList(emptyList);
            persistDetectResult(assignmentId, emptyList, Collections.emptyList());
            return detectVO;
        }

        AssignmentSimilarityRequestDTO requestDTO = AssignmentSimilarityRequestDTO.builder()
                .assignments(requestItems)
                .threshold(0D)
                .build();

        List<AssignmentSimilarityResultVO> similarityResults = assignmentSimilarityService.detectSimilarAssignments(requestDTO);
        if (CollectionUtils.isEmpty(similarityResults)) {
            List<TeacherAssignDetectItemVO> emptyList = Collections.emptyList();
            detectVO.setList(emptyList);
            persistDetectResult(assignmentId, emptyList, Collections.emptyList());
            return detectVO;
        }

        List<TeacherAssignDetectItemVO> summaryList = new ArrayList<>(similarityResults.size());
        List<AssignmentDetectDetailVO> detailList = new ArrayList<>(similarityResults.size());

        for (AssignmentSimilarityResultVO resultVO : similarityResults) {
            StudentAssignmentMeta leftMeta = scoreMetaMap.get(resultVO.getLeftAssignmentId());
            StudentAssignmentMeta rightMeta = scoreMetaMap.get(resultVO.getRightAssignmentId());
            if (leftMeta == null || rightMeta == null) {
                continue;
            }

            TeacherAssignDetectItemVO itemVO = new TeacherAssignDetectItemVO();
            itemVO.setLeftStudentId(leftMeta.studentId());
            itemVO.setLeftStudentName(leftMeta.studentName());
            itemVO.setLeftClassName(leftMeta.className());
            itemVO.setRightStudentId(rightMeta.studentId());
            itemVO.setRightStudentName(rightMeta.studentName());
            itemVO.setRightClassName(rightMeta.className());
            itemVO.setScore(resultVO.getWinnowingScore());
            summaryList.add(itemVO);

            AssignmentDetectDetailVO detailVO = new AssignmentDetectDetailVO();
            detailVO.setLeftAssignmentId(resultVO.getLeftAssignmentId());
            detailVO.setRightAssignmentId(resultVO.getRightAssignmentId());
            String leftContent = StringUtils.hasText(resultVO.getLeftContent())
                    ? resultVO.getLeftContent()
                    : fetchAssignmentContent(scoreObjectMap.get(resultVO.getLeftAssignmentId()));
            String rightContent = StringUtils.hasText(resultVO.getRightContent())
                    ? resultVO.getRightContent()
                    : fetchAssignmentContent(scoreObjectMap.get(resultVO.getRightAssignmentId()));
            String diff = StringUtils.hasText(resultVO.getDiff())
                    ? resultVO.getDiff()
                    : buildDiffContent(leftContent, rightContent,
                    resultVO.getLeftAssignmentId(), resultVO.getRightAssignmentId());
            detailVO.setDiff(diff);
            detailVO.setLeftContent(leftContent);
            detailVO.setRightContent(rightContent);
            detailList.add(detailVO);
        }

        detectVO.setList(summaryList);
        persistDetectResult(assignmentId, summaryList, detailList);
        return detectVO;
    }

    @Override
    public AssignmentDetectCompareVO getAssignmentCompare(AssignmentCompareGetDTO dto) {
        if (dto == null || dto.getAssignmentId() == null
                || dto.getLeftStudentId() == null || dto.getRightStudentId() == null) {
            throw new IllegalOperationException("assignmentId and student ids are required");
        }
        List<TeacherAssignDetectItemVO> summaryList = assignmentDetectRepository.getSummary(dto.getAssignmentId());
        if (CollectionUtils.isEmpty(summaryList)) {
            throw new IllegalOperationException("comparison result not found, please run detection first");
        }
        TeacherAssignDetectItemVO summaryItem = summaryList.stream()
                .filter(item -> isMatchingPair(item, dto.getLeftStudentId(), dto.getRightStudentId()))
                .findFirst()
                .orElseThrow(() -> new IllegalOperationException("comparison pair not found"));

        AssignmentScore leftScore = assignmentScoreMapper.getByAssignmentAndStudent(dto.getAssignmentId(), dto.getLeftStudentId());
        AssignmentScore rightScore = assignmentScoreMapper.getByAssignmentAndStudent(dto.getAssignmentId(), dto.getRightStudentId());

        if (leftScore == null || rightScore == null) {
            throw new IllegalOperationException("assignment score not found for students");
        }

        AssignmentDetectDetailVO detailVO = assignmentDetectRepository.getDetail(dto.getAssignmentId(),
                leftScore.getId(), rightScore.getId());
        if (detailVO == null || !StringUtils.hasText(detailVO.getDiff())) {
            throw new IllegalOperationException("comparison detail not found");
        }

        boolean sameOrder = dto.getLeftStudentId().equals(summaryItem.getLeftStudentId())
                && dto.getRightStudentId().equals(summaryItem.getRightStudentId());
        String diff = detailVO.getDiff();
        if (!StringUtils.hasText(diff) || !diff.contains("@@")) {
            String leftContent = detailVO.getLeftContent();
            if (!StringUtils.hasText(leftContent)) {
                leftContent = fetchAssignmentContent(leftScore.getRawText());
            }
            String rightContent = detailVO.getRightContent();
            if (!StringUtils.hasText(rightContent)) {
                rightContent = fetchAssignmentContent(rightScore.getRawText());
            }
            diff = buildDiffContent(leftContent, rightContent, leftScore.getId(), rightScore.getId());
        }
        AssignmentDetectCompareVO compareVO = new AssignmentDetectCompareVO();
        compareVO.setLeftName(sameOrder ? summaryItem.getLeftStudentName() : summaryItem.getRightStudentName());
        compareVO.setRightName(sameOrder ? summaryItem.getRightStudentName() : summaryItem.getLeftStudentName());
        compareVO.setDiff(diff);
        return compareVO;
    }

    private void persistDetectResult(Long assignmentId,
                                     List<TeacherAssignDetectItemVO> summary,
                                     List<AssignmentDetectDetailVO> details) {
        try {
            assignmentDetectRepository.saveSummary(assignmentId, summary);
            assignmentDetectRepository.replaceDetails(assignmentId, details);
        } catch (Exception ex) {
            log.warn("Failed to persist assignment detect result for assignmentId={}", assignmentId, ex);
        }
    }

    private record StudentAssignmentMeta(Long studentId, String studentName, String className) {
    }

    private boolean isMatchingPair(TeacherAssignDetectItemVO item, Long leftStudentId, Long rightStudentId) {
        if (item == null || leftStudentId == null || rightStudentId == null) {
            return false;
        }
        boolean sameOrder = leftStudentId.equals(item.getLeftStudentId()) &&
                rightStudentId.equals(item.getRightStudentId());
        boolean reversedOrder = leftStudentId.equals(item.getRightStudentId()) &&
                rightStudentId.equals(item.getLeftStudentId());
        return sameOrder || reversedOrder;
    }

    private String buildDiffContent(String leftContent, String rightContent,
                                    Long leftAssignmentId, Long rightAssignmentId) {
        String header = "diff --git a/assignment-" + leftAssignmentId + ".txt b/assignment-" + rightAssignmentId + ".txt"
                + System.lineSeparator()
                + "--- a/assignment-" + leftAssignmentId + ".txt" + System.lineSeparator()
                + "+++ b/assignment-" + rightAssignmentId + ".txt" + System.lineSeparator();
        String patchText = UnifiedDiffFormatter.format(leftContent, rightContent);
        return header + patchText;
    }

    private String fetchAssignmentContent(String objectName) {
        if (!StringUtils.hasText(objectName)) {
            return null;
        }
        try {
            String url = aliyunOSSOperator.getUrl(objectName);
            if (!StringUtils.hasText(url)) {
                return null;
            }
            return HttpClientUtil.doGet(url, null);
        } catch (Exception ex) {
            log.warn("Failed to fetch assignment content from OSS object: {}", objectName, ex);
            return null;
        }
    }
}
