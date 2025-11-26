package com.smartcourse.mapper;

import com.smartcourse.pojo.dto.StudentGetExamDTO;
import com.smartcourse.pojo.entity.ExamScore;
import com.smartcourse.pojo.vo.exam.TeacherViewAnswerItemVO;
import com.smartcourse.pojo.vo.exam.ExamScoreVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Mapper
public interface ExamScoreMapper {

    /**
     * 根据id更新examScore非空字段
     * @param examScore 更新值
     * @return 更新数量
     */
    int updateExamScoreSelective(ExamScore examScore);

    List<TeacherViewAnswerItemVO> getStudentAnswers(@Param("examId") Long examId, @Param("studentId") Long studentId);

    Long submit(ExamScore examScore);

    ExamScoreVO getScore(StudentGetExamDTO studentGetExamDTO);

    @Update("update evaluation_expert.exam_score set total_score = #{totalScore} where id = #{scoreId}")
    void addScore(Long scoreId, BigDecimal totalScore);

    @Select("select * from exam_score where exam_id=#{examId} and student_id=#{studentId};")
    ExamScore getExamScoreByExamIdAndStudentId(@Param("examId") Long examId, @Param("studentId") Long studentId);
}
