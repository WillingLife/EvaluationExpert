# MultipleChoiceScoreCalculator 使用指南

`MultipleChoiceScoreCalculator` 位于 `src/main/java/com/smartcourse/utils/MultipleChoiceScoreCalculator.java`，封装了考卷多选题的所有计分策略（全对得满分、漏选固定分、按比例计分、按漏选扣分、按错选扣分、自定义公式）。对接端只需堆积题目统计数据和策略配置，就可以统一调用该工具获取得分。

## 1. 典型调用流程

1. **准备统计数据**  
   - `fullScore`：题目在本卷的满分（`BigDecimal`）。  
   - `totalOptionCount`：题目的选项总数。  
   - `correctlySelectedCount`：学生勾选的选项中属于正确答案的数量。  
   - `missedCorrectCount`：正确答案中学生没有勾选的数量。  
   - `incorrectlySelectedCount`：学生勾选的错误选项数量。  
   - （可选）`correctlyIgnoredCount`：错误选项中学生没有勾选的数量。如果不传，工具会通过 `totalOptionCount` 自动推导。  
2. **构建 `MultipleChoiceStrategyContext`**  
   使用 Lombok `@Builder` 创建上下文对象，同时把策略配置 JSON 映射为 Java `Map` 传入 `strategyConfig`。  
3. **调用 `MultipleChoiceScoreCalculator.calculate`**  
   传入 `multiple_strategy` 对应的编码（或 `StrategyType` 枚举值），返回的 `BigDecimal` 即为该学生在此题的得分。工具内部会根据配置自动做四舍五入、上下限裁剪等逻辑。

## 2. 示例：按错选扣分策略

```java
import com.smartcourse.utils.MultipleChoiceScoreCalculator;
import com.smartcourse.utils.MultipleChoiceScoreCalculator.MultipleChoiceStrategyContext;

import java.math.BigDecimal;
import java.util.Map;

public class ScoreDemo {

    public BigDecimal calculate() {
        Map<String, Object> config = Map.of(
            "wrong_deduct_per", "1.5", // 每错选一个扣 1.5 分
            "min_score", 0            // 不低于 0 分
        );

        MultipleChoiceStrategyContext context = MultipleChoiceStrategyContext.builder()
            .fullScore(new BigDecimal("5"))
            .totalOptionCount(4)
            .correctlySelectedCount(3)
            .missedCorrectCount(0)
            .incorrectlySelectedCount(1)
            .strategyConfig(config)
            .build();

        return MultipleChoiceScoreCalculator.calculate("deduct_per_wrong", context);
    }
}
```

## 3. 自定义公式示例

```java
Map<String, Object> config = Map.of(
    "fomula", "score - incorrectly_selected_count * 2 - missed_correct_count",
    "rounding", "round",
    "scale_precision", 2,
    "min_score", 0,
    "max_score", 10
);

MultipleChoiceStrategyContext ctx = MultipleChoiceStrategyContext.builder()
    .fullScore(new BigDecimal("10"))
    .totalOptionCount(6)
    .correctlySelectedCount(4)
    .missedCorrectCount(1)
    .incorrectlySelectedCount(1)
    .strategyConfig(config)
    .build();

BigDecimal finalScore = MultipleChoiceScoreCalculator.calculate("custom", ctx);
```

工具会先解析 `fomula`，把 `score`、`missed_correct_count` 等变量替换成上下文中的数值，再按照 `rounding` 和 `scale_precision` 进行保留小数，最后按 `min_score`、`max_score` 控制范围。

## 4. 常见问题

| 场景 | 说明 |
| --- | --- |
| `multiple_strategy` 为空 | `calculate` 会抛出 `IllegalArgumentException`，请在调用前兜底。 |
| 自定义公式变量缺失 | 表达式中包含的变量必须出现在上下文中，否则会抛出异常。 |
| 精度设置 | 若未配置 `scale_precision`，结果保留原始精度；比例计算和除法默认使用 8 位精度后再裁剪。 |

> 只要把题目统计和 `multiple_strategy_conf` 按上述方式封装，任何地方都能复用相同的评分逻辑，避免在 Controller/Service 层重复写分支。 
