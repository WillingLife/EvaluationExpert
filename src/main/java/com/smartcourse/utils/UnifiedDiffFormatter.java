package com.smartcourse.utils;

import org.bitbucket.cowwoc.diffmatchpatch.DiffMatchPatch;
import org.springframework.util.StringUtils;

import java.util.LinkedList;

/**
 * Formats textual differences into a unified diff body that diff2html can render.
 */
public final class UnifiedDiffFormatter {

    private static final String LF = "\n";

    private UnifiedDiffFormatter() {
    }

    public static String format(String leftContent, String rightContent) {
        String normalizedLeft = normalize(leftContent);
        String normalizedRight = normalize(rightContent);

        DiffMatchPatch differ = new DiffMatchPatch();
        LinkedList<DiffMatchPatch.Diff> diffs = differ.diffMain(normalizedLeft, normalizedRight);
        if (diffs.isEmpty()) {
            return buildEmptyDiffBlock();
        }
        boolean onlyEqual = diffs.stream().allMatch(diff -> diff.operation == DiffMatchPatch.Operation.EQUAL);
        differ.diffCleanupSemantic(diffs);
        if (onlyEqual) {
            return buildIdenticalDiffBlock(normalizedLeft);
        }
        return buildUnifiedBlock(diffs, countLines(normalizedLeft), countLines(normalizedRight));
    }

    private static String normalize(String text) {
        if (text == null) {
            return "";
        }
        return text.replace("\r\n", "\n").replace('\r', '\n');
    }

    private static int countLines(String text) {
        if (!StringUtils.hasLength(text)) {
            return 0;
        }
        int lines = 1;
        for (int i = 0; i < text.length(); i++) {
            if (text.charAt(i) == '\n') {
                lines++;
            }
        }
        return lines;
    }

    private static String buildUnifiedBlock(LinkedList<DiffMatchPatch.Diff> diffs,
                                            int leftLines,
                                            int rightLines) {
        StringBuilder builder = new StringBuilder();
        builder.append("@@ -").append(startLine(leftLines)).append(",").append(leftLines)
                .append(" +").append(startLine(rightLines)).append(",").append(rightLines)
                .append(" @@")
                .append(LF);
        for (DiffMatchPatch.Diff diff : diffs) {
            if (diff.text == null || diff.text.isEmpty()) {
                continue;
            }
            char prefix = prefixFor(diff.operation);
            String[] lines = diff.text.split("\n", -1);
            for (int i = 0; i < lines.length; i++) {
                String line = lines[i];
                boolean isTrailingEmpty = i == lines.length - 1 && line.isEmpty();
                if (isTrailingEmpty) {
                    continue;
                }
                builder.append(prefix).append(line).append(LF);
            }
        }
        return builder.toString();
    }

    private static char prefixFor(DiffMatchPatch.Operation operation) {
        return switch (operation) {
            case INSERT -> '+';
            case DELETE -> '-';
            default -> ' ';
        };
    }

    private static int startLine(int lines) {
        return lines > 0 ? 1 : 0;
    }

    private static String buildIdenticalDiffBlock(String content) {
        int lineCount = countLines(content);
        StringBuilder builder = new StringBuilder();
        builder.append("@@ -").append(startLine(lineCount)).append(",").append(lineCount)
                .append(" +").append(startLine(lineCount)).append(",").append(lineCount)
                .append(" @@")
                .append(LF);
        if (lineCount == 0) {
            builder.append(" No differences found").append(LF);
            return builder.toString();
        }
        String[] lines = content.split("\n", -1);
        for (int i = 0; i < lines.length; i++) {
            String line = lines[i];
            boolean isTrailingEmpty = i == lines.length - 1 && line.isEmpty();
            if (isTrailingEmpty) {
                continue;
            }
            builder.append(' ').append(line).append(LF);
        }
        return builder.toString();
    }

    private static String buildEmptyDiffBlock() {
        return "@@ -0,0 +0,0 @@" + LF + " No differences found" + LF;
    }
}
