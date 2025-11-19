package com.smartcourse.infra.es.vo;

import com.smartcourse.model.QuestionDocument;

import java.util.List;
import java.util.Map;

/**
 * 单条命中记录（包含高亮与原始得分，方便应用层 rerank）。
 */
public record QuestionEsHit(QuestionDocument document,
                            double score,
                            Map<String, List<String>> highlights) {
}
