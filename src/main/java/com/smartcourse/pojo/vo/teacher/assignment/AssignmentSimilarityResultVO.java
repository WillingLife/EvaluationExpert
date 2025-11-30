package com.smartcourse.pojo.vo.teacher.assignment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AssignmentSimilarityResultVO {
    private Long leftAssignmentId;
    private Long rightAssignmentId;
    /**
     * Similarity calculated from simhash hamming distance.
     */
    private double simHashScore;
    /**
     * Winnowing similarity value (0-1).
     */
    private double winnowingScore;
    /**
     * Unified diff formatted string that works with diff2html in the frontend.
     */
    private String diff;
    private String leftAssignmentUrl;
    private String rightAssignmentUrl;
    private String leftContent;
    private String rightContent;
}
