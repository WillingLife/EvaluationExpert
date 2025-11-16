package com.smartcourse.mapper;

import com.smartcourse.pojo.entity.ExamScore;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ExamScoreMapper {

    /**
     * 根据id更新examScore非空字段
     * @param examScore 更新值
     * @return 更新数量
     */
    int updateExamScoreSelective(ExamScore examScore);
}
