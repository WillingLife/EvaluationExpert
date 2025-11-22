package com.smartcourse.infra.http.dify;

import com.smartcourse.infra.http.dify.annotations.ExamGenerateQueryClient;
import com.smartcourse.infra.http.dify.annotations.GradeShortQuestionClient;
import com.smartcourse.infra.http.dify.annotations.MappingKnowledgeClient;
import com.smartcourse.infra.http.dify.annotations.PolishAssignmentClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
public class DifyClientGateway {
    @GradeShortQuestionClient
    private final DifyClient gradeShortQuestionClient;

    @PolishAssignmentClient
    private final DifyClient polishAssignmentClient;

    @MappingKnowledgeClient
    private final DifyClient mappingKnowledgeClient;

    @ExamGenerateQueryClient
    private final DifyClient examGenerateQueryClient;

    public DifyClient gradeShortQuestionClient() {
        return gradeShortQuestionClient;
    }

    public DifyClient polishAssignmentClient() {
        return polishAssignmentClient;
    }

    public DifyClient mappingKnowledgeClient() {
        return mappingKnowledgeClient;
    }

    public DifyClient examGenerateQueryClient() {
        return examGenerateQueryClient;
    }

    public DifyClientGateway(@GradeShortQuestionClient DifyClient gradeShortQuestionClient,
                             @PolishAssignmentClient DifyClient polishAssignmentClient,
                             @MappingKnowledgeClient DifyClient mappingKnowledgeClient,
                             @ExamGenerateQueryClient DifyClient examGenerateQueryClient) {
        this.gradeShortQuestionClient = gradeShortQuestionClient;
        this.polishAssignmentClient = polishAssignmentClient;
        this.mappingKnowledgeClient = mappingKnowledgeClient;
        this.examGenerateQueryClient = examGenerateQueryClient;
    }

}
