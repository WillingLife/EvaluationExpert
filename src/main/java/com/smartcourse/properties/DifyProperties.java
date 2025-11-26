package com.smartcourse.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "dify")
public class DifyProperties {
    /**
     * dify 运行的base url
     */
    private String baseUrl;
    /**
     * 简答题评分系统key
     */
    private String gradeShortQuestionKey;
    /**
     * 润色作业key
     */
    private String polishAssignmentKey;

    /**
     * 映射知识点key
     */
    private String mappingKnowledgeKey;

    /**
     * 智能组卷部分，生成查询key
     */
    private String examGenerateQueryKey;

    /**
     * 智能批改key
     */
    private String gradeAssignmentKey;
}
