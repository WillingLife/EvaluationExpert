package com.smartcourse.pojo.dto.exam.stream;

public sealed interface ExamGenStreamPayload permits ExamGeneratingPayload, ExamFinishPayload {
}
