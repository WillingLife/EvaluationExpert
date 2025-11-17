package com.smartcourse.mapper;

import com.smartcourse.pojo.dto.exam.ExamScoreItemDTO;
import com.smartcourse.pojo.entity.ExamScoreItem;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ExamScoreItemMapper {
    int batchUpdateExamScoreItemSelective(List<ExamScoreItem> examScoreItems);

    void submit(List<ExamScoreItemDTO> examScoreItems);
}
