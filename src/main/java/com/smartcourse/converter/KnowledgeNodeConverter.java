package com.smartcourse.converter;

import com.smartcourse.pojo.dto.dify.AiInputKnowledgeNodeDTO;
import com.smartcourse.pojo.dto.dify.DifyKnowledgeNodeDTO;
import com.smartcourse.pojo.entity.KnowledgeNode;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface KnowledgeNodeConverter {

    AiInputKnowledgeNodeDTO knowledgeNodeToAiDTO(KnowledgeNode knowledgeNode);

    List<AiInputKnowledgeNodeDTO> knowledgeNodesToAiDTOs(List<KnowledgeNode> knowledgeNodes);

    DifyKnowledgeNodeDTO knowledgeNodeToDifyDTO(KnowledgeNode knowledgeNode);

    List<DifyKnowledgeNodeDTO> knowledgeNodesToDifyDTOs(List<KnowledgeNode> knowledgeNodes);

}
