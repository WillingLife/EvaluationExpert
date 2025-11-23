package com.smartcourse.converter;

import com.smartcourse.pojo.dto.dify.DifyKnowledgeNodeDTO;
import com.smartcourse.pojo.entity.KnowledgeNode;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface KnowledgeNodeConverter {

    DifyKnowledgeNodeDTO knowledgeNodeToDifyDTO(KnowledgeNode knowledgeNode);

    List<DifyKnowledgeNodeDTO> knowledgeNodesToDifyDTOs(List<KnowledgeNode> knowledgeNodes);

}
