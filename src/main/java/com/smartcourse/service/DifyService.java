package com.smartcourse.service;

public interface DifyService {
    void mappingKnowledge(Long courseId, Long questionId, String question);

    void gradeAssignment(Long assignmentScoreId);
}
