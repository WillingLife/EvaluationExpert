package com.smartcourse.service;

import com.smartcourse.pojo.dto.AssignmentSimilarityRequestDTO;
import com.smartcourse.pojo.vo.teacher.assignment.AssignmentSimilarityResultVO;

import java.util.List;

public interface AssignmentSimilarityService {

    /**
     * Compare assignment submissions and return suspicious pairs. The returned VO items must contain the
     * inline unified diff text that diff2html can consume directly rather than a remote URL because the
     * frontend renders the diff content itself in this decoupled architecture.
     *
     * @param requestDTO assignment metadata list + threshold.
     * @return suspicious assignment pairs sorted by similarity desc, each with diff2html friendly text.
     */
    List<AssignmentSimilarityResultVO> detectSimilarAssignments(AssignmentSimilarityRequestDTO requestDTO);
}
