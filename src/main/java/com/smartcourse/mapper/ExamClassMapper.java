package com.smartcourse.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ExamClassMapper {

    /**
     * 替换考试的班级ID（删除现有的，然后插入提供的）。
     */
    int replaceExamClasses(@Param("examId") Long examId, @Param("classIds") List<Long> classIds);

    void deleteExamClassesByExamId(@Param("examId") Long examId);
    void insertExamClasses(@Param("examId") Long examId, @Param("classIds") List<Long> classIds);
}
