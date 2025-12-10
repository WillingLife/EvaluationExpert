package com.smartcourse.service;

import com.smartcourse.pojo.dto.AssignmentSimilarityRequestDTO;
import com.smartcourse.pojo.vo.teacher.assignment.AssignmentSimilarityResultVO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class AssignmentServiceTests {
    @Autowired
    private AssignmentSimilarityService assignmentSimilarityService;

    @Test
    public void test(){
        AssignmentSimilarityRequestDTO.AssignmentTextItem item1 = new AssignmentSimilarityRequestDTO.AssignmentTextItem(1L,"5f1bfdd9-8a38-42dc-85c5-de86111f3fdf.txt");
        AssignmentSimilarityRequestDTO.AssignmentTextItem ite2 = new AssignmentSimilarityRequestDTO.AssignmentTextItem(2L,"3e6ea8d8-9b35-40c9-b848-eeb7bbdb0b9b.txt");
        AssignmentSimilarityRequestDTO.AssignmentTextItem ite3 = new AssignmentSimilarityRequestDTO.AssignmentTextItem(3L,"111.txt");
        AssignmentSimilarityRequestDTO.AssignmentTextItem ite4 = new AssignmentSimilarityRequestDTO.AssignmentTextItem(4L,"222.txt");
        AssignmentSimilarityRequestDTO.AssignmentTextItem ite5 = new AssignmentSimilarityRequestDTO.AssignmentTextItem(5L,"222.txt");
        AssignmentSimilarityRequestDTO.AssignmentTextItem ite6 = new AssignmentSimilarityRequestDTO.AssignmentTextItem(6L,"3e6ea8d8-9b35-40c9-b848-eeb7bbdb0b9b.txt");
        AssignmentSimilarityRequestDTO dto = new AssignmentSimilarityRequestDTO(List.of(item1,ite2,ite3,ite4,ite5,ite6),0);
        List<AssignmentSimilarityResultVO> assignmentSimilarityResultVOS = assignmentSimilarityService.detectSimilarAssignments(dto);
        System.out.println(assignmentSimilarityResultVOS);

    }
}
