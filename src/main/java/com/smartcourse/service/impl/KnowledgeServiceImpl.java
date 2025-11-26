package com.smartcourse.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartcourse.mapper.ExamMapper;
import com.smartcourse.mapper.KnowledgeMapper;
import com.smartcourse.model.KnowledgePoint;
import com.smartcourse.model.QuestionKnowledgeDocument;
import com.smartcourse.pojo.dto.knowledge.ClassMapDTO;
import com.smartcourse.pojo.dto.knowledge.StudentMapDTO;
import com.smartcourse.pojo.vo.knowledge.*;
import com.smartcourse.repository.QuestionKnowledgeRepository;
import com.smartcourse.service.KnowledgeService;
import jakarta.json.Json;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        Long courseId = examMapper.getCourseId(studentMapDTO.getExamId());
        List<EdgeVO> edgeVOS = knowledgeMapper.getEdgeByCourseId(courseId);
        studentMapVO.setEdges(edgeVOS);

        ObjectMapper objectMapper = new ObjectMapper();
        List<ExamNodeVO> nodeVOList = new ArrayList<>();
        String nodeJson = knowledgeMapper.getByJson(studentMapDTO.getStudentId(), studentMapDTO.getExamId());
        if (nodeJson != null) {
            try {
                nodeVOList = objectMapper.readValue(nodeJson, new TypeReference<List<ExamNodeVO>>() {
                });
                studentMapVO.setNodes(nodeVOList);
                return studentMapVO;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

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
                        .filter(kp -> kp != null && kp.getId() != null && kp.getId().equals(nodeId))
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
                            shortAnswerAmount += weight * targetVO.getTotalScore().doubleValue();
                    case "fill_blank" -> fillBlankAmount += weight * targetVO.getTotalScore().doubleValue();
                    case "multiple" -> multipleAmount += weight * targetVO.getTotalScore().doubleValue();
                    default -> singleAmount += weight * targetVO.getTotalScore().doubleValue();
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
        try {
            String json = objectMapper.writeValueAsString(nodeVOList);
            knowledgeMapper.addNodes(studentMapDTO.getExamId(), studentMapDTO.getStudentId(), json);
        } catch (Exception e) {
            e.printStackTrace();
        }

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

    @Override
    @Transactional
    public ClassMapVO getClassMap(ClassMapDTO classMapDTO) {
        ObjectMapper objectMapper = new ObjectMapper();
        ClassMapVO classMapVO = new ClassMapVO();

        Long courseId = examMapper.getCourseId(classMapDTO.getExamId());
        List<EdgeVO> edgeVOS = knowledgeMapper.getEdgeByCourseId(courseId);
        classMapVO.setEdges(edgeVOS);
        List<ClassNodeVO> nodeList = new ArrayList<>();

        List<NodeQuestionVO> list = examMapper.getClassQuestion(classMapDTO);
        Map<Long, List<NodeQuestionVO>> nodeQuestionVOS = new HashMap<>();
        if (list != null) {
            for (NodeQuestionVO vo : list) {
                if (vo != null && vo.getStudentId() != null) {
                    nodeQuestionVOS.computeIfAbsent(vo.getStudentId(), k -> new ArrayList<>())
                            .add(vo);
                }
            }
        }
        List<Long> questionIds = new ArrayList<>();
        for (Map.Entry<Long, List<NodeQuestionVO>> entry : nodeQuestionVOS.entrySet()) {
            for (NodeQuestionVO nodeQuestionVO : entry.getValue()) {
                questionIds.add(nodeQuestionVO.getQuestionId());
            }
            break;
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
            List<Double> getScoreList = new ArrayList<>();
            ClassNodeVO classNodeVO = new ClassNodeVO();

            for (Map.Entry<Long, List<NodeQuestionVO>> entry : nodeQuestionVOS.entrySet()) {
                Long studentId = entry.getKey();
                List<NodeQuestionVO> value = entry.getValue();
                String nodeJson = knowledgeMapper.getByJson(studentId, classMapDTO.getExamId());
                if (nodeJson != null) {
                    try {
                        List<ExamNodeVO> nodes = objectMapper.readValue(nodeJson, new TypeReference<List<ExamNodeVO>>() {
                        });
                        ExamNodeVO examNodeVO = nodes.stream()
                                .filter(vo -> vo != null && vo.getId() != null && vo.getId().equals(nodeId))
                                .findFirst()
                                .orElse(null);// 如果没找到返回 null
                        assert examNodeVO != null;
                        getScoreList.add(examNodeVO.getGetScore());
                        allScore = examNodeVO.getAllScore();
                        getScore += examNodeVO.getGetScore();
                        continue;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                double studentAllScore = 0.0;
                double getStudentScore = 0.0;
                for (QuestionKnowledgeDocument questionKnowledgeDocument : allById) {
                    List<KnowledgePoint> knowledgePoints = questionKnowledgeDocument.getKnowledgePoints();
                    double weight = knowledgePoints.stream()
                            .filter(kp -> kp != null && kp.getId() != null && kp.getId().equals(1L))
                            .findFirst()
                            .map(KnowledgePoint::getWeight)
                            .orElse(0.0);

                    NodeQuestionVO targetVO = value.stream()
                            .filter(vo -> vo != null && vo.getQuestionId() != null && vo.getQuestionId().equals(questionKnowledgeDocument.getId()))
                            .findFirst()
                            .orElse(null); // 如果没找到返回 null

                    assert targetVO != null;
                    studentAllScore += targetVO.getTotalScore().doubleValue() * weight;
                    getStudentScore += targetVO.getScore().doubleValue() * weight;
                }
                allScore = studentAllScore;
                getScore += getStudentScore;
                getScoreList.add(getStudentScore);
            }

            classNodeVO.setId(nodeId);
            classNodeVO.setAllScore(allScore);
            classNodeVO.setGetScore(getScore / nodeQuestionVOS.size());
            classNodeVO.setGetScoreList(getScoreList);
            nodeList.add(classNodeVO);
        }

        classMapVO.setNodes(nodeList);
        classMapVO.setStudentNumber((long) nodeQuestionVOS.size());
        return classMapVO;
    }

    @Override
    @Transactional
    public CourseMapVO getCourseMap(Long examId) {
        CourseMapVO courseMapVO = new CourseMapVO();
        List<CourseNodeVO> courseNodeVOS = new ArrayList<>();

        List<Long> classIds = knowledgeMapper.getClass(examId);
        Set<Long> set = new HashSet<>();
        List<ClassMapVO> nodeList = new ArrayList<>();
        for (Long classId : classIds) {
            ClassMapDTO classMapDTO = new ClassMapDTO();
            classMapDTO.setExamId(examId);
            classMapDTO.setClassId(classId);
            ClassMapVO classMapVO = getClassMap(classMapDTO);
            if (classMapVO.getNodes() == null || classMapVO.getNodes().isEmpty()) {
                nodeList.add(null);
                continue;
            }

            nodeList.add(classMapVO);
            List<ClassNodeVO> nodes = classMapVO.getNodes();
            for (ClassNodeVO classNodeVO : nodes) {
                set.add(classNodeVO.getId());
            }
            courseMapVO.setEdges(classMapVO.getEdges());
        }

        for (Long nodeId : set) {
            CourseNodeVO courseNodeVO = new CourseNodeVO();
            courseNodeVO.setId(nodeId);
            double allScore = 0.0;
            double getScore = 0.0;
            double num = 0.0;
            List<Clazz> clazzes = new ArrayList<>();
            List<Double> students = new ArrayList<>();

            for (int i = 0; i < nodeList.size(); i++) {
                ClassMapVO classMapVO = nodeList.get(i);
                if (classMapVO == null) {
                    continue;
                }
                Long classId = classIds.get(i);
                List<ClassNodeVO> nodes = classMapVO.getNodes();
                ClassNodeVO classNodeVO = nodes.stream()
                        .filter(vo -> vo != null && vo.getId() != null && vo.getId().equals(nodeId))
                        .findFirst()
                        .orElse(null);// 如果没找到返回 null;

                if (classNodeVO != null) {
                    allScore = classNodeVO.getAllScore();
                }
                if (classNodeVO != null) {
                    getScore += classNodeVO.getGetScore() * classMapVO.getStudentNumber();
                }
                num += classMapVO.getStudentNumber();
                if (classNodeVO != null) {
                    students.addAll(classNodeVO.getGetScoreList());
                }

                Clazz clazz = new Clazz();
                String name = knowledgeMapper.getName(classId);
                clazz.setName(name);
                if (classNodeVO != null) {
                    clazz.setGetScore(classNodeVO.getGetScore());
                }
                clazzes.add(clazz);
            }

            courseNodeVO.setAllScore(allScore);
            courseNodeVO.setGetScore(getScore / num);
            courseNodeVO.setGetScoreClass(clazzes);
            courseNodeVO.setGetScoreStudent(students);
            courseNodeVOS.add(courseNodeVO);
        }

        courseMapVO.setNodes(courseNodeVOS);
        return courseMapVO;
    }

}
