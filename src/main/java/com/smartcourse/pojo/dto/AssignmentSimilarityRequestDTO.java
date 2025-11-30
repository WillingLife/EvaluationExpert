package com.smartcourse.pojo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Request dto used for assignment similarity detection.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AssignmentSimilarityRequestDTO {

    /**
     * Assignments that already have their text converted and stored in OSS.
     */
    private List<AssignmentTextItem> assignments;

    /**
     * Similarity threshold (0-1) required to mark two assignments as suspicious.
     */
    private double threshold;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AssignmentTextItem {
        private Long id;
        /**
         * Object name in OSS that points to the converted txt file.
         */
        private String name;
    }
}
