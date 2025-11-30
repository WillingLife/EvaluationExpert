package com.smartcourse.service.impl;

import com.hankcs.hanlp.seg.common.Term;
import com.hankcs.hanlp.tokenizer.SpeedTokenizer;
import com.smartcourse.pojo.dto.AssignmentSimilarityRequestDTO;
import com.smartcourse.pojo.vo.teacher.assignment.AssignmentSimilarityResultVO;
import com.smartcourse.service.AssignmentSimilarityService;
import com.smartcourse.utils.AliyunOSSOperator;
import com.smartcourse.utils.HttpClientUtil;
import com.smartcourse.utils.UnifiedDiffFormatter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bitbucket.cowwoc.diffmatchpatch.DiffMatchPatch;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AssignmentSimilarityServiceImpl implements AssignmentSimilarityService {

    private static final double DEFAULT_SIMILARITY_THRESHOLD = 0.40D;
    private static final double SIMHASH_GATE = 0.40D;
    private static final int SIMHASH_BITS = 64;
    private static final int K_GRAM_SIZE = 5;
    private static final int WINDOW_SIZE = 4;
    private static final char TOKEN_SEPARATOR = '\u0001';
    private static final long FNV_OFFSET = 0xcbf29ce484222325L;
    private static final long FNV_PRIME = 1099511628211L;

    private final AliyunOSSOperator aliyunOSSOperator;

    @Override
    public List<AssignmentSimilarityResultVO> detectSimilarAssignments(AssignmentSimilarityRequestDTO requestDTO) {
        if (requestDTO == null || CollectionUtils.isEmpty(requestDTO.getAssignments()) || requestDTO.getAssignments().size() < 2) {
            return Collections.emptyList();
        }

        List<AssignmentTextContext> contexts = requestDTO.getAssignments().stream()
                .map(this::buildContext)
                .filter(Objects::nonNull)
                .toList();

        if (contexts.size() < 2) {
            return Collections.emptyList();
        }

        double threshold = requestDTO.getThreshold() > 0 ? requestDTO.getThreshold() : DEFAULT_SIMILARITY_THRESHOLD;
        List<AssignmentSimilarityResultVO> results = new ArrayList<>();

        for (int i = 0; i < contexts.size(); i++) {
            AssignmentTextContext left = contexts.get(i);
            for (int j = i + 1; j < contexts.size(); j++) {
                AssignmentTextContext right = contexts.get(j);
                double simHashScore = calculateSimHashScore(left.getSimHash(), right.getSimHash());
                if (simHashScore < SIMHASH_GATE) {
                    continue;
                }

                double winnowingScore = calculateWinnowingSimilarity(left.getFingerprints(), right.getFingerprints());
                if (winnowingScore < threshold) {
                    continue;
                }

                String diff = buildDiff(left, right);
                results.add(AssignmentSimilarityResultVO.builder()
                        .leftAssignmentId(left.getAssignmentId())
                        .rightAssignmentId(right.getAssignmentId())
                        .simHashScore(simHashScore)
                        .winnowingScore(winnowingScore)
                        .diff(diff)
                        .leftAssignmentUrl(left.getAssignmentUrl())
                        .rightAssignmentUrl(right.getAssignmentUrl())
                        .leftContent(left.getContent())
                        .rightContent(right.getContent())
                        .build());
            }
        }

        return results.stream()
                .sorted((a, b) -> Double.compare(b.getWinnowingScore(), a.getWinnowingScore()))
                .toList();
    }

    private AssignmentTextContext buildContext(AssignmentSimilarityRequestDTO.AssignmentTextItem item) {
        if (item == null || item.getId() == null || !StringUtils.hasText(item.getName())) {
            return null;
        }
        String url = aliyunOSSOperator.getUrl(item.getName());
        if (!StringUtils.hasText(url)) {
            log.warn("Failed to create presigned url for assignment: {}", item.getName());
            return null;
        }
        String content = HttpClientUtil.doGet(url, null);
        if (!StringUtils.hasText(content)) {
            log.warn("Failed to fetch assignment content from oss object: {}", item.getName());
            return null;
        }
        String normalizedContent = normalizeContent(content);
        List<String> tokens = tokenize(normalizedContent);
        long simHash = computeSimHash(tokens);
        Set<Long> fingerprints = buildFingerprintSet(tokens);
        return new AssignmentTextContext(item.getId(), item.getName(), url, normalizedContent, simHash, fingerprints);
    }

    private List<String> tokenize(String content) {
        if (!StringUtils.hasText(content)) {
            return Collections.emptyList();
        }
        List<Term> terms = SpeedTokenizer.segment(content);
        List<String> tokens = new ArrayList<>(terms.size());
        for (Term term : terms) {
            if (term == null || !StringUtils.hasText(term.word)) {
                continue;
            }
            String word = term.word.trim();
            if (StringUtils.hasText(word)) {
                tokens.add(word);
            }
        }
        return tokens;
    }

    private long computeSimHash(List<String> tokens) {
        if (CollectionUtils.isEmpty(tokens)) {
            return 0L;
        }
        int[] bits = new int[SIMHASH_BITS];
        for (String token : tokens) {
            long hash = fnv64(token);
            for (int i = 0; i < SIMHASH_BITS; i++) {
                if (((hash >>> i) & 1L) == 1L) {
                    bits[i] += 1;
                } else {
                    bits[i] -= 1;
                }
            }
        }
        long result = 0L;
        for (int i = 0; i < SIMHASH_BITS; i++) {
            if (bits[i] > 0) {
                result |= (1L << i);
            }
        }
        return result;
    }

    private Set<Long> buildFingerprintSet(List<String> tokens) {
        List<Fingerprint> fingerprints = runWinnowing(tokens);
        if (fingerprints.isEmpty()) {
            return Collections.emptySet();
        }
        return fingerprints.stream()
                .map(Fingerprint::hash)
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    private List<Fingerprint> runWinnowing(List<String> tokens) {
        List<Long> shingleHashes = buildShingleHashes(tokens);
        if (shingleHashes.isEmpty()) {
            return Collections.emptyList();
        }

        int window = Math.min(WINDOW_SIZE, shingleHashes.size());
        List<Fingerprint> fingerprints = new ArrayList<>();
        Deque<Integer> deque = new ArrayDeque<>();

        for (int index = 0; index < shingleHashes.size(); index++) {
            long currentHash = shingleHashes.get(index);
            while (!deque.isEmpty() && shingleHashes.get(deque.peekLast()) >= currentHash) {
                deque.pollLast();
            }
            deque.addLast(index);

            int windowStart = index - window + 1;
            if (deque.peekFirst() != null && deque.peekFirst() < windowStart) {
                deque.pollFirst();
            }

            if (index >= window - 1) {
                Integer minIndex = deque.peekFirst();
                if (minIndex != null) {
                    Fingerprint candidate = new Fingerprint(shingleHashes.get(minIndex), minIndex);
                    if (fingerprints.isEmpty() || fingerprints.get(fingerprints.size() - 1).position != candidate.position) {
                        fingerprints.add(candidate);
                    }
                }
            }
        }
        return fingerprints;
    }

    private List<Long> buildShingleHashes(List<String> tokens) {
        if (CollectionUtils.isEmpty(tokens) || tokens.size() < K_GRAM_SIZE) {
            return Collections.emptyList();
        }
        List<Long> hashes = new ArrayList<>(tokens.size() - K_GRAM_SIZE + 1);
        for (int i = 0; i <= tokens.size() - K_GRAM_SIZE; i++) {
            StringBuilder shingle = new StringBuilder();
            for (int j = 0; j < K_GRAM_SIZE; j++) {
                shingle.append(tokens.get(i + j)).append(TOKEN_SEPARATOR);
            }
            hashes.add(fnv64(shingle.toString()));
        }
        return hashes;
    }

    private double calculateSimHashScore(long left, long right) {
        int distance = Long.bitCount(left ^ right);
        return 1D - ((double) distance / SIMHASH_BITS);
    }

    private double calculateWinnowingSimilarity(Set<Long> left, Set<Long> right) {
        if (CollectionUtils.isEmpty(left) || CollectionUtils.isEmpty(right)) {
            return 0D;
        }
        Set<Long> leftCopy = new HashSet<>(left);
        leftCopy.retainAll(right);
        if (leftCopy.isEmpty()) {
            return 0D;
        }
        Set<Long> union = new HashSet<>(left);
        union.addAll(right);
        return union.isEmpty() ? 0D : (double) leftCopy.size() / union.size();
    }

    private String buildDiff(AssignmentTextContext left, AssignmentTextContext right) {
        String patchText = UnifiedDiffFormatter.format(left.getContent(), right.getContent());
        String leftDisplay = buildDisplayName(left);
        String rightDisplay = buildDisplayName(right);
        return "diff --git a/" + leftDisplay + " b/" + rightDisplay + System.lineSeparator() +
                "--- a/" + leftDisplay + System.lineSeparator() +
                "+++ b/" + rightDisplay + System.lineSeparator() +
                patchText;
    }

    private String buildDisplayName(AssignmentTextContext ctx) {
        return "assignment-" + ctx.getAssignmentId() + ".txt";
    }

    private String normalizeContent(String content) {
        return content.replace("\r\n", "\n").replace('\r', '\n');
    }

    private long fnv64(String value) {
        long hash = FNV_OFFSET;
        for (int i = 0; i < value.length(); i++) {
            hash ^= value.charAt(i);
            hash *= FNV_PRIME;
        }
        return hash;
    }

    private record Fingerprint(long hash, int position) {
    }

    private static final class AssignmentTextContext {
        private final Long assignmentId;
        private final String objectName;
        private final String assignmentUrl;
        private final String content;
        private final long simHash;
        private final Set<Long> fingerprints;

        private AssignmentTextContext(Long assignmentId, String objectName, String assignmentUrl, String content, long simHash, Set<Long> fingerprints) {
            this.assignmentId = assignmentId;
            this.objectName = objectName;
            this.assignmentUrl = assignmentUrl;
            this.content = content;
            this.simHash = simHash;
            this.fingerprints = fingerprints;
        }

        public Long getAssignmentId() {
            return assignmentId;
        }

        public String getObjectName() {
            return objectName;
        }

        public String getAssignmentUrl() {
            return assignmentUrl;
        }

        public String getContent() {
            return content;
        }

        public long getSimHash() {
            return simHash;
        }

        public Set<Long> getFingerprints() {
            return fingerprints;
        }
    }
}
