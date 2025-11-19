package com.smartcourse.infra.http.dify;

import com.smartcourse.infra.http.dify.annotations.GradeShortQuestionClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DifyClientGateway {
    @GradeShortQuestionClient
    private final DifyClient gradeShortQuestionClient;

    public DifyClient gradeShortQuestionClient() {
        return gradeShortQuestionClient;
    }

}
