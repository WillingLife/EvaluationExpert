package com.smartcourse.pojo.dto.dify;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.smartcourse.pojo.dto.dify.base.blocked.DifyFileRemoteRequestDTO;
import lombok.Data;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class DifyGradeAssignmentDTO {
    private String question;
    private DifyFileRemoteRequestDTO report;

    public DifyGradeAssignmentDTO(String question,String url){
        this.question=question;
        this.report = DifyFileRemoteRequestDTO.createDocument(url);
    }

    public DifyGradeAssignmentDTO() {
    }

}
