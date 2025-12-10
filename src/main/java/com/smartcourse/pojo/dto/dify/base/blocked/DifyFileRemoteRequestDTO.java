package com.smartcourse.pojo.dto.dify.base.blocked;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class DifyFileRemoteRequestDTO {
    private String transferMethod="remote_url";
    private String url;
    private String type;

    public static DifyFileRemoteRequestDTO createDocument(String url){
        DifyFileRemoteRequestDTO dto = new DifyFileRemoteRequestDTO();
        dto.transferMethod="remote_url";
        dto.url=url;
        dto.type="document";
        return dto;
    }

}
