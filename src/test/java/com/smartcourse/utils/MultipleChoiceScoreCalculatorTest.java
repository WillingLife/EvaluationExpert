package com.smartcourse.utils;

import com.smartcourse.utils.MultipleChoiceScoreCalculator.MultipleChoiceStrategyContext;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class MultipleChoiceScoreCalculatorTest {

    @Test
    void allOrNothingNeedsPerfectAnswer() {
        MultipleChoiceStrategyContext perfect = context("5", 4, 4, 0, 0, Collections.emptyMap());
        assertEquals(new BigDecimal("5"),
            MultipleChoiceScoreCalculator.calculate("all_or_nothing_if_miss", perfect));

        MultipleChoiceStrategyContext missedOne = context("5", 4, 3, 1, 0, Collections.emptyMap());
        assertEquals(BigDecimal.ZERO,
            MultipleChoiceScoreCalculator.calculate("all_or_nothing_if_miss", missedOne));
    }

    @Test
    void fixedOnMissFallsBackWhenOnlyMissed() {
        Map<String, Object> config = Map.of("score_on_any_miss", 2);
        MultipleChoiceStrategyContext context = context("5", 4, 3, 1, 0, config);
        assertEquals(new BigDecimal("2"),
            MultipleChoiceScoreCalculator.calculate("fixed_on_miss", context));

        MultipleChoiceStrategyContext wrong = context("5", 4, 4, 0, 1, config);
        assertEquals(BigDecimal.ZERO,
            MultipleChoiceScoreCalculator.calculate("fixed_on_miss", wrong));
    }

    @Test
    void proportionalRespectsRoundingAndClamp() {
        Map<String, Object> config = Map.of(
            "rounding", "floor",
            "scale_precision", 1,
            "max_score", "7"
        );
        MultipleChoiceStrategyContext context = context("10", 4, 3, 1, 0, config);
        assertEquals(new BigDecimal("7"),
            MultipleChoiceScoreCalculator.calculate("proportional", context));
    }

    @Test
    void deductPerMissHonorsMinimumScore() {
        Map<String, Object> config = Map.of(
            "miss_deduct_per", 3,
            "min_score", 2
        );
        MultipleChoiceStrategyContext context = context("10", 5, 0, 5, 0, config);
        assertEquals(new BigDecimal("2"),
            MultipleChoiceScoreCalculator.calculate("deduct_per_miss", context));
    }

    @Test
    void deductPerWrongSubtractsPerIncorrectChoice() {
        Map<String, Object> config = Map.of(
            "wrong_deduct_per", "1.5",
            "min_score", 0
        );
        MultipleChoiceStrategyContext context = context("5", 5, 4, 0, 1, config);
        assertEquals(new BigDecimal("3.5"),
            MultipleChoiceScoreCalculator.calculate("deduct_per_wrong", context));
    }

    @Test
    void customFormulaEvaluatesExpression() {
        Map<String, Object> config = Map.of(
            "fomula", "score - incorrectly_selected_count * 2 - missed_correct_count",
            "scale_precision", 2,
            "rounding", "round"
        );
        MultipleChoiceStrategyContext context = MultipleChoiceStrategyContext.builder()
            .fullScore(new BigDecimal("12"))
            .totalOptionCount(6)
            .correctlySelectedCount(3)
            .missedCorrectCount(1)
            .incorrectlySelectedCount(2)
            .strategyConfig(config)
            .build();
        assertEquals(new BigDecimal("7.00"),
            MultipleChoiceScoreCalculator.calculate("custom", context));
    }

    @Test
    void customFormulaRequiresExpression() {
        MultipleChoiceStrategyContext context = context("5", 4, 2, 2, 0, Collections.emptyMap());
        assertThrows(IllegalArgumentException.class,
            () -> MultipleChoiceScoreCalculator.calculate("custom", context));
    }

    private MultipleChoiceStrategyContext context(String fullScore,
                                                  int totalOptionCount,
                                                  int correctlySelectedCount,
                                                  int missedCorrectCount,
                                                  int incorrectlySelectedCount,
                                                  Map<String, Object> config) {
        return MultipleChoiceStrategyContext.builder()
            .fullScore(new BigDecimal(fullScore))
            .totalOptionCount(totalOptionCount)
            .correctlySelectedCount(correctlySelectedCount)
            .missedCorrectCount(missedCorrectCount)
            .incorrectlySelectedCount(incorrectlySelectedCount)
            .strategyConfig(config)
            .build();
    }
}
