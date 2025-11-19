package com.smartcourse.infra.rabbitmq;

import java.io.Serial;
import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * gradeShortQuestion任务的有效负载。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GradeShortQuestionTaskMessage implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long scoreId;

    private Long examItemId;
}
