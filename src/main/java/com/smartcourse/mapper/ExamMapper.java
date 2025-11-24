package com.smartcourse.mapper;

import com.smartcourse.pojo.dto.knowledge.ClassMapDTO;
import com.smartcourse.pojo.dto.knowledge.StudentMapDTO;
import com.smartcourse.pojo.entity.Exam;
import com.smartcourse.pojo.vo.exam.ExamList;
import com.smartcourse.pojo.vo.exam.items.TeacherGetExamItemVO;
import com.smartcourse.pojo.vo.knowledge.NodeQuestionVO;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

@Mapper
public interface ExamMapper {

    @Select("select * from evaluation_expert.exam where id = #{examId}")
    Exam getById(Long examId);

    @Insert("""
            INSERT INTO evaluation_expert.exam (
                name, description, notice, course_id, total_score, duration_minutes,
                start_time, pass_score, shuffle_questions, shuffle_options, status,
                version, creator, deleted,create_time,update_time
            ) VALUES (
                #{name}, #{description}, #{notice}, #{courseId}, #{totalScore}, #{durationMinutes},
                #{startTime}, #{passScore}, #{shuffleQuestions}, #{shuffleOptions}, #{status},
                #{version}, #{creator}, #{deleted}, NOW(), NOW()
            )
            """)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insertExam(Exam exam);

    int updateExamRecursive(Exam exam);

    int updateExamSelective(Exam exam);

    List<ExamList> getList(Long courseId);

    List<TeacherGetExamItemVO> getTeacherGetExamItemVOListByCourseId(@Param("courseId") Long courseId);

    @Update("UPDATE evaluation_expert.exam SET deleted = 1 WHERE id = #{examId}")
    int deleteExamById(@Param("examId") Long examId);

    @Select("select course_id from evaluation_expert.exam where id = #{examId}")
    Long getCourseId(Long examId);

    List<NodeQuestionVO> getQuestion(StudentMapDTO studentMapDTO);

    List<NodeQuestionVO> getClassQuestion(ClassMapDTO classMapDTO);
}
