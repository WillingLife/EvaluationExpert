package com.smartcourse.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.io.Resource;

@Data
@ConfigurationProperties(prefix = "application.exam.prompts")
public class ExamPromptsProperties {
    private Resource system;
    private Resource user;
}
