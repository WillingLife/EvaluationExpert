package com.smartcourse.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartcourse.enums.QuestionTypeEnum;
import com.smartcourse.mapper.*;
import com.smartcourse.pojo.dto.StudentGetExamDTO;
import com.smartcourse.pojo.dto.exam.ExamScoreItemDTO;
import com.smartcourse.pojo.dto.exam.StudentExamDTO;
import com.smartcourse.pojo.entity.Exam;
import com.smartcourse.pojo.entity.ExamScore;
import com.smartcourse.pojo.entity.ExamScoreItem;
import com.smartcourse.pojo.entity.ExamSection;
import com.smartcourse.pojo.vo.exam.ExamSectionWithIdsVO;
import com.smartcourse.pojo.vo.exam.SectionItemDTO;
import com.smartcourse.pojo.vo.exam.StudentExamVO;
import com.smartcourse.pojo.vo.exam.question.*;
import com.smartcourse.pojo.vo.exam.sql.SectionItemVO;
import com.smartcourse.service.AsyncQuestionService;
import com.smartcourse.service.StudentExamService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.smartcourse.pojo.dto.exam.StudentExamQuestionDTO;
import com.smartcourse.pojo.dto.exam.StudentExamSectionDTO;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
public class StudentExamServiceImpl implements StudentExamService {
    @Autowired
    ExamMapper examMapper;

    @Autowired
    ExamScoreMapper examScoreMapper;

    @Autowired
    ExamSectionMapper examSectionMapper;

    @Autowired
    ExamScoreItemMapper examScoreItemMapper;

    @Autowired
    QuestionMapper questionMapper;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AsyncQuestionService asyncQuestionService;


    /**
     * @param studentGetExamDTO
     * @return
     */
    @Override
    public StudentExamVO getStudentExamPaper(StudentGetExamDTO studentGetExamDTO) {
        Long examId = studentGetExamDTO.getExamId();
        Exam exam = examMapper.getById(examId);
        List<SectionItemDTO> list = examSectionMapper.getSectionItems(examId);

        // 按 sectionId 分组
        Map<Long, List<SectionItemDTO>> groupMap = list.stream()
                .collect(Collectors.groupingBy(SectionItemDTO::getId));

        // 转换为 ExamSectionWithIdsVO
        List<ExamSectionWithIdsVO> collect = groupMap.values().stream()
                .map(items -> {
                    SectionItemDTO first = items.get(0);

                    ExamSectionWithIdsVO vo = new ExamSectionWithIdsVO();
                    // 复制 Section 字段
                    BeanUtils.copyProperties(first, vo);

                    // 提取 questionId 列表（过滤 null）
                    List<Long> questionIds = items.stream()
                            .map(SectionItemDTO::getQuestionId)
                            .filter(id -> id != null)
                            .collect(Collectors.toList());

                    vo.setQuestionIds(questionIds);
                    return vo;
                })
                .sorted((a, b) -> a.getOrderNo().compareTo(b.getOrderNo())) // 按 orderNo 排序
                .toList();
        List<Long> choiceQuestionIds = new ArrayList<>();
        List<Long> fillBlankIds = new ArrayList<>();
        List<Long> shortanswerIds = new ArrayList<>();

        for (ExamSectionWithIdsVO vo : collect) {
            if (vo.getQuestionType().equals("fill_blank")) {
                fillBlankIds.addAll(vo.getQuestionIds());
            } else if (vo.getQuestionType().equals("short_answer")) {
                shortanswerIds.addAll(vo.getQuestionIds());
            } else {
                choiceQuestionIds.addAll(vo.getQuestionIds());
            }
        }

        // 并发执行3次查询
        CompletableFuture<List<StudentExamChoiceQuestionVO>> choiceFuture =
                asyncQuestionService.getChoiceAsync(choiceQuestionIds);

        CompletableFuture<List<StudentExamFillBlankQuestionVO>> fillBlankFuture =
                asyncQuestionService.getFillAsync(fillBlankIds);

        CompletableFuture<List<StudentExamShortAnswerQuestionVO>> shortAnswerFuture =
                asyncQuestionService.getShortAsync(shortanswerIds);

        // 等待所有任务完成并获取结果
        CompletableFuture.allOf(choiceFuture, fillBlankFuture, shortAnswerFuture).join();

        List<StudentExamChoiceQuestionVO> studentExamChoiceQuestionVOS = choiceFuture.join();
        List<StudentExamFillBlankQuestionVO> studentExamFillBlankQuestionVOS = fillBlankFuture.join();
        List<StudentExamShortAnswerQuestionVO> studentExamShortAnswerQuestionVOS = shortAnswerFuture.join();

        List<StudentExamSectionVO> studentExamSectionVOS = new ArrayList<>();
        for (ExamSectionWithIdsVO vo : collect) {
            StudentExamSectionVO studentExamSectionVO = new StudentExamSectionVO();
            studentExamSectionVO.setSectionId(vo.getId());
            studentExamSectionVO.setTitle(vo.getTitle());
            studentExamSectionVO.setQuestionType(QuestionTypeEnum.fromValue(vo.getQuestionType()));
            studentExamSectionVO.setQuestionNumber(vo.getQuestionIds().size());
            studentExamSectionVO.setDescription(vo.getDescription());
            List<Long> questionIds = vo.getQuestionIds();
            List<StudentExamQuestionVO> questions = new ArrayList<>();
            if (vo.getQuestionType().equals("fill_blank")) {
                for (StudentExamFillBlankQuestionVO studentExamFillBlankQuestionVO : studentExamFillBlankQuestionVOS) {
                    if (questionIds.contains(studentExamFillBlankQuestionVO.getQuestionId())) {
                        questions.add(studentExamFillBlankQuestionVO);
                    }
                }
            } else if (vo.getQuestionType().equals("short_answer")) {
                for (StudentExamShortAnswerQuestionVO studentExamShortAnswerQuestionVO : studentExamShortAnswerQuestionVOS) {
                    if (questionIds.contains(studentExamShortAnswerQuestionVO.getQuestionId())) {
                        questions.add(studentExamShortAnswerQuestionVO);
                    }
                }
            } else {
                for (StudentExamChoiceQuestionVO studentExamChoiceQuestionVO : studentExamChoiceQuestionVOS) {
                    if (questionIds.contains(studentExamChoiceQuestionVO.getQuestionId())) {
                        questions.add(studentExamChoiceQuestionVO);
                    }
                }
            }
            studentExamSectionVO.setQuestions(questions);
        }

        StudentExamVO studentExamVO = new StudentExamVO();
        studentExamVO.setExamId(examId);
        studentExamVO.setExamName(exam.getName());
        studentExamVO.setExamNotice(exam.getNotice());
        studentExamVO.setStartTime(exam.getStartTime());
        studentExamVO.setDurationMinutes(exam.getDurationMinutes());
        studentExamVO.setSections(studentExamSectionVOS);

        return studentExamVO;
        /*
        查询过程为：
        查询1：根据examId在exam表查询Exam实体类
        查询2：根据examId在exam_section表查询List<ExamSection>
        查询3：根据List<ExamSection>得到的section_id列表查询exam_item表，返回Map<Long,List<Long>>,key为section_id,
        value为question_id列表
        查询4：合并成整个question_id列表，向question表查询，根据question表的不同type字段向相应的表进行条件查询，返回数据
        并发顺序为1
        2->3->4
        优化方法为：
        查询2和查询3合并为1次连表查询
        提前分好组别（单/多选，填空，选择），使用CompletableFuture并发3次查询
         */
    }

