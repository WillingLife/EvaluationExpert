package com.smartcourse.mapper;

import com.smartcourse.pojo.entity.AssignmentDimensionRemark;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface AssignmentDimensionRemarkMapper {
    void insert(AssignmentDimensionRemark remark);
    List<AssignmentDimensionRemark> selectByAssignmentRemarkId(@Param("assignmentRemarkId") Long assignmentRemarkId);
}