package com.smartcourse.utils;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Utility that encapsulates the multi-select scoring rules described in exam_table_structure(2).md lines 85-190.
 * <p>
 * It relies on a strategy pattern so each scoring rule can be plugged independently, while callers only need to
 * construct {@link MultipleChoiceStrategyContext} with the relevant answer statistics.
 */
public final class MultipleChoiceScoreCalculator {

    private static final int DEFAULT_PROPORTIONAL_SCALE = 8;

    private MultipleChoiceScoreCalculator() {
    }

    public static BigDecimal calculate(String strategyCode, MultipleChoiceStrategyContext context) {
        return calculate(StrategyType.fromCode(strategyCode), context);
    }

    public static BigDecimal calculate(StrategyType strategyType, MultipleChoiceStrategyContext context) {
        Objects.requireNonNull(strategyType, "strategyType must not be null");
        Objects.requireNonNull(context, "context must not be null");
        BigDecimal score = strategyType.apply(context);
        return score;
    }

    public enum StrategyType {
        ALL_OR_NOTHING("all_or_nothing_if_miss", ctx -> {
            if (ctx.getMissedCorrectCount() > 0 || ctx.getIncorrectlySelectedCount() > 0) {
                return BigDecimal.ZERO;
            }
            return ctx.getFullScore();
        }),
        FIXED_ON_MISS("fixed_on_miss", ctx -> {
            if (ctx.getIncorrectlySelectedCount() > 0) {
                return BigDecimal.ZERO;
            }
            if (ctx.getMissedCorrectCount() > 0) {
                BigDecimal fallback = getBigDecimal(ctx.config(), "score_on_any_miss", BigDecimal.ZERO);
                return clampScore(fallback, ctx.getFullScore(), BigDecimal.ZERO, ctx.config());
            }
            return ctx.getFullScore();
        }),
        PROPORTIONAL("proportional", ctx -> {
            int totalCorrectOptions = ctx.getCorrectlySelectedCount() + ctx.getMissedCorrectCount();
            if (totalCorrectOptions <= 0) {
                return BigDecimal.ZERO;
            }
            BigDecimal ratio = BigDecimal.valueOf(ctx.getCorrectlySelectedCount())
                .divide(BigDecimal.valueOf(totalCorrectOptions), DEFAULT_PROPORTIONAL_SCALE, RoundingMode.HALF_UP);
            BigDecimal raw = ctx.getFullScore().multiply(ratio);
            BigDecimal scaled = applyScale(raw, ctx.config());
            return clampScore(scaled, ctx.getFullScore(), BigDecimal.ZERO, ctx.config());
        }),
        DEDUCT_PER_MISS("deduct_per_miss", ctx -> {
            BigDecimal deductionPer = getBigDecimal(ctx.config(), "miss_deduct_per", BigDecimal.ZERO);
            BigDecimal deduction = deductionPer.multiply(BigDecimal.valueOf(ctx.getMissedCorrectCount()));
            BigDecimal raw = ctx.getFullScore().subtract(deduction);
            return clampScore(raw, ctx.getFullScore(), getBigDecimal(ctx.config(), "min_score", BigDecimal.ZERO), ctx.config());
        }),
        DEDUCT_PER_WRONG("deduct_per_wrong", ctx -> {
            BigDecimal deductionPer = getBigDecimal(ctx.config(), "wrong_deduct_per", BigDecimal.ZERO);
            BigDecimal deduction = deductionPer.multiply(BigDecimal.valueOf(ctx.getIncorrectlySelectedCount()));
            BigDecimal raw = ctx.getFullScore().subtract(deduction);
            return clampScore(raw, ctx.getFullScore(), getBigDecimal(ctx.config(), "min_score", BigDecimal.ZERO), ctx.config());
        }),
        CUSTOM("custom", ctx -> {
            Map<String, Object> config = ctx.config();
            String formula = getString(config, "fomula", getString(config, "formula", null));
            if (formula == null || formula.isBlank()) {
                throw new IllegalArgumentException("Custom strategy requires fomula/formula in multiple_strategy_conf");
            }
            Map<String, BigDecimal> variables = new HashMap<>();
            variables.put("score", ctx.getFullScore());
            variables.put("count", BigDecimal.valueOf(ctx.getTotalOptionCount()));
            variables.put("correctly_selected_count", BigDecimal.valueOf(ctx.getCorrectlySelectedCount()));
            variables.put("missed_correct_count", BigDecimal.valueOf(ctx.getMissedCorrectCount()));
            variables.put("incorrectly_selected_count", BigDecimal.valueOf(ctx.getIncorrectlySelectedCount()));
            variables.put("correctly_ignored_count", BigDecimal.valueOf(ctx.resolvedCorrectlyIgnoredCount()));
            BigDecimal evaluated = evaluateFormula(formula, variables);
            BigDecimal scaled = applyScale(evaluated, config);
            return clampScore(scaled, ctx.getFullScore(), getBigDecimal(config, "min_score", null), config);
        });

