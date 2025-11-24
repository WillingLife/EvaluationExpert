package com.smartcourse.service.impl;

import com.smartcourse.mapper.ExamMapper;
import com.smartcourse.mapper.KnowledgeMapper;
import com.smartcourse.model.KnowledgePoint;
import com.smartcourse.model.QuestionKnowledgeDocument;
import com.smartcourse.pojo.dto.knowledge.StudentMapDTO;
import com.smartcourse.pojo.vo.knowledge.*;
import com.smartcourse.repository.QuestionKnowledgeRepository;
import com.smartcourse.service.KnowledgeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class KnowledgeServiceImpl implements KnowledgeService {
    @Autowired
    ExamMapper examMapper;

    @Autowired
    KnowledgeMapper knowledgeMapper;

    @Autowired
    QuestionKnowledgeRepository questionKnowledgeRepository;

    @Override
    public StudentMapVO getStudentMap(StudentMapDTO studentMapDTO) {
        StudentMapVO studentMapVO = new StudentMapVO();
        List<ExamNodeVO> nodeVOList = new ArrayList<>();

        List<NodeQuestionVO> nodeQuestionVOS = examMapper.getQuestion(studentMapDTO);
        List<Long> questionIds = new ArrayList<>();
        for (NodeQuestionVO nodeQuestionVO : nodeQuestionVOS) {
            questionIds.add(nodeQuestionVO.getQuestionId());
        }
        Iterable<QuestionKnowledgeDocument> allById = questionKnowledgeRepository.findAllById(questionIds);
        Set<Long> set = StreamSupport.stream(allById.spliterator(), false)
                .findFirst() // 只取第一个文档
                .map(QuestionKnowledgeDocument::getKnowledgePoints)
                .stream()
                .flatMap(List::stream)
                .filter(Objects::nonNull)
                .map(KnowledgePoint::getId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        for (Long nodeId : set) {
            double allScore = 0.0;
            double getScore = 0.0;
            double singleAmount = 0.0;
            double multipleAmount = 0.0;
            double fillBlankAmount = 0.0;
            double shortAnswerAmount = 0.0;
            double allWeight = 0.0;

            ExamNodeVO nodeVO = new ExamNodeVO();
            nodeVO.setId(nodeId);
            for (QuestionKnowledgeDocument questionKnowledgeDocument : allById) {
                List<KnowledgePoint> knowledgePoints = questionKnowledgeDocument.getKnowledgePoints();
                double weight = knowledgePoints.stream()
                        .filter(kp -> kp != null && kp.getId() != null && kp.getId().equals(1L))
                        .findFirst()
                        .map(KnowledgePoint::getWeight)
                        .orElse(0.0);
                allWeight += weight;

                NodeQuestionVO targetVO = nodeQuestionVOS.stream()
                        .filter(vo -> vo != null && vo.getQuestionId() != null && vo.getQuestionId().equals(questionKnowledgeDocument.getId()))
                        .findFirst()
                        .orElse(null); // 如果没找到返回 null

                assert targetVO != null;
                allScore += targetVO.getTotalScore().doubleValue() * weight;
                getScore += targetVO.getScore().doubleValue() * weight;
                switch (targetVO.getQuestionType()) {
                    case "short_answer" ->
                            shortAnswerAmount += weight * targetVO.getTotalScore().doubleValue() * weight;
                    case "fill_blank" -> fillBlankAmount += weight * targetVO.getTotalScore().doubleValue() * weight;
                    case "multiple" -> multipleAmount += weight * targetVO.getTotalScore().doubleValue() * weight;
                    default -> singleAmount += weight * targetVO.getTotalScore().doubleValue() * weight;
                }
            }

            nodeVO.setAllScore(allScore);
            nodeVO.setGetScore(getScore / allWeight);
            nodeVO.setSingleAmount(singleAmount / allScore);
            nodeVO.setMultipleAmount(multipleAmount / allScore);
            nodeVO.setFillBlankAmount(fillBlankAmount / allScore);
            nodeVO.setShortAnswerAmount(shortAnswerAmount / allScore);
            nodeVOList.add(nodeVO);
        }
        studentMapVO.setNodes(nodeVOList);

        Long courseId = examMapper.getCourseId(studentMapDTO.getExamId());
        List<EdgeVO> edgeVOS = knowledgeMapper.getEdgeByCourseId(courseId);
        studentMapVO.setEdges(edgeVOS);

        return studentMapVO;
    }

    @Override
    public MapVO getMap(Long courseId) {
        List<NodeVO> nodeVOS = knowledgeMapper.getNodeByCourseId(courseId);
        List<EdgeVO> edgeVOS = knowledgeMapper.getEdgeByCourseId(courseId);
        MapVO mapVO = new MapVO();
        mapVO.setNodes(nodeVOS);
        mapVO.setEdges(edgeVOS);
        return mapVO;
    }

}
