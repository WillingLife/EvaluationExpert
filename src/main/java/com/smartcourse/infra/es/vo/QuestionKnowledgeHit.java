package com.smartcourse.infra.es.vo;

/**
 * Knowledge-based ES hit containing question id and score.
 */
public record QuestionKnowledgeHit(Long id, double score) {
}