        private final String code;
        private final ScoreStrategy strategy;

        StrategyType(String code, ScoreStrategy strategy) {
            this.code = code;
            this.strategy = strategy;
        }

        public BigDecimal apply(MultipleChoiceStrategyContext context) {
            return strategy.apply(context);
        }

        public static StrategyType fromCode(String code) {
            if (code == null || code.isBlank()) {
                throw new IllegalArgumentException("multiple_strategy code must not be blank");
            }
            for (StrategyType value : values()) {
                if (value.code.equalsIgnoreCase(code)) {
                    return value;
                }
            }
            throw new IllegalArgumentException("Unsupported multiple_strategy code: " + code);
        }
    }

    @FunctionalInterface
    private interface ScoreStrategy {
        BigDecimal apply(MultipleChoiceStrategyContext context);
    }

    @Getter
    @Builder
    public static class MultipleChoiceStrategyContext {
        private final BigDecimal fullScore;
        private final int totalOptionCount;
        private final int correctlySelectedCount;
        private final int missedCorrectCount;
        private final int incorrectlySelectedCount;
        private final Integer correctlyIgnoredCount;
        @Builder.Default
        private final Map<String, Object> strategyConfig = Collections.emptyMap();

        public int resolvedCorrectlyIgnoredCount() {
            if (correctlyIgnoredCount != null) {
                return correctlyIgnoredCount;
            }
            int correctOptionCount = correctlySelectedCount + missedCorrectCount;
            int incorrectOptionCount = Math.max(0, totalOptionCount - correctOptionCount);
            return Math.max(0, incorrectOptionCount - incorrectlySelectedCount);
        }

        private Map<String, Object> config() {
            return strategyConfig == null ? Collections.emptyMap() : strategyConfig;
        }
    }

    private static BigDecimal applyScale(BigDecimal value, Map<String, Object> config) {
        Integer scale = getInteger(config, "scale_precision", null);
        if (scale == null) {
            return value;
        }
        RoundingMode roundingMode = getRoundingMode(config.get("rounding"), RoundingMode.HALF_UP);
        return value.setScale(scale, roundingMode);
    }

    private static BigDecimal clampScore(BigDecimal value, BigDecimal defaultMax, BigDecimal defaultMin, Map<String, Object> config) {
        BigDecimal max = getBigDecimal(config, "max_score", defaultMax);
        BigDecimal min = getBigDecimal(config, "min_score", defaultMin);
        if (max != null && value.compareTo(max) > 0) {
            value = max;
        }
        if (min != null && value.compareTo(min) < 0) {
            value = min;
        }
        return value;
    }

    private static BigDecimal getBigDecimal(Map<String, Object> config, String key, BigDecimal defaultValue) {
        Object value = config.get(key);
        if (value == null) {
            return defaultValue;
        }
        if (value instanceof BigDecimal) {
            return (BigDecimal) value;
        }
        if (value instanceof Number) {
            return BigDecimal.valueOf(((Number) value).doubleValue());
        }
        if (value instanceof String str && !str.isBlank()) {
            return new BigDecimal(str);
        }
        return defaultValue;
    }

    private static Integer getInteger(Map<String, Object> config, String key, Integer defaultValue) {
        Object value = config.get(key);
        if (value == null) {
            return defaultValue;
        }
        if (value instanceof Integer) {
            return (Integer) value;
        }
        if (value instanceof Number) {
            return ((Number) value).intValue();
        }
        if (value instanceof String str && !str.isBlank()) {
            return Integer.parseInt(str);
        }
        return defaultValue;
    }

    private static String getString(Map<String, Object> config, String key, String defaultValue) {
        Object value = config.get(key);
        if (value == null) {
            return defaultValue;
        }
        String str = String.valueOf(value);
        return str.isBlank() ? defaultValue : str;
    }

    private static RoundingMode getRoundingMode(Object raw, RoundingMode defaultMode) {
        if (raw == null) {
            return defaultMode;
        }
        String val = raw.toString().trim().toLowerCase();
        return switch (val) {
            case "ceil", "ceiling" -> RoundingMode.CEILING;
            case "floor" -> RoundingMode.FLOOR;
            case "round", "half_up" -> RoundingMode.HALF_UP;
            case "down" -> RoundingMode.DOWN;
            case "up" -> RoundingMode.UP;
            default -> defaultMode;
        };
    }

