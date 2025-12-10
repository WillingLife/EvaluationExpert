package com.smartcourse.infra.ai;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ExamGenerationContext {
    private String formatInstructions;
    private String userPrompt;
    private String selectedQuestionJson;
    private Long courseId;
    private String knowledgeJson;
    private String context;
}
