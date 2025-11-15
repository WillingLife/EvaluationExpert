package com.smartcourse.mapper;

import com.smartcourse.pojo.entity.AssignmentRemark;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface AssignmentRemarkMapper {
    void insert(AssignmentRemark remark);
    AssignmentRemark selectByScoreId(@Param("scoreId") Long scoreId);
}