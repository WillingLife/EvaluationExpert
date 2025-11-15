package com.smartcourse.mapper;

import com.smartcourse.pojo.entity.Question;
import com.smartcourse.pojo.vo.QuestionQueryVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface QuestionMapper {
    /**
     * 新增题目信息
     * @param question 题目信息
     */
    void insert(Question question);

    /**
     * 分页查询题目列表
     */
    List<QuestionQueryVO> pageList(Long courseId, String stem, Integer difficulty,
                                   Integer type, Integer offset, Integer pageSize);

    /**
     * 统计题目数量
     */
    Long count(Long courseId, String stem, Integer difficulty, Integer type);

    /**
     * 根据ID查询题目
     */
    Question selectById(Long id);

    /**
     * 更新题目信息
     * @param question 新题目信息
     */
    void update(Question question);
}
