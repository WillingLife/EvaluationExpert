package com.smartcourse.converter;

import com.smartcourse.enums.QuestionTypeEnum;
import com.smartcourse.pojo.dto.TeacherSaveExamDTO;
import com.smartcourse.pojo.dto.exam.TeacherSaveExamQuestionDTO;
import com.smartcourse.pojo.dto.exam.TeacherSaveExamSectionDTO;
import com.smartcourse.pojo.entity.Exam;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@SpringBootTest()
public class ExamConverterTest {
    @Autowired
    private ExamConverter examConverter;

    @Test
    public void testConverterWithNull(){
        TeacherSaveExamDTO dto = new TeacherSaveExamDTO();
        dto.setExamId(null);
        dto.setCourseId(1L);
        dto.setTeacherId(2L);
        dto.setDescription("description");
        dto.setExamName("examName");
        dto.setExamNotice("notice");
        dto.setStartTime(LocalDateTime.now());
        dto.setDurationMinutes(120);
        dto.setTotalScore(new BigDecimal(120));
        dto.setPassScore(new BigDecimal(80));
        dto.setShuffleQuestions(false);
        dto.setShuffleOptions(false);
        dto.setVersion(1);
        List<TeacherSaveExamSectionDTO> sections = new ArrayList<>();
        TeacherSaveExamSectionDTO section1 = new TeacherSaveExamSectionDTO();
        section1.setSectionId(null);
        section1.setOrderNo(1);
        section1.setTitle("title1");
        section1.setQuestionType(QuestionTypeEnum.fromValue("single"));
        section1.setQuestionNumber(2);
        List<TeacherSaveExamQuestionDTO>  questions = new ArrayList<>();
        TeacherSaveExamQuestionDTO question1 = new TeacherSaveExamQuestionDTO();
        question1.setExamItemId(null);
        question1.setQuestionId(1L);
        question1.setScore(BigDecimal.ONE);
        questions.add(question1);
        section1.setQuestions(questions);
        sections.add(section1);
        dto.setSections(sections);

        Exam exam = examConverter.teacherSaveExamDTOToExam(dto);
        System.out.println(exam);


    }
    @Test
    public void testConverterWithVal(){
        TeacherSaveExamDTO dto = new TeacherSaveExamDTO();
        dto.setExamId(9L);
        dto.setCourseId(1L);
        dto.setTeacherId(2L);
        dto.setDescription("description");
        dto.setExamName("examName");
        dto.setExamNotice("notice");
        dto.setStartTime(LocalDateTime.now());
        dto.setDurationMinutes(120);
        dto.setTotalScore(new BigDecimal(120));
        dto.setPassScore(new BigDecimal(80));
        dto.setShuffleQuestions(false);
        dto.setShuffleOptions(false);
        dto.setVersion(1);
        List<TeacherSaveExamSectionDTO> sections = new ArrayList<>();
        TeacherSaveExamSectionDTO section1 = new TeacherSaveExamSectionDTO();
        section1.setSectionId(8L);
        section1.setOrderNo(1);
        section1.setTitle("title1");
        section1.setQuestionType(QuestionTypeEnum.fromValue("single"));
        section1.setQuestionNumber(2);
        List<TeacherSaveExamQuestionDTO>  questions = new ArrayList<>();
        TeacherSaveExamQuestionDTO question1 = new TeacherSaveExamQuestionDTO();
        question1.setExamItemId(null);
        question1.setQuestionId(1L);
        question1.setScore(BigDecimal.ONE);
        questions.add(question1);
        section1.setQuestions(questions);
        sections.add(section1);
        dto.setSections(sections);

        Exam exam = examConverter.teacherSaveExamDTOToExam(dto);
        System.out.println(exam);
    }
}
