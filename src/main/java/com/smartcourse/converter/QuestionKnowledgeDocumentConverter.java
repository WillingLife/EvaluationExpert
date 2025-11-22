package com.smartcourse.converter;

import com.smartcourse.model.KnowledgePoint;
import com.smartcourse.model.QuestionKnowledgeDocument;
import com.smartcourse.pojo.vo.dify.DifyMappingKnowledgeItemVO;
import com.smartcourse.pojo.vo.dify.DifyMappingKnowledgeVO;
import org.mapstruct.Mapper;

import java.util.ArrayList;
import java.util.List;

@Mapper(componentModel = "spring")
public interface QuestionKnowledgeDocumentConverter {

   default QuestionKnowledgeDocument difyKnowledgeVoToKnowledgeDocument(DifyMappingKnowledgeVO vo,Long id,Long courseId){
       QuestionKnowledgeDocument  document = new QuestionKnowledgeDocument();
       List<KnowledgePoint> knowledgePoints = new ArrayList<>();
       for(DifyMappingKnowledgeItemVO itemVO: vo.getNodes()){
           KnowledgePoint  knowledgePoint = new KnowledgePoint();
           knowledgePoint.setId(itemVO.getId());
           knowledgePoint.setWeight(itemVO.getWeight());
           knowledgePoints.add(knowledgePoint);
       }
       document.setId(id);
       document.setCourseId(courseId);
       document.setKnowledgePoints(knowledgePoints);
       return document;
   }

}
