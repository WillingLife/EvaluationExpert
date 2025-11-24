package com.smartcourse.service;

import com.smartcourse.pojo.dto.knowledge.StudentMapDTO;
import com.smartcourse.pojo.vo.knowledge.MapVO;
import com.smartcourse.pojo.vo.knowledge.StudentMapVO;

public interface KnowledgeService {
    StudentMapVO getStudentMap(StudentMapDTO studentMapDTO);

    MapVO getMap(Long courseId);
}
