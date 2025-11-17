package com.smartcourse.mapper;

import com.smartcourse.pojo.entity.ExamScore;
import com.smartcourse.pojo.vo.exam.TeacherViewAnswerItemVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

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
}