    @Override
    @Transactional
    public void submit(StudentExamDTO studentExamDTO) {
        ExamScore examScore = new ExamScore();
        examScore.setExamId(studentExamDTO.getExamId());
        examScore.setStudentId(studentExamDTO.getStudentId());
        examScore.setStatus("submitted");
        examScore.setStartTime(studentExamDTO.getStartTime());
        examScore.setSubmitTime(studentExamDTO.getSubmitTime());
        examScore.setCreateTime(LocalDateTime.now());
        examScore.setUpdateTime(LocalDateTime.now());
        if (studentExamDTO.getStartTime() != null && studentExamDTO.getSubmitTime() != null) {
            Duration duration = Duration.between(studentExamDTO.getStartTime(), studentExamDTO.getSubmitTime());
            examScore.setDurationSeconds(Math.toIntExact(duration.getSeconds()));
        } else {
            throw new IllegalArgumentException("提交时间和开始时间不能为0");
        }

        Long scoreId = examScoreMapper.submit(examScore);

        List<ExamScoreItemDTO> examScoreItems = new ArrayList<>();
        for (StudentExamSectionDTO section : studentExamDTO.getSections()) {
            String questionType = String.valueOf(section.getQuestionType());
            for (StudentExamQuestionDTO question : section.getQuestions()) {
                ExamScoreItemDTO examScoreItem = new ExamScoreItemDTO();
                String answerJson = null;
                try {
                    answerJson = switch (questionType) {
                        case "single", "multiple" -> {
                            // 处理选择题
                            List<Integer> choices = question.getChoiceAnswer();
                            yield objectMapper.writeValueAsString(choices);
                        }
                        case "fill_blank" -> {
                            // 处理填空题
                            List<String> blanks = question.getFillBlankAnswer();
                            yield objectMapper.writeValueAsString(blanks);
                        }
                        case "short_answer" -> {
                            // 处理简答题
                            String answer = question.getShortAnswer();
                            yield objectMapper.writeValueAsString(answer);
                        }
                        default -> answerJson;
                    };
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }

                examScoreItem.setScoreId(scoreId);
                examScoreItem.setAnswer(answerJson);
                examScoreItem.setCreateTime(LocalDateTime.now());
                examScoreItem.setUpdateTime(LocalDateTime.now());
                examScoreItem.setQuestionId(question.getQuestionId());
                examScoreItems.add(examScoreItem);
            }
        }

        examScoreItemMapper.submit(examScoreItems);
    }
}
