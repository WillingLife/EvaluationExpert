package com.smartcourse.repository.mongo;

import com.smartcourse.pojo.vo.teacher.assignment.AssignmentDetectDetailVO;
import com.smartcourse.pojo.vo.teacher.assignment.TeacherAssignDetectItemVO;
import lombok.RequiredArgsConstructor;
import org.bson.Document;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class AssignmentDetectRepository {

    private static final String SUMMARY_COLLECTION = "teacher_assignment_detect_summary";
    private static final String DETAIL_COLLECTION = "teacher_assignment_detect_detail";

    private final MongoTemplate mongoTemplate;

    public void saveSummary(Long assignmentId, List<TeacherAssignDetectItemVO> summary) {
        Query query = Query.query(Criteria.where("assignmentId").is(assignmentId));
        Update update = new Update()
                .set("assignmentId", assignmentId)
                .set("list", summary);
        mongoTemplate.upsert(query, update, SUMMARY_COLLECTION);
    }

    public void replaceDetails(Long assignmentId, List<AssignmentDetectDetailVO> details) {
        Query query = Query.query(Criteria.where("assignmentId").is(assignmentId));
        mongoTemplate.remove(query, DETAIL_COLLECTION);
        if (CollectionUtils.isEmpty(details)) {
            return;
        }
        List<Document> documents = details.stream()
                .map(detail -> new Document()
                        .append("assignmentId", assignmentId)
                        .append("leftAssignmentId", detail.getLeftAssignmentId())
                        .append("rightAssignmentId", detail.getRightAssignmentId())
                        .append("diff", detail.getDiff())
                        .append("leftContent", detail.getLeftContent())
                        .append("rightContent", detail.getRightContent()))
                .collect(Collectors.toList());
        mongoTemplate.insert(documents, DETAIL_COLLECTION);
    }

    public List<TeacherAssignDetectItemVO> getSummary(Long assignmentId) {
        Query query = Query.query(Criteria.where("assignmentId").is(assignmentId));
        Document document = mongoTemplate.findOne(query, Document.class, SUMMARY_COLLECTION);
        if (document == null) {
            return Collections.emptyList();
        }
        List<Document> list = document.getList("list", Document.class);
        if (CollectionUtils.isEmpty(list)) {
            return Collections.emptyList();
        }
        return list.stream()
                .map(item -> mongoTemplate.getConverter().read(TeacherAssignDetectItemVO.class, item))
                .collect(Collectors.toList());
    }

    public AssignmentDetectDetailVO getDetail(Long assignmentId, Long leftAssignmentId, Long rightAssignmentId) {
        Criteria normalOrder = Criteria.where("assignmentId").is(assignmentId)
                .and("leftAssignmentId").is(leftAssignmentId)
                .and("rightAssignmentId").is(rightAssignmentId);
        AssignmentDetectDetailVO detail = mongoTemplate.findOne(Query.query(normalOrder),
                AssignmentDetectDetailVO.class, DETAIL_COLLECTION);
        if (detail != null) {
            return detail;
        }
        Criteria reversedOrder = Criteria.where("assignmentId").is(assignmentId)
                .and("leftAssignmentId").is(rightAssignmentId)
                .and("rightAssignmentId").is(leftAssignmentId);
        return mongoTemplate.findOne(Query.query(reversedOrder), AssignmentDetectDetailVO.class, DETAIL_COLLECTION);
    }
}
