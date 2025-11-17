package com.smartcourse.mapper;

import com.smartcourse.pojo.entity.Exam;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface ExamMapper {

    @Select("select * from evaluation_expert.exam where id = #{examId}")
    Exam getById(Long examId);

    @Insert("""
            INSERT INTO evaluation_expert.exam (
                name, description, notice, course_id, total_score, duration_minutes,
                start_time, pass_score, shuffle_questions, shuffle_options, status,
                version, creator, deleted
            ) VALUES (
                #{name}, #{description}, #{notice}, #{courseId}, #{totalScore}, #{durationMinutes},
                #{startTime}, #{passScore}, #{shuffleQuestions}, #{shuffleOptions}, #{status},
                #{version}, #{creator}, #{deleted}
            )
            """)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insertExam(Exam exam);

    int updateExamRecursive(Exam exam);

    int updateExamSelective(Exam exam);

}
