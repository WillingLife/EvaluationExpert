package com.smartcourse.service.impl;
import com.smartcourse.converter.QuestionElasticSearchConverter;
import com.smartcourse.pojo.dto.QuestionElasticSearchDTO;
import com.smartcourse.model.QuestionDocument;
import com.smartcourse.repository.QuestionDocumentRepository;
import com.smartcourse.service.QuestionElasticSearchService;
import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.DocumentSplitter;
import dev.langchain4j.data.document.splitter.DocumentSplitters;
import dev.langchain4j.data.segment.TextSegment;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class QuestionElasticSearchServiceImpl implements QuestionElasticSearchService {

    private static final int CHUNK_THRESHOLD = 1000;
    private static final int CHUNK_SIZE = 800;
    private static final int CHUNK_OVERLAP = 80;

    private final QuestionDocumentRepository repository;
    private final ObjectProvider<EmbeddingModel> embeddingModelProvider;
    private final QuestionElasticSearchConverter questionElasticSearchConverter;
    private final DocumentSplitter documentSplitter = DocumentSplitters.recursive(CHUNK_SIZE, CHUNK_OVERLAP);

    @Override
    public void addQuestionDocument(QuestionElasticSearchDTO dto) {
        if (dto == null) {
            throw new IllegalArgumentException("QuestionElasticSearchDTO 不能为空");
        }

        QuestionDocument doc = questionElasticSearchConverter.QuestionElasticSearchDTOToQuestionDocument(dto);

        EmbeddingModel model = embeddingModelProvider.getIfAvailable();
        boolean hasTextForEmbedding = StringUtils.hasText(dto.getQuestionText())
                || StringUtils.hasText(dto.getAnswerText());

        if (model == null && hasTextForEmbedding) {
            log.warn("嵌入模型bean不可用。跳过问题id的向量生成 {}", dto.getId());
        }

        doc.setQuestionVectorChunks(createQuestionChunks(dto.getQuestionText(), model));
        doc.setAnswerVectorChunks(createAnswerChunks(dto.getAnswerText(), model));

        repository.save(doc);
    }

    /**
     * 将文本转化为 {@link QuestionDocument.QuestionVectorChunk}列表
     *
     * @param text 题目文本
     * @param model embedding 模型
     * @return
     */
    private List<QuestionDocument.QuestionVectorChunk> createQuestionChunks(String text, EmbeddingModel model) {
        List<String> chunks = splitText(text);
        if (chunks.isEmpty()) {
            return null;
        }

        List<QuestionDocument.QuestionVectorChunk> vectorChunks = new ArrayList<>(chunks.size());
        for (int i = 0; i < chunks.size(); i++) {
            String chunkText = chunks.get(i);
            QuestionDocument.QuestionVectorChunk chunk = new QuestionDocument.QuestionVectorChunk();
            chunk.setChunkId(i+1);
            chunk.setChunkText(chunkText);
            if (model != null) {
                chunk.setChunkVector(model.embed(chunkText));
            }
            vectorChunks.add(chunk);
        }
        return vectorChunks;
    }

    /**
     * 将答案文本切分为{@link QuestionDocument.AnswerVectorChunk}
     * @param text 答案文本
     * @param model embedding 模型
     * @return QuestionDocument.AnswerVectorChunk列表
     */
    private List<QuestionDocument.AnswerVectorChunk> createAnswerChunks(String text, EmbeddingModel model) {
        List<String> chunks = splitText(text);
        if (chunks.isEmpty()) {
            return null;
        }

        List<QuestionDocument.AnswerVectorChunk> vectorChunks = new ArrayList<>(chunks.size());
        for (int i = 0; i < chunks.size(); i++) {
            String chunkText = chunks.get(i);
            QuestionDocument.AnswerVectorChunk chunk = new QuestionDocument.AnswerVectorChunk();
            chunk.setChunkId(i+1);
            chunk.setChunkText(chunkText);
            if (model != null) {
                chunk.setChunkVector(model.embed(chunkText));
            }
            vectorChunks.add(chunk);
        }
        return vectorChunks;
    }

    /**
     * 切分文本
     * @param text 待切分文本
     * @return
     */
    private List<String> splitText(String text) {
        if (!StringUtils.hasText(text)) {
            return List.of();
        }
        String normalized = text.trim();
        if (normalized.length() <= CHUNK_THRESHOLD) {
            return List.of(normalized);
        }

        List<TextSegment> segments = documentSplitter.split(Document.from(normalized));
        return segments.stream()
                .map(TextSegment::text)
                .toList();
    }
}


