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
}