    private static BigDecimal evaluateFormula(String formula, Map<String, BigDecimal> variables) {
        List<String> postfix = toPostfix(formula);
        Deque<BigDecimal> stack = new ArrayDeque<>();
        for (String token : postfix) {
            if (token.length() == 1 && isOperator(token.charAt(0))) {
                if (stack.size() < 2) {
                    throw new IllegalArgumentException("Invalid expression: " + formula);
                }
                BigDecimal right = stack.pop();
                BigDecimal left = stack.pop();
                stack.push(applyOperator(token.charAt(0), left, right));
            } else {
                stack.push(resolveValue(token, variables));
            }
        }
        if (stack.size() != 1) {
            throw new IllegalArgumentException("Invalid expression: " + formula);
        }
        return stack.pop();
    }

    private static List<String> toPostfix(String formula) {
        List<String> output = new ArrayList<>();
        Deque<Character> ops = new ArrayDeque<>();
        boolean expectOperand = true;
        int idx = 0;
        while (idx < formula.length()) {
            char ch = formula.charAt(idx);
            if (Character.isWhitespace(ch)) {
                idx++;
                continue;
            }
            if (Character.isDigit(ch) || ch == '.') {
                int start = idx;
                idx++;
                while (idx < formula.length()) {
                    char next = formula.charAt(idx);
                    if (Character.isDigit(next) || next == '.') {
                        idx++;
                    } else {
                        break;
                    }
                }
                output.add(formula.substring(start, idx));
                expectOperand = false;
                continue;
            }
            if (Character.isLetter(ch) || ch == '_') {
                int start = idx;
                idx++;
                while (idx < formula.length()) {
                    char next = formula.charAt(idx);
                    if (Character.isLetterOrDigit(next) || next == '_') {
                        idx++;
                    } else {
                        break;
                    }
                }
                output.add(formula.substring(start, idx));
                expectOperand = false;
                continue;
            }
            if (ch == '(') {
                ops.push(ch);
                idx++;
                expectOperand = true;
                continue;
            }
            if (ch == ')') {
                while (!ops.isEmpty() && ops.peek() != '(') {
                    output.add(String.valueOf(ops.pop()));
                }
                if (ops.isEmpty() || ops.pop() != '(') {
                    throw new IllegalArgumentException("Mismatched parentheses in formula: " + formula);
                }
                idx++;
                expectOperand = false;
                continue;
            }
            if (isOperator(ch)) {
                if (expectOperand) {
                    if (ch == '-') {
                        output.add("0");
                    } else {
                        throw new IllegalArgumentException("Unexpected operator position for '" + ch + "'");
                    }
                } else {
                    expectOperand = true;
                }
                while (!ops.isEmpty() && isOperator(ops.peek()) && precedence(ops.peek()) >= precedence(ch)) {
                    output.add(String.valueOf(ops.pop()));
                }
                ops.push(ch);
                idx++;
                continue;
            }
            throw new IllegalArgumentException("Unsupported character in formula: " + ch);
        }
        while (!ops.isEmpty()) {
            char op = ops.pop();
            if (op == '(') {
                throw new IllegalArgumentException("Mismatched parentheses in formula: " + formula);
            }
            output.add(String.valueOf(op));
        }
        return output;
    }

    private static BigDecimal resolveValue(String token, Map<String, BigDecimal> variables) {
        if (isNumeric(token)) {
            return new BigDecimal(token);
        }
        BigDecimal value = variables.get(token);
        if (value == null) {
            throw new IllegalArgumentException("Unknown variable: " + token);
        }
        return value;
    }

    private static BigDecimal applyOperator(char operator, BigDecimal left, BigDecimal right) {
        return switch (operator) {
            case '+' -> left.add(right);
            case '-' -> left.subtract(right);
            case '*' -> left.multiply(right);
            case '/' -> {
                if (BigDecimal.ZERO.compareTo(right) == 0) {
                    throw new ArithmeticException("Division by zero in custom formula");
                }
                yield left.divide(right, DEFAULT_PROPORTIONAL_SCALE, RoundingMode.HALF_UP);
            }
            default -> throw new IllegalArgumentException("Unsupported operator: " + operator);
        };
    }

    private static boolean isNumeric(String token) {
        if (token == null || token.isBlank()) {
            return false;
        }
        char first = token.charAt(0);
        if (first != '-' && first != '+' && !Character.isDigit(first)) {
            return false;
        }
        for (int i = 1; i < token.length(); i++) {
            char ch = token.charAt(i);
            if (!Character.isDigit(ch) && ch != '.') {
                return false;
            }
        }
        return true;
    }

    private static boolean isOperator(char ch) {
        return ch == '+' || ch == '-' || ch == '*' || ch == '/';
    }

    private static int precedence(char operator) {
        return (operator == '*' || operator == '/') ? 2 : 1;
    }
}
