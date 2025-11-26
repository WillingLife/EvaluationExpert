package com.smartcourse.infra.http.dify;

import com.smartcourse.infra.http.dify.annotations.*;
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

    @GradeAssignmentClient
    private final DifyClient gradingAssignmentClient;

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

    public DifyClient gradeAssignmentClient() {
        return gradingAssignmentClient;
    }

    public DifyClientGateway(@GradeShortQuestionClient DifyClient gradeShortQuestionClient,
                             @PolishAssignmentClient DifyClient polishAssignmentClient,
                             @MappingKnowledgeClient DifyClient mappingKnowledgeClient,
                             @ExamGenerateQueryClient DifyClient examGenerateQueryClient,
                             @GradeAssignmentClient DifyClient gradeAssignmentClient) {
        this.gradeShortQuestionClient = gradeShortQuestionClient;
        this.polishAssignmentClient = polishAssignmentClient;
        this.mappingKnowledgeClient = mappingKnowledgeClient;
        this.examGenerateQueryClient = examGenerateQueryClient;
        this.gradingAssignmentClient = gradeAssignmentClient;
    }

}
